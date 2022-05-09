package com.zzpj.dc.app.service;

import com.zzpj.dc.app.dao.ImageS3DAO;
import com.zzpj.dc.app.dao.ImageDAO;
import com.zzpj.dc.app.exceptions.UserLimitExceededException;
import com.zzpj.dc.app.exceptions.WrongFileTypeException;
import com.zzpj.dc.app.model.Image;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
public class ImageService {
    private static final Byte[] PNG_SIGNATURE = new Byte[] {(byte) 0x89, 0x50, 0x4E, 0x47, 0x0D, 0x0A, 0x1A, 0x0A};
    private static final Long USER_HOURLY_ADD_LIMIT = 20L;
    private static final Long HOUR_MILLIS = 3600000L;

    private ImageDAO imageDAO;

    @Autowired()
    public void setImageDAO(@Qualifier("ImageS3DAO") ImageDAO imageDAO) {
        this.imageDAO = imageDAO;
    }

    public void addImage(MultipartFile image, String owner) throws IOException {
        Long currentTime = System.currentTimeMillis();
        Image img = new Image(image.getOriginalFilename(), image.getBytes(), owner, currentTime);
        if(!checkOwnerHourLimits(owner, currentTime)) {
            throw new UserLimitExceededException();
        }
        if (!isPNG(img)) {
            throw new WrongFileTypeException();
        }
        imageDAO.addImage(img);
    }

    /**
     * Method to check if user exceeded his hourly limits for adding images
     * @param owner username of user whose limit is checked
     * @param time time in milliseconds for which limits will be checked
     * @return boolean specifying if user can add more images now
     */
    private boolean checkOwnerHourLimits(String owner, Long time) {
        Long limitWindowStart = time - HOUR_MILLIS;
        return getForOwner(owner)
                .stream()
                .filter(img -> img.getSaveDate() > limitWindowStart)
                .toList().size() <= USER_HOURLY_ADD_LIMIT;
    }

    public Image getImageByName(String name, String userId) {
        //TODO
        return imageDAO.getImageByName(name, userId);
    }

    public List<Image> getForOwner(String owner) {
        //TODO
        return imageDAO.getImagesForOwner(owner);
    }

    /**
     * Method to check if passed file is PNG
     * All PNGs should start with known 8 bytes
     * If passed file starts with something different it returns false
     * @param image Image to check if it is PNG
     * @return boolean telling if specified file is PNG
     */
    private boolean isPNG(Image image) {
        if (image.getContent().length < PNG_SIGNATURE.length) {
            return false;
        }
        for (int i = 0; i < PNG_SIGNATURE.length; i++) {
            if (image.getContent()[i] != PNG_SIGNATURE[i]) {
                return false;
            }
        }
        return true;
    }
}
