
import interactions.ImageInteractions;
import interactions.PaginationInteractions;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

public class MessageListener extends ListenerAdapter {
    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        super.onSlashCommandInteraction(event);
        String commandName = event.getName();

        switch (commandName) {
            case "upload" -> ImageInteractions.uploadImage(event);
            case "list" -> ImageInteractions.listImages(event);
            case "send" -> ImageInteractions.sendImage(event);
        }
    }

    @Override
    public void onButtonInteraction(@NotNull ButtonInteractionEvent event) {
        String buttonId = event.getButton().getId();

        assert buttonId != null;
        switch (buttonId) {
            case "list/previous" -> PaginationInteractions.getPreviousPage(event);
            case "list/next" -> PaginationInteractions.getNextPage(event);
        }
    }
}
