package com.zzpj.dc.app.rest;

import com.zzpj.dc.app.model.Image;
import com.zzpj.dc.app.service.ImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
/**
 * REST Controller used to manipulate images
 */
@RestController("/image")
public class ImageController {

    private final ImageService imageService;

    @Autowired
    public ImageController(ImageService imageService) {
        this.imageService = imageService;
    }

    /**
     * Method used to add images to backend
     *
     * @param image Image to be added
     * @param owner ID of user who is adding image
     */
    @PostMapping(
            value = "/{userId}",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public void addImage(@RequestBody MultipartFile image, @PathVariable("userId") String owner) {
        try {
            imageService.addImage(image, owner);
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/{userId}/{name}")
    public Image getPhoto(@PathVariable("name") String photoName,
                          @PathVariable("userId") String userId) {
        return imageService.getImageByName(photoName, userId);
    }

}
