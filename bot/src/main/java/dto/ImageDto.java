package dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ImageDto {
    private String name;
    private String url;
    private String owner;
    private Long saveDate;
}
