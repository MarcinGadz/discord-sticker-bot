package com.zzpj.dc.app.dao;

import com.zzpj.dc.app.model.Image;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component(value = "ImageInMemoryDAO")
@Profile("LOCAL")
public class ImageInMemoryDAO implements ImageDAO{
    private List<Image> images = new ArrayList<>();
    @Override
    public void addImage(Image img) {
        img.setUrl("https://localhost:1234/" + img.getSaveDate());
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
}
