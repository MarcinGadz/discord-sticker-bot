package com.zzpj.dc.app.dao;

import com.zzpj.dc.app.model.Image;
import org.springframework.stereotype.Component;

import java.util.List;

public interface ImageDAO {
    void addImage(Image img);
    List<Image> getImagesForOwner(String owner);
    Image getImageByName(String name, String owner);
}
