package com.zzpj.dc.app.dao;

import com.zzpj.dc.app.model.Image;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.util.ArrayList;
import java.util.List;

@Component(value = "ImageS3DAO")
public class ImageS3DAO implements ImageDAO {

    @Override
    public void addImage(Image image) {
        Region region = Region.EU_CENTRAL_1;
        S3Client s3 = S3Client.builder()
                .region(region)
                .build();

        String bucket = "stickqr";

        s3.putObject(PutObjectRequest.builder()
                .bucket(bucket)
                .key(image.getOwner() + "/" + image.getName())
                .contentType("x-png")
                .contentLength((long) image.getContent().length)
                .build(),
                RequestBody.fromBytes(image.getContent())
        );
    }

    @Override
    public List<Image> getImagesForOwner(String owner) {
        // TODO Implement getImagesForOwner
        return new ArrayList<>();
    }

    @Override
    public Image getImageByName(String name, String owner) {
        // TODO Implement getImageByName
        return null;
    }
}
