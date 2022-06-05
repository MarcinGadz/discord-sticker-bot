import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import dto.ImageDto;
import exceptions.BaseException;
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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ImageService {
    private static final String apiURL = "http://localhost:8080/image/";
    private static final String apiKey = "13cf17e6-0929-475b-bad0-1b7ab1bdca80";

    public static void uploadImage(String imageURL, String imageName, String userID) throws BaseException {
        byte[] imageData;

        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
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

            HttpPost uploadRequest = new HttpPost(apiURL + userID);
            uploadRequest.setEntity(mpBuilder.build());
            uploadRequest.setHeader("x-api-key", apiKey);

            CloseableHttpResponse res = httpClient.execute(uploadRequest);
            if (res.getStatusLine().getStatusCode() != 200) {
                // TODO: obsługa wyjątków
                throw new BaseException("Couldn't upload");
            }

            System.out.println(res.getStatusLine());
            res.close();
        } catch (IOException e) {
            throw new BaseException("Couldn't upload the image");
        }
    }

    public static List<ImageDto> getStickersForUser(String userID) throws BaseException {
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpGet request = new HttpGet(apiURL + userID);
            request.setHeader("x-api-key", apiKey);
            CloseableHttpResponse response = httpClient.execute(request);

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            response.getEntity().writeTo(out);

            Gson gson = new Gson();
            List<ImageDto> images = gson.fromJson(out.toString(), new TypeToken<ArrayList<ImageDto>>() {}.getType());
            out.close();

            return images;
        } catch (Exception e) {
            throw new BaseException("Couldn't get requested list");
        }
    }
}
