package com.zzpj.dc.app.rest;

import com.zzpj.dc.app.exceptions.ImageContentEmptyException;
import com.zzpj.dc.app.model.Image;
import com.zzpj.dc.app.service.ImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

/**
 * REST Controller used to manipulate images
 */
@RestController
@RequestMapping("/image")
public class ImageController {

    private final ImageService imageService;

    @Autowired
    public ImageController(ImageService imageService) {
        this.imageService = imageService;
    }

    /**
     * Method used to add images to backend
     *
     * @param image Image to be added (must be valid PNG file with max size of 1MiB)
     * @param owner ID of user who is adding image
     */
    @PostMapping(value = "/{userId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public void addImage(
            @PathVariable("userId") @NonNull String owner,
            @RequestBody @NonNull MultipartFile image
    ) throws IOException, ImageContentEmptyException {
        imageService.addImage(image, owner);
    }

    @GetMapping("/{userId}/{name}")
    public Image getPhoto(
            @PathVariable("name") @NonNull String photoName,
            @PathVariable("userId") @NonNull String userId
    ) {
        return imageService.getImageByName(photoName, userId);
    }

    @GetMapping("/{userId}")
    public List<Image> getPhoto(
            @PathVariable("userId") @NonNull String userId
    ) {
        return imageService.getForOwner(userId);
    }
}
