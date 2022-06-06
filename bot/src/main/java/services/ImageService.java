package services;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import dto.ImageDto;
import exceptions.BaseException;
import exceptions.ExceptionFactory;
import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
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
import java.net.URISyntaxException;
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
        } catch (IOException e) {
            throw ExceptionFactory.couldntDownloadImage();
        }

        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            MultipartEntityBuilder mpBuilder = MultipartEntityBuilder.create();
            mpBuilder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
            mpBuilder.addBinaryBody("image", imageData, ContentType.DEFAULT_BINARY, imageName);

            HttpPost request = new HttpPost(apiURL + userID + "/" + imageName);
            request.setEntity(mpBuilder.build());
            request.setHeader("x-api-key", apiKey);

            CloseableHttpResponse res = httpClient.execute(request);

            int statusCode = res.getStatusLine().getStatusCode();
            if (statusCode == 400) {
                throw ExceptionFactory.wrongFileTypeException();
            } else if (statusCode == 429) {
                throw ExceptionFactory.userLimitExceededException();
            } else if (statusCode != 200) {
                throw ExceptionFactory.unexpectedException();
            }
            res.close();
        } catch (IOException e) {
            throw ExceptionFactory.unexpectedException();
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
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode != 200) {
                throw ExceptionFactory.unexpectedException();
            }
            response.close();

            return images;
        } catch (URISyntaxException | IOException e) {
            throw ExceptionFactory.unexpectedException();
        }
    }

    public static ImageDto getImage(String imageName, String userID) throws BaseException {
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpGet request = new HttpGet(apiURL + userID + "/" + imageName);
            request.setHeader("x-api-key", apiKey);
            CloseableHttpResponse response = httpClient.execute(request);

            if (response.getStatusLine().getStatusCode() != 200) {
                throw ExceptionFactory.imageNotFoundException();
            }

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            response.getEntity().writeTo(out);

            Gson gson = new Gson();
            ImageDto image = gson.fromJson(out.toString(), ImageDto.class);
            out.close();
            response.close();

            return image;
        } catch (IOException e) {
            throw ExceptionFactory.unexpectedException();
        }
    }

    public static void removeImage(String imageName, String userID) throws BaseException {
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpDelete request =  new HttpDelete(apiURL + userID + "/" + imageName);
            request.setHeader("x-api-key", apiKey);
            CloseableHttpResponse response = httpClient.execute(request);

            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode == 404) {
                throw ExceptionFactory.imageNotFoundException();
            } else if (statusCode != 200) {
                throw ExceptionFactory.unexpectedException();
            }

            response.close();
        } catch (IOException e) {
            throw ExceptionFactory.unexpectedException();
        }
    }
}
