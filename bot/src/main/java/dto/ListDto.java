package dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class ListDto<T> {
    private List<T> list;
    private int allPages;
    private int pageNumber;
}
