package com.zzpj.dc.app.dao;

import com.zzpj.dc.app.exceptions.ImageContentEmptyException;
import com.zzpj.dc.app.exceptions.ImageDoesntExistException;
import com.zzpj.dc.app.model.Image;

import java.util.List;

public interface ImageDAO {
    void addImage(Image img) throws ImageContentEmptyException;
    List<Image> getImagesForOwner(String owner, int maxKeys, String startAfter);
    default List<Image> getImagesForOwner(String owner) {
        return getImagesForOwner(owner, Integer.MAX_VALUE, "");
    }
    Image getImageByName(String name, String owner) throws ImageDoesntExistException;
    void removeImageByName(String name, String owner) throws ImageDoesntExistException;
}
