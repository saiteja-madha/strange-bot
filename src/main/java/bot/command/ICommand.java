package bot.command;

import bot.utils.BotUtils;
import me.duncte123.botcommons.messaging.EmbedUtils;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public abstract class ICommand {

    protected String name = "";
    protected List<String> aliases = Collections.emptyList();
    protected String help = "No help available";
    protected String usage = "";
    protected int minArgsCount = 0;
    protected int maxCharCount = 3000;
    protected Permission[] userPermissions = new Permission[0];
    protected Permission[] botPermissions = new Permission[0];
    protected int cooldown = 0;
    protected CommandCategory category = CommandCategory.UNLISTED;

    public void run(@Nonnull CommandContext ctx) {
        final TextChannel channel = ctx.getChannel();

        if (!ctx.getSelfMember().hasPermission(channel, Permission.MESSAGE_WRITE))
            return;

        if (this.userPermissions.length > 0 && !ctx.getMember().hasPermission(channel, this.userPermissions)) {
            final String permissionsWord = "permission" + (this.userPermissions.length > 1 ? "s" : "");

            ctx.reply("You need the `" + parsePerms(this.userPermissions) + "` " + permissionsWord
                    + " for this command\nPlease contact your server administrator if this is incorrect.");
            return;
        }

        if (this.botPermissions.length > 0 && !ctx.getSelfMember().hasPermission(channel, this.botPermissions)) {
            final String permissionsWord = "permission" + (this.botPermissions.length > 1 ? "s" : "");
            ctx.reply("I need the `" + parsePerms(this.botPermissions) + "` " + permissionsWord
                    + " for this command to work\nPlease contact your server administrator about this.");
            return;
        }

        if (this.minArgsCount > 0 && (ctx.getArgs().isEmpty() || ctx.getArgs().size() < this.minArgsCount)) {
            ctx.reply("Missing command arguments");
            return;
        }

        if (ctx.getArgsJoined().length() > this.maxCharCount) {
            ctx.reply("Maximum character count for arguments cannot exceed `" + this.maxCharCount
                    + "` characters");
            return;
        }

        if (this.cooldown > 0) {
            String key = getCooldownKey(ctx);
            int remaining = ctx.getCmdHandler().getRemainingCooldown(key);
            if (remaining > 0) {
                this.sendCoolDown(ctx.getEvent(), remaining);
                return;
            } else ctx.getCmdHandler().applyCooldown(key, cooldown);
        }

        ctx.getEvent().getChannel().sendTyping().queue();
        handle(ctx);

    }


    public abstract void handle(@Nonnull CommandContext ctx);

    public void sendUsage(@Nonnull CommandContext ctx) {
        ctx.reply(ctx.getPrefix() + this.name + " " + this.usage);
    }

    protected String parsePerms(Permission[] perms) {
        return Arrays.stream(perms).map(Permission::getName).collect(Collectors.joining("`, `"));
    }

    public String getName() {
        return this.name;
    }

    public List<String> getAliases() {
        return aliases;
    }

    private void sendCoolDown(GuildMessageReceivedEvent event, int seconds) {
        BotUtils.sendMsg(
                event.getChannel(),
                EmbedUtils.defaultEmbed().setAuthor("Kindly wait " + seconds + " seconds before using this command").build(),
                5
        );
    }

    private String getCooldownKey(CommandContext ctx) {
        return this.name + "|" + "U:" + ctx.getAuthor().getId();
    }

}