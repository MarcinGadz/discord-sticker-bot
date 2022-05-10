import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;

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
            JDA jda = JDABuilder.createDefault(properties.getProperty("BOT_TOKEN")).build();
        } catch (IOException ex) {
            throw new Error("Configuration file not found! Exiting...");
        } catch (LoginException ex) {
            throw new Error("Wrong API key. Exiting...");
        }

    }

}
