
import exceptions.BaseException;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class MessageListener extends ListenerAdapter {
    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        super.onSlashCommandInteraction(event);
        if (event.getName().equals("komenda")) {
            event.reply("lulz").queue();
        }

        if (event.getName().equals("upload")) {
            try {
                String userID = event.getUser().getId();
                String imageName = Objects.requireNonNull(event.getOption("image")).getAsString();

                System.out.println(userID);

                Message message = event.getChannel()
                        .getHistory()
                        .retrievePast(1)
                        .map(messages -> messages.get(0)).complete();

                ImageService.uploadImage(message, imageName, userID);
                event.reply("Successfully uploaded sticker named: " + imageName).queue();
            } catch (NullPointerException e) {
                event.reply("Something went wrong").queue();
            } catch (BaseException e) {
                event.reply(e.getMessage()).queue();
            }
        }
    }
}
