package commands;

import dto.ImageDto;
import exceptions.BaseException;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import services.ImageService;

import java.awt.*;
import java.util.List;
import java.util.Objects;

public class ImageCommands {

    public static void uploadImage(SlashCommandInteractionEvent event) {
        try {
            String userID = event.getUser().getId();

            String imageName = Objects.requireNonNull(event.getOption("name")).getAsString();
            String imageURL = Objects.requireNonNull(event.getOption("image")).getAsAttachment().getUrl();

            ImageService.uploadSticker(imageURL, imageName, userID);
            event.reply("Successfully uploaded sticker named: " + imageName).queue();
        } catch (NullPointerException e) {
            event.reply("Something went wrong").queue();
        } catch (BaseException e) {
            event.reply(e.getMessage()).queue();
        }
    }

    public static void listImages(SlashCommandInteractionEvent event) {
        try {
            String userID = event.getUser().getId();

            List<ImageDto> images =  ImageService.getStickersForUser(userID);
            StringBuilder content = new StringBuilder();
            for (ImageDto image : images) {
                content.append(image.getName()).append("\n");
                System.out.println(image.getName());
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

    public static void sendImage(SlashCommandInteractionEvent event) {
        try {
            String userID = event.getUser().getId();

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
