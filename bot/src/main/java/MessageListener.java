
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
                String imageName = Objects.requireNonNull(event.getOption("name")).getAsString();
                String imageURL = Objects.requireNonNull(event.getOption("image")).getAsAttachment().getUrl();

                System.out.println(userID);

                ImageService.uploadImage(imageURL, imageName, userID);
                event.reply("Successfully uploaded sticker named: " + imageName).queue();
            } catch (NullPointerException e) {
                event.reply("Something went wrong").queue();
            } catch (BaseException e) {
                event.reply(e.getMessage()).queue();
            }
        }
    }
}
