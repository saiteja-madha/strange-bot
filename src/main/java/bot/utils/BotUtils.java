package bot.utils;

import me.duncte123.botcommons.messaging.MessageUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;

import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public class BotUtils {

    public static void sendMsg(TextChannel channel, String message) {
        channel.sendMessage(message).queue(null, (error) -> { /* Ignore */ });
    }

    public static void sendMsg(TextChannel channel, String message, int time) {
        channel.sendMessage(message)
                .queue((m) -> m.delete().queueAfter(time, TimeUnit.SECONDS, null, (error) -> { /* Ignore */ })
                        , null);
    }

    public static void sendMsg(TextChannel channel, MessageEmbed embed) {
        channel.sendMessage(embed).queue(null, (error) -> { /* Ignore */ });
    }

    public static void sendMsg(TextChannel channel, MessageEmbed embed, int time) {
        channel.sendMessage(embed)
                .queue((m) -> m.delete().queueAfter(time, TimeUnit.SECONDS, null, (error) -> { /* Ignore */ })
                        , null);
    }

    public static void sendDM(User user, EmbedBuilder embed, Consumer<? super Object> success, Consumer<? super Throwable> error) {
        if (user == null)
            return;

        user.openPrivateChannel()
                .flatMap((channel) -> channel.sendMessage(embed.build()))
                .queue(success, error);

    }

    public static void sendDM(User user, String message) {
        if (user == null)
            return;

        user.openPrivateChannel()
                .flatMap((channel) -> channel.sendMessage(message))
                .queue(null, (error) -> { /* Ignore */ });

    }

    public static void sendDM(User user, MessageEmbed embed) {
        if (user == null)
            return;

        user.openPrivateChannel()
                .flatMap((channel) -> channel.sendMessage(embed))
                .queue(null, (error) -> { /* Ignore */ });

    }

    public static void sendSuccessWithMessage(Message message, String content) {
        MessageUtils.sendSuccessWithMessage(message, content);
    }

    public static void sendErrorWithMessage(Message message, String content) {
        MessageUtils.sendErrorWithMessage(message, content);
    }
}
