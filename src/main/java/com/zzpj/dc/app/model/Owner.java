package com.zzpj.dc.app.model;

import java.util.LinkedList;
import java.util.List;

public class Owner {
    private String id;
    private List<Image> lastUsed = new LinkedList<>();
    private List<Image> ownImages = new LinkedList<>();

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<Image> getLastUsed() {
        return lastUsed;
    }

    public void setLastUsed(List<Image> lastUsed) {
        this.lastUsed = lastUsed;
    }

    public List<Image> getOwnImages() {
        return ownImages;
    }

    public void setOwnImages(List<Image> ownImages) {
        this.ownImages = ownImages;
    }
}
