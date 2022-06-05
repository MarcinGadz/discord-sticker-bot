package com.zzpj.dc.app.dao;

import com.zzpj.dc.app.exceptions.ImageContentEmptyException;
import com.zzpj.dc.app.exceptions.ImageDoesntExistException;
import com.zzpj.dc.app.model.Image;

import java.util.List;

public interface ImageDAO {
    void addImage(Image img) throws ImageContentEmptyException;
    void removeImageByName(String name, String owner) throws ImageDoesntExistException;
    List<Image> getImagesForOwner(String owner);
    Image getImageByName(String name, String owner) throws ImageDoesntExistException;
}
