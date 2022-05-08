package com.zzpj.dc.app.model;

import java.util.LinkedList;
import java.util.List;

public class Owner {
    private String id;
    private List<Image> lastUsed = new LinkedList<>();
    private List<Image> ownImages = new LinkedList<>();
}
