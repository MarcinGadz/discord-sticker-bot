package interactions;

import dto.ImageDto;
import exceptions.BaseException;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import services.ImageService;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ImageInteractions {

    public static void uploadImage(SlashCommandInteractionEvent event) {
        try {
            String userID = event.getUser().getId();

            String imageName = Objects.requireNonNull(event.getOption("name")).getAsString();
            String imageURL = Objects.requireNonNull(event.getOption("image")).getAsAttachment().getUrl();

            ImageService.uploadImage(imageURL, imageName, userID);
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

            List<ImageDto> images = ImageService.getImagesForUser(userID, null);
            List<MessageEmbed> embeds = ImageInteractions.embedImages(images);

            List<String> history = new ArrayList<>();
            history.add(images.get(images.size() - 1).getName());
            List<Button> buttons = PaginationInteractions.getButtons(history, false);

            event.replyEmbeds(embeds).addActionRow(buttons).setEphemeral(false).queue();
        } catch (BaseException e) {
            throw new RuntimeException(e);
        }
    }

    public static void sendImage(SlashCommandInteractionEvent event) {
        try {
            String userID = event.getUser().getId();

            String stickerName = Objects.requireNonNull(event.getOption("name")).getAsString();
            ImageDto image = ImageService.getImage(stickerName, userID);
            event.reply(image.getUrl()).queue();
        } catch (NullPointerException e) {
            event.reply("Something went wrong").queue();
        } catch (BaseException e) {
            event.reply(e.getMessage()).queue();
        }
    }

    public static List<MessageEmbed> embedImages (List<ImageDto> images) throws BaseException {
        List<MessageEmbed> embeds = new ArrayList<>();
        EmbedBuilder embedBuilder = new EmbedBuilder();

        for (ImageDto image: images) {
            embedBuilder.setTitle(image.getName()).setColor(new Color(154, 0, 215));
            embedBuilder.setImage(image.getUrl());
            embeds.add(embedBuilder.build());
        }

        return embeds;
    }

}
