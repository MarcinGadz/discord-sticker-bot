package com.zzpj.dc.app.dao;

import com.zzpj.dc.app.model.Image;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Component(value = "ImageInMemoryDAO")
@Profile("LOCAL")
public class ImageInMemoryDAO implements ImageDAO{
    private List<Image> images = new ArrayList<>();

    @PostConstruct
    private void initialize() {
        Image img = new Image("first", "https://localhost:1234/first", new byte[] {1, 2, 3}, "johndoe", 1654284602228L);
        Image img2 = new Image("sec", "https://localhost:1234/sec", new byte[] {9, 9, 3}, "jankowalski", 1654284578628L);
        Image img3 = new Image("third", "https://localhost:1234/third", new byte[] {5, 5, 2}, "johndoe", 1654284987234L);
        images.add(img);
        images.add(img2);
        images.add(img3);
    }

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
