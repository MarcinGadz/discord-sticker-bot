package com.zzpj.dc.app.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@EqualsAndHashCode
public class Image {
    private String name;
    private String url;
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private byte[] content;
    private String owner;
    private Long saveDate;
}
