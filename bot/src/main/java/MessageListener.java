
import dto.ImageDto;
import exceptions.BaseException;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.List;
import java.util.Objects;

public class MessageListener extends ListenerAdapter {
    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        super.onSlashCommandInteraction(event);

        String userID = event.getUser().getId();

        if (event.getName().equals("upload")) {
            try {
                String imageName = Objects.requireNonNull(event.getOption("name")).getAsString();
                String imageURL = Objects.requireNonNull(event.getOption("image")).getAsAttachment().getUrl();

                System.out.println(userID);

                ImageService.uploadSticker(imageURL, imageName, userID);
                event.reply("Successfully uploaded sticker named: " + imageName).queue();
            } catch (NullPointerException e) {
                event.reply("Something went wrong").queue();
            } catch (BaseException e) {
                event.reply(e.getMessage()).queue();
            }
        }

        if (event.getName().equals("list")) {
            try {
                List<ImageDto> images =  ImageService.getStickersForUser(userID);
                StringBuilder content = new StringBuilder();
                for (ImageDto image : images) {
                    content.append(image.getName()).append("\n");
                }

                EmbedBuilder eb = new EmbedBuilder();
                eb.setTitle("Your stickers:");
                eb.setColor(new Color(15, 150, 210));
                eb.setDescription(content);

                event.replyEmbeds(eb.build()).queue();
            } catch (BaseException e) {
                event.reply(e.getMessage()).queue();
            }
        }

        if (event.getName().equals("send")) {
            try {
                String stickerName = Objects.requireNonNull(event.getOption("name")).getAsString();
                ImageDto image = ImageService.getSticker(stickerName, userID);
                event.reply(image.getUrl()).queue();
            } catch (NullPointerException e) {
                event.reply("Something went wrong").queue();
            } catch (BaseException e) {
                event.reply(e.getMessage()).queue();
            }
        }
    }
}
