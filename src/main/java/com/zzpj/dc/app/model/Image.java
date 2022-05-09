package com.zzpj.dc.app.model;

import java.util.Arrays;
import java.util.Objects;

public final class Image {
    private String name;
    private Byte[] content;
    private String owner;
    private Long saveDate;

    public Image() {
    }

    public Image(String name, Byte[] content, String owner, Long saveDate) {
        this.name = name;
        this.content = content;
        this.owner = owner;
        this.saveDate = saveDate;
    }

    public Long getSaveDate() {
        return saveDate;
    }

    public void setSaveDate(Long saveDate) {
        this.saveDate = saveDate;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Byte[] getContent() {
        return content;
    }

    public void setContent(Byte[] content) {
        this.content = content;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (Image) obj;
        return Objects.equals(this.name, that.name) &&
                Arrays.equals(this.content, that.content) &&
                Objects.equals(this.owner, that.owner);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, owner);
    }

    @Override
    public String toString() {
        return "Image[" +
                "name=" + name + ", " +
                "owner=" + owner + ']';
    }

}
