package interactions;

import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

import java.util.ArrayList;
import java.util.List;

public class PaginationInteractions {

    public static void listPaginated (List<MessageEmbed> embeds, SlashCommandInteractionEvent event) {
        List<Button> buttons = new ArrayList<>();

        buttons.add(Button.primary("list/previous", "◀").asDisabled());
        buttons.add(Button.primary("current", "1").asDisabled());
        buttons.add(Button.primary("list/next", "▶"));

        event.replyEmbeds(embeds).addActionRow(buttons).setEphemeral(false).queue();
    }

    public static void updateButtons (ButtonInteractionEvent event) {
        int currentPage = Integer.parseInt(
                event.getMessage().getActionRows().get(0).getButtons().get(1).getLabel());

        ActionRow oldRow = event.getMessage().getActionRows().get(0);

        List<net.dv8tion.jda.api.interactions.components.buttons.Button> buttons = new ArrayList<>(oldRow.getButtons());
        net.dv8tion.jda.api.interactions.components.buttons.Button button = Button.primary(
                "current", String.valueOf(currentPage + 1)).asDisabled();
        buttons.set(1, button);

        event.getInteraction().editMessageEmbeds().setActionRow(buttons).queue();
    }
}
