
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.RestAction;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Objects;

public class MessageListener extends ListenerAdapter {
//    @Override
//    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
//        System.out.println(event.getMessage().getContentRaw());
//        if (event.getAuthor().getId().equals("973626271974187089")) return;
//        event.getMessage().reply("https://media.discordapp.net/attachments/973635712207441982/973641940421718066/unknown.png \n https://i.ytimg.com/vi/xszh8hfMGu8/hq720.jpg?sqp=-oaymwEcCNAFEJQDSFXyq4qpAw4IARUAAIhCGAFwAcABBg==&rs=AOn4CLBBSWfYMW0lxRIObFE7nQE9CcXGJA").queue();
//        System.out.println("USER ID: " + event.getAuthor().getId());
//    }

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        super.onSlashCommandInteraction(event);
        if (event.getName().equals("komenda")) {
            event.reply("lulz").queue();
        }

        if (event.getName().equals("upload")) {
            // pobierz ostatnią wiadomość z czatu
            Message message = event.getChannel()
                    .getHistory()
                    .retrievePast(1)
                    .map(messages -> messages.get(0)).complete();

            try {
                ImageService.uploadImage(message);
            } catch (Exception e) {
                event.reply(e.getMessage()).queue();
            }
        }
    }
}