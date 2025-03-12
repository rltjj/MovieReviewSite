package org.big.dto;

import lombok.Data;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

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
    private int reviewCount;
    private LocalDateTime createdAt; // 변경
    
 // 개봉일 포맷 메서드 추가
    public String getFormattedReleaseDate() {
        if (releaseDate != null) {
            return releaseDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        }
        return "";  // releaseDate가 null인 경우 빈 문자열 반환
    }
}
