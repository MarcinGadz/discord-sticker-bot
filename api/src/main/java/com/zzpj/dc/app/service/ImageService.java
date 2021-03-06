package com.zzpj.dc.app.service;

import com.zzpj.dc.app.dao.ImageDAO;
import com.zzpj.dc.app.exceptions.*;
import com.zzpj.dc.app.model.Image;
import com.zzpj.dc.app.util.EnvironmentUtils;
import com.zzpj.dc.app.util.TimeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.Objects;

@Service
public class ImageService {
    private static final Byte[] PNG_SIGNATURE = new Byte[]{(byte) 0x89, 0x50, 0x4E, 0x47, 0x0D, 0x0A, 0x1A, 0x0A};
    private static final Long HOUR_MILLIS = 3600000L;
    private final Integer userAddPerHourLimit;
    private final Integer userAddPerDayLimit;
    private final Integer userMaxImages;
    private ImageDAO imageDAO;
    private TimeUtils timeUtils;

    @Autowired
    public ImageService(EnvironmentUtils env) {
        this.userAddPerHourLimit = env.getUserAddPerHourLimit();
        this.userAddPerDayLimit = env.getGetUserAddPerDayLimit();
        this.userMaxImages = env.getUserMaxImages();
    }

    @Autowired
    public void setImageDAO(ImageDAO imageDAO) {
        this.imageDAO = imageDAO;
    }

    @Autowired
    public void setTimeUtils(TimeUtils timeUtils) {
        this.timeUtils = timeUtils;
    }

    /**
     * Method to persist image passed by user if conditions are passed
     *
     * @param image Image to be saved
     * @param owner User who is uploading image
     */
    public void addImage(MultipartFile image, String imageName, String owner) throws IOException, ImageContentEmptyException, ImageAlreadyExistsException, UserLimitExceededException, WrongFileTypeException {
        Long currentTime = timeUtils.getCurrentMilis();
        LocalDate currentDay = timeUtils.getCurrentDay();
        if (image == null) {
            throw new ImageContentEmptyException();
        }
        Image img = new Image(
                imageName,
                null,
                image.getBytes(),
                owner,
                currentTime
        );
        if(!checkOwnerHourLimits(owner, currentTime) || !checkOwnerImagesLimit(owner)) {
            throw new UserLimitExceededException();
        }
        if(!checkOwnerDailyLimits(owner, currentDay)) {
            throw new UserLimitExceededException();
        }
        if (!isPNG(img)) {
            throw new WrongFileTypeException();
        }
        try {
            getImageByName(imageName, owner);
            throw new ImageAlreadyExistsException();
        } catch (ImageDoesntExistException ex) {
            imageDAO.addImage(img);
        }
    }

    /**
     * Checks if user owns more than USER_MAX_IMAGES - 1 images
     *
     * @param owner ID of user who will be checked
     * @return boolean specifying if user does not have more than USER_MAX_IMAGES - 1 images
     */
    private boolean checkOwnerImagesLimit(String owner) {
        return imageDAO.getImagesForOwner(owner).size() < userMaxImages;
    }

    /**
     * Method to check if user exceeded his hourly limits for adding images
     *
     * @param owner username of user whose limit is checked
     * @param time  time in milliseconds for which limits will be checked
     * @return boolean specifying if user can add more images now
     */
    private boolean checkOwnerHourLimits(String owner, Long time) {
        Long limitWindowStart = time - HOUR_MILLIS;
        return imageDAO.getImagesForOwner(owner)
                .stream()
                .filter(img -> img.getSaveDate() > limitWindowStart)
                .toList().size() < userAddPerHourLimit;
    }

    /**
     * Method to check if user exceeded his daily limits for adding images
     *
     * @param owner username of user whose limit is checked
     * @param date  Date for which limits will be checked
     * @return boolean specifying if user can add more images now
     */
    private boolean checkOwnerDailyLimits(String owner, LocalDate date) {
        Long limitWindowStart = date.atStartOfDay()
                .atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
        return imageDAO.getImagesForOwner(owner)
                .stream()
                .filter(img -> img.getSaveDate() > limitWindowStart)
                .toList().size() < userAddPerDayLimit;
    }

    /**
     * Get one image specified by name
     * @param name name of the image to be found
     * @param userId owner of image
     * @return found image or null
     */
    public Image getImageByName(String name, String userId) throws ImageDoesntExistException {
        return imageDAO.getImageByName(name, userId);
    }

    /**
     * Get list of all images owned by specified user
     * @param owner username of owner
     * @return list of all images owned by user
     */
    public List<Image> getForOwner(String owner, int maxItems, String startAfter) {
        return imageDAO.getImagesForOwner(owner, maxItems, startAfter);
    }

    /**
     * Method to check if passed file is PNG
     * All PNGs should start with known 8 bytes
     * If passed file starts with something different it returns false
     *
     * @param image Image to check if it is PNG
     * @return boolean telling if specified file is PNG
     */
    private boolean isPNG(Image image) {
        if (Objects.isNull(image.getContent())) {
            return false;
        }
        byte[] content = image.getContent();
        if (content.length < PNG_SIGNATURE.length) {
            return false;
        }
        for (int i = 0; i < PNG_SIGNATURE.length; i++) {
            if (content[i] != PNG_SIGNATURE[i]) {
                return false;
            }
        }
        return true;
    }

    /**
     * Removes image of given object-name from DAO
     * @param name name of the image to be removed
     * @param owner owner of the image to be removed
     * @throws ImageDoesntExistException image of this name/owner was not found in DAO
     */
    public void removeImageByName(String name, String owner) throws ImageDoesntExistException {
        imageDAO.removeImageByName(name, owner);
    }
}
