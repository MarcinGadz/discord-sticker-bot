
import commands.ImageCommands;
import dto.ImageDto;
import exceptions.BaseException;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import org.jetbrains.annotations.NotNull;
import services.ImageService;

import java.awt.*;
import java.util.*;
import java.util.List;

public class MessageListener extends ListenerAdapter {
    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        super.onSlashCommandInteraction(event);

        String userID = event.getUser().getId();
        String commandName = event.getName();

        switch (commandName) {
            case "upload" -> ImageCommands.uploadImage(event);
            case "list" -> ImageCommands.listImages(event);
            case "send" -> ImageCommands.sendImage(event);
        }

        if (event.getName().equals("test")) {
            try {
                List<ImageDto> images =  ImageService.getStickersForUser(userID); // zamiana na metode z paginacja

                List<MessageEmbed> embeds = new ArrayList<>();
                List<Button> buttons = new ArrayList<>();

                EmbedBuilder embedBuilder = new EmbedBuilder();

                for (ImageDto image:
                     images) {
                    embedBuilder.setTitle(image.getName()).setColor(new Color(154, 0, 215));
                    embedBuilder.setImage(image.getUrl());
                    embeds.add(embedBuilder.build());
                }

                buttons.add(Button.primary("list/previous", "◀").asDisabled());
                buttons.add(Button.primary("current", "1").asDisabled());
                buttons.add(Button.primary("list/next", "▶"));

                event.replyEmbeds(embeds).addActionRow(buttons).setEphemeral(false).queue();

                } catch (BaseException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public void onButtonInteraction(@NotNull ButtonInteractionEvent event) {
        String buttonId = event.getButton().getId();

        assert buttonId != null;
        if (buttonId.contains("list/next")) {
            String currentPage = event.getMessage().getActionRows().get(0).getButtons().get(1).getLabel();

            ActionRow oldRow = event.getMessage().getActionRows().get(0);

            List<Button> buttons = new ArrayList<>(oldRow.getButtons());
            Button button = Button.primary(
                    "current", String.valueOf(Integer.parseInt(currentPage) + 1)).asDisabled();
            buttons.set(1, button);

            event.getInteraction().editMessageEmbeds().setActionRow(buttons).queue();

        }
    }
}
