import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.interactions.commands.OptionType;

import javax.security.auth.login.LoginException;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class App {

    public static void main(String[] args) {

        String rootPath = System.getProperty("user.dir");
        Properties properties = new Properties();

        try {
            properties.load(new FileInputStream(rootPath + "/app.properties"));
            JDA jda = JDABuilder.createDefault(properties.getProperty("BOT_TOKEN"))
                    .setActivity(Activity.playing("jpł"))
                    .build();

            jda.addEventListener(new MessageListener());
            jda.getGuildById("973627370726633532");

            jda.upsertCommand("upload", "Upload new sticker")
                    .addOption(OptionType.ATTACHMENT, "image", "image to upload", true)
                    .addOption(OptionType.STRING, "name", "sticker name", true)
                    .queue();
            jda.upsertCommand("send", "Send selected sticker")
                    .addOption(OptionType.STRING, "name", "sticker name", true)
                    .queue();
            jda.upsertCommand("remove", "Remove selected sticker")
                    .addOption(OptionType.STRING, "name", "sticker name", true)
                    .queue();
            jda.upsertCommand("list", "List all your sticker names").queue();

            jda.awaitReady();

        } catch (IOException ex) {
            throw new Error("Configuration file not found! Exiting...");
        } catch (LoginException ex) {
            throw new Error("Wrong API key. Exiting...");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
