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

/**
 * Class implementing ImageDAO functionality for AWS S3
 */
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

    /**
     * Sends an image to S3
     *
     * @param image Image to be sent to S3 (has to have valid PNG content)
     * @throws ImageContentEmptyException when provided Image has no content
     */
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

    /**
     * Gets all the images, that are owned by the provided owner from S3.
     *
     * @param owner ID of the owner the images are supposed to be listed for
     * @return List of all images that belong to a given owner
     */
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

    /**
     * Creates an access URL to a given object
     *
     * @param key Key of the S3 object
     * @return URL to the object
     */
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


    /**
     * @param response GetObjectResponse wrapped inside a ResponseInputStream
     * @param key Key of the object
     * @return Image object containing Image information and it's content
     * @throws IOException problem with reading Url
     */
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

    /**
     * @param name Name of the image to be retrieved
     * @param owner Owner of the image to be retrieved
     * @return Image object containing a valid PNG content
     * @throws ImageDoesntExistException  image doesn't exit in the bucket
     */
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

    /**
     * Removes an image of given filename from the S3 bucket
     *
     * @param name name of the object that should be removed
     * @param owner owner of the object
     * @throws ImageDoesntExistException image doesn't exist in the bucket
     */
    @Override
    public void removeImageByName(String name, String owner) throws ImageDoesntExistException {
        String key = owner + "/" + name;
        try {
            s3.deleteObject(DeleteObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .build()
            );
        } catch (NoSuchKeyException e) {
            throw new ImageDoesntExistException("Image of given key doesn't exist");
        }
    }
}
