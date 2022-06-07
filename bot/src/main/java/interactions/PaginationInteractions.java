package interactions;

import dto.ImageDto;
import exceptions.BaseException;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import services.ImageService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PaginationInteractions {

    public static List<Button> getButtons(List<String> history, boolean isLastPage) {
        List<Button> buttons = new ArrayList<>();

        if (history.size() == 1) {
            buttons.add(Button.primary("list/previous", "◀").asDisabled());
        } else {
            buttons.add(Button.primary("list/previous", "◀"));
        }

        buttons.add(Button.primary("current=" + String.join("/", history), String.valueOf(history.size())).asDisabled());

        if (isLastPage) {
            buttons.add(Button.primary("list/next", "▶").asDisabled());
        } else {
            buttons.add(Button.primary("list/next", "▶"));
        }

        return buttons;
    }

    public static List<String> getHistory(ButtonInteractionEvent event) {
        ActionRow row = event.getMessage().getActionRows().get(0);
        String id = row.getButtons().get(1).getId();
        String sub = id.substring(id.indexOf('=') + 1);
        return new ArrayList<>(Arrays.asList(sub.split("/")));
    }

    public static void getNextPage(ButtonInteractionEvent event) throws BaseException {
            String userID = event.getUser().getId();
            List<String> history = PaginationInteractions.getHistory(event);
            List<ImageDto> images = ImageService.getImagesForUser(userID, history.get(history.size() - 1));
            List<MessageEmbed> embeds = ImageInteractions.embedImages(images);

            try {
                String newElement = images.get(images.size() - 1).getName();
                history.add(newElement);

                List<Button> buttons = PaginationInteractions.getButtons(history, false);
                event.getInteraction().editMessageEmbeds(embeds).setActionRow(buttons).queue();
            } catch (IndexOutOfBoundsException e) {
                // If response is empty array (This was last page)
                List<Button> buttons = PaginationInteractions.getButtons(history, true);
                List<MessageEmbed> currentEmbeds = event.getInteraction().getMessage().getEmbeds();
                event.getInteraction().editMessageEmbeds(currentEmbeds).setActionRow(buttons).queue();
            }
    }


    public static void getPreviousPage(ButtonInteractionEvent event) {
        try {
            String userID = event.getUser().getId();
            List<String> history = PaginationInteractions.getHistory(event);
            history.remove(history.size() - 1);

            String startAfter;
            try {
                startAfter = history.get(history.size() - 2);
            } catch (IndexOutOfBoundsException e) {
                startAfter = "";
            }

            List<ImageDto> images = ImageService.getImagesForUser(userID, startAfter);
            List<MessageEmbed> embeds = ImageInteractions.embedImages(images);

            List<Button> buttons = PaginationInteractions.getButtons(history, false);
            event.getInteraction().editMessageEmbeds(embeds).setActionRow(buttons).queue();
        } catch (BaseException e) {
            event.reply("Something went wrong").queue();
        }
    }
}
