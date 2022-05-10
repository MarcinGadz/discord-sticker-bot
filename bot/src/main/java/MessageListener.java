
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

public class MessageListener extends ListenerAdapter {
    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        System.out.println(event.getMessage().getContentRaw());
        if (event.getAuthor().getId().equals("973626271974187089")) return;
        event.getMessage().reply("https://media.discordapp.net/attachments/973635712207441982/973641940421718066/unknown.png \n https://i.ytimg.com/vi/xszh8hfMGu8/hq720.jpg?sqp=-oaymwEcCNAFEJQDSFXyq4qpAw4IARUAAIhCGAFwAcABBg==&rs=AOn4CLBBSWfYMW0lxRIObFE7nQE9CcXGJA").queue();
        System.out.println("USER ID: " + event.getAuthor().getId());
    }

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        super.onSlashCommandInteraction(event);
        if (event.getName().equals("komenda")) {
            event.reply("lulz").queue();
        }
    }
}
