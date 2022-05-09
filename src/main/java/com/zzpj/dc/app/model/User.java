package com.zzpj.dc.app.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.LinkedList;
import java.util.List;

@Getter
@Setter
@ToString
public class User {
    private String id;
    private List<Image> lastUsed = new LinkedList<>();
    private List<Image> ownImages = new LinkedList<>();
}
