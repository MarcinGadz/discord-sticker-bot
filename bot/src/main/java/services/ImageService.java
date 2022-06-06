package services;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import dto.ImageDto;
import exceptions.BaseException;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

public class ImageService {
    private static final String apiURL = "http://localhost:8080/image/";
    private static final String apiKey = "13cf17e6-0929-475b-bad0-1b7ab1bdca80";

    public static void uploadImage(String imageURL, String imageName, String userID) throws BaseException {
        byte[] imageData;

        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpGet request = new HttpGet(imageURL);
            CloseableHttpResponse response = httpClient.execute(request);
            HttpEntity entity = response.getEntity();
            imageData = EntityUtils.toByteArray(entity);
        } catch (Exception e) {
            throw new BaseException("Couldn't get image");
        }

        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            MultipartEntityBuilder mpBuilder = MultipartEntityBuilder.create();
            mpBuilder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
            mpBuilder.addBinaryBody("image", imageData, ContentType.DEFAULT_BINARY, imageName);

            HttpPost request = new HttpPost(apiURL + userID + "/" + imageName);
            request.setEntity(mpBuilder.build());
            request.setHeader("x-api-key", apiKey);

            CloseableHttpResponse res = httpClient.execute(request);
            if (res.getStatusLine().getStatusCode() != 200) {
                throw new BaseException("Couldn't upload");
            }

            res.close();
        } catch (IOException e) {
            throw new BaseException("Couldn't upload the image");
        }
    }

    public static List<ImageDto> getImagesForUser(String userID, String startAfter) throws BaseException {
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpGet request = new HttpGet(apiURL + userID);
            URI uri = new URIBuilder(request.getURI())
                    .addParameter("maxItems", "10")
                    .addParameter("startAfter", startAfter)
                    .build();
            request.setURI(uri);

            request.setHeader("x-api-key", apiKey);
            CloseableHttpResponse response = httpClient.execute(request);

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            response.getEntity().writeTo(out);

            Gson gson = new Gson();
            List<ImageDto> images = gson.fromJson(out.toString(), new TypeToken<ArrayList<ImageDto>>() {}.getType());
            out.close();
            response.close();

            return images;
        } catch (Exception e) {
            throw new BaseException("Couldn't get requested list");
        }
    }

    public static ImageDto getImage(String imageName, String userID) throws BaseException {
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpGet request = new HttpGet(apiURL + userID + "/" + imageName);
            request.setHeader("x-api-key", apiKey);
            CloseableHttpResponse response = httpClient.execute(request);

            if (response.getStatusLine().getStatusCode() != 200) {
                throw new BaseException("Couldn't find sticker with this name");
            }

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            response.getEntity().writeTo(out);

            Gson gson = new Gson();
            ImageDto image = gson.fromJson(out.toString(), ImageDto.class);
            out.close();
            response.close();

            return image;
        } catch (IOException e) {
            throw new BaseException("Couldn't get the image");
        }
    }
}
