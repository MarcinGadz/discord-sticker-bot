package com.zzpj.dc.app.dao;

import com.zzpj.dc.app.exceptions.ImageDoesntExistException;
import com.zzpj.dc.app.model.Image;
import org.springframework.stereotype.Component;

import javax.naming.OperationNotSupportedException;
import java.util.ArrayList;
import java.util.List;

@Component(value = "ImageInMemoryDAO")
public class ImageInMemoryDAO implements ImageDAO{
    private List<Image> images = new ArrayList<>();
    @Override
    public void addImage(Image img) {
        images.add(img);
    }

    @Override
    public List<Image> getImagesForOwner(String owner) {
        return images.stream()
                .filter(img -> img.getOwner().equals(owner))
                .toList();
    }

    @Override
    public Image getImageByName(String name, String owner) {
        return images.stream()
                .filter(img -> img.getOwner().equals(owner) && img.getName().equals(name))
                .findFirst().orElse(null);
    }

    @Override
    public void removeImageByName(String name, String owner) throws ImageDoesntExistException {
        throw new UnsupportedOperationException();
    }
}
