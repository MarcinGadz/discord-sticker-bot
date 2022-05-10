package com.zzpj.dc.app.dao;

import com.zzpj.dc.app.exceptions.ImageContentEmptyException;
import com.zzpj.dc.app.exceptions.ImageDoesntExistException;
import com.zzpj.dc.app.model.Image;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Component(value = "ImageS3DAO")
public class ImageS3DAO implements ImageDAO {

    private final Region awsRegion;
    private final String bucketName;
    private final S3Client s3;

    public ImageS3DAO() {
        awsRegion = Region.EU_CENTRAL_1;
        s3 = S3Client.builder()
                .region(awsRegion)
                .build();

        bucketName = "stickqr";
    }

    @Override
    public void addImage(Image image) throws ImageContentEmptyException {
        if (Objects.isNull(image.getContent())) {
            throw new ImageContentEmptyException("Image content is empty");
        }

        byte[] imageContent = image.getContent();
        s3.putObject(PutObjectRequest.builder()
                .bucket(bucketName)
                .key(image.getOwner() + "/" + image.getName())
                .contentType("x-png")
                .contentLength((long) imageContent.length)
                .build(),
                RequestBody.fromBytes(imageContent)
        );
    }

    @Override
    public List<Image> getImagesForOwner(String owner) {
        return s3.listObjectsV2(ListObjectsV2Request.builder()
                .bucket(bucketName)
                .prefix(owner + "/")
                .build())
                .contents()
                .stream()
                .map(object -> {
                    String[] keySplit = object.key().split("/");
                    String user = keySplit[0];
                    String filename = keySplit[1];

                    return new Image(
                            filename,
                            getObjectUrl(object),
                            null,
                            user,
                            object.lastModified().getEpochSecond()
                    );
                })
                .collect(Collectors.toList());
    }

    private String getObjectUrl(String key) {
        return s3.utilities().getUrl(GetUrlRequest.builder()
            .bucket(bucketName)
            .region(awsRegion)
            .key(key)
            .build())
            .toString();
    }

    private String getObjectUrl(S3Object object) {
        return getObjectUrl(object.key());
    }


    private Image getImageFromGetObjectResponse(
            ResponseInputStream<GetObjectResponse> response,
            String key
    ) throws IOException {
        String[] keySplit = key.split("/"); // Splits object name into sticker owner's id

        return new Image(
                keySplit[0],
                getObjectUrl(key),
                response.readAllBytes(),
                keySplit[1],
                LocalDateTime.now().toEpochSecond(ZoneOffset.UTC)
        );
    }

    private Image getImageFromS3Object(S3Object object) throws ImageDoesntExistException {
        try (
                ResponseInputStream<GetObjectResponse> response = s3.getObject(GetObjectRequest.builder()
                        .bucket(bucketName)
                        .key(object.key())
                        .build())
        ) {
            return getImageFromGetObjectResponse(response, object.key());
        } catch (NoSuchKeyException e) {
            throw new ImageDoesntExistException("Image of given key doesn't exist");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Image getImageByName(String name, String owner) throws ImageDoesntExistException {
        String key = owner + "/" + name;
        try (
            ResponseInputStream<GetObjectResponse> response = s3.getObject(GetObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .build())
        ) {
            return getImageFromGetObjectResponse(response, key);
        } catch (NoSuchKeyException e) {
            throw new ImageDoesntExistException("Image of given key doesn't exist");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
