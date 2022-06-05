import exceptions.BaseException;
import net.dv8tion.jda.api.entities.Message;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
public class ImageService {
    private static final String apiURL = "http://localhost:8080/image";
    private static final String apiKey = "13cf17e6-0929-475b-bad0-1b7ab1bdca80";

    public static void uploadImage(Message message, String imageName, String userID) throws BaseException {

        byte[] imageData;

        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            String imageURL = message.getAttachments().get(0).getUrl();

            HttpGet getImageDataRequest = new HttpGet(imageURL);
            CloseableHttpResponse response = httpClient.execute(getImageDataRequest);
            HttpEntity entity = response.getEntity();
            imageData = EntityUtils.toByteArray(entity);
        } catch (Exception e) {
            throw new BaseException("Couldn't get image");
        }

        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            MultipartEntityBuilder mpBuilder = MultipartEntityBuilder.create();
            mpBuilder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
            mpBuilder.addBinaryBody("image", imageData, ContentType.DEFAULT_BINARY, imageName);

            HttpPost uploadRequest = new HttpPost(apiURL + "/" + userID);
            uploadRequest.setEntity(mpBuilder.build());
            uploadRequest.setHeader("x-api-key", apiKey);

            httpClient.execute(uploadRequest);
        } catch (Exception e) {
            throw new BaseException("Couldn't upload the image");
        }
    }
}
