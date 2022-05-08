package com.zzpj.dc.app.service;

import com.zzpj.dc.app.exceptions.WrongFileTypeException;
import com.zzpj.dc.app.model.Image;
import com.zzpj.dc.app.model.Owner;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

@Service
public class ImageService {

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
        List<Byte> pngSignature = Stream.of(137, 80, 78, 71, 13, 10, 26, 10)
                .map(Integer::byteValue).toList();

        if (image.getContent().length < pngSignature.size()) {
            return false;
        }
        for (int i = 0; i < 8; i++) {
            if (! image.getContent()[i].equals(pngSignature.get(i))) {
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
