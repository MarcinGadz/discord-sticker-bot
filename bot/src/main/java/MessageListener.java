import exceptions.BaseException;
import exceptions.ExceptionMessages;
import interactions.ImageInteractions;
import interactions.PaginationInteractions;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class MessageListener extends ListenerAdapter {
    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        super.onSlashCommandInteraction(event);
        String commandName = event.getName();

        try {
            switch (commandName) {
                case "upload" -> ImageInteractions.uploadImage(event);
                case "list" -> ImageInteractions.listImages(event);
                case "send" -> ImageInteractions.sendImage(event);
                case "remove" -> ImageInteractions.removeImage(event);
            }
        } catch (BaseException exception) {
            event.reply(exception.getMessage()).setEphemeral(true).queue();
        }

    }

    @Override
    public void onButtonInteraction(@NotNull ButtonInteractionEvent event) {
        String buttonId = event.getButton().getId();

        try {

        switch (Objects.requireNonNull(buttonId)) {
            case "list/previous" -> PaginationInteractions.getPreviousPage(event);
            case "list/next" -> PaginationInteractions.getNextPage(event);
        }
        } catch (BaseException e) {
            event.getInteraction().reply(e.getMessage()).setEphemeral(true).queue();
        } catch (NullPointerException e) {
            event.getInteraction().reply(ExceptionMessages.UNEXPECTED).setEphemeral(true).queue();
        }
    }
}
