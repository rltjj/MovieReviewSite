package org.big.dto;

import lombok.Data;

@Data
public class BookmarkDto {
    private Long id;
    private Long userId;
    private Long movieId;
    private String title;
    private String genre;
}
