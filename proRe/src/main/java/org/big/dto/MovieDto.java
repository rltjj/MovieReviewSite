package org.big.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class MovieDto {
    private Long id;
    private String title;
    private String director;
    private LocalDateTime releaseDate; // 변경
    private String genre;
    private String posterImageName;
    private String synopsis;
    private Double averageRating;
    private LocalDateTime createdAt; // 변경
}
