package com.zzpj.dc.app.service;

import com.zzpj.dc.app.exceptions.WrongFileTypeException;
import com.zzpj.dc.app.model.Image;
import com.zzpj.dc.app.model.Owner;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class ImageService {
    private final Byte[] PNG_SIGNATURE = new Byte[] {(byte) 0x89, 0x50, 0x4E, 0x47, 0x0D, 0x0A, 0x1A, 0x0A};

    public void addImage(MultipartFile image, String owner) throws IOException {
        Image img = new Image(image.getOriginalFilename(), primitiveToObjects(image.getBytes()), owner);

        if (!isPNG(img)) {
            throw new WrongFileTypeException();
        }
        // TODO persist

    }

    public Image getImageByName(String name, String userId) {
        //TODO
        return new Image();
    }

    public List<Image> getForOwner(Owner owner) {
        //TODO
        return new ArrayList<>();
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
            if (! image.getContent()[i].equals(PNG_SIGNATURE[i])) {
                return false;
            }
        }
        return true;
    }

    private Byte[] primitiveToObjects(byte[] arr) {
        Byte[] tmp = new Byte[arr.length];
        for (int i = 0; i < arr.length; i++) {
            tmp[i] = arr[i];
        }
        return tmp;
    }
}
