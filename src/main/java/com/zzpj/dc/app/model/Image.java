package com.zzpj.dc.app.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import java.util.Optional;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Image {
    private String name;
    private String url;
    @ToString.Exclude
    @JsonIgnore
    private byte[] content;
    private String owner;
    private Long saveDate;
}
