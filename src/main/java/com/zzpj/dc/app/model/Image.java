package com.zzpj.dc.app.model;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Image {
    private String name;
    private byte[] content;
    @ToString.Exclude
    private String owner;
    private Long saveDate;
}
