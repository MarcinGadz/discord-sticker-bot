import net.dv8tion.jda.api.entities.Message;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class ImageService {
    public static void uploadImage(Message message) throws Exception {
        try {
            String imageURL = message.getAttachments().get(0).getUrl();

            HttpClient httpClient = HttpClient.newHttpClient();
            HttpRequest requestImage = HttpRequest.newBuilder()
                    .uri(URI.create(imageURL))
                    .build();

            // body of the upload image request
            HttpResponse<byte[]> response = httpClient.send(requestImage, HttpResponse.BodyHandlers.ofByteArray());

            //send request to api
        } catch (Exception e) {
            throw new Exception("Nie udało się pobrać zdjęcia z ostatniej wiadomości");
        }
    }
}
