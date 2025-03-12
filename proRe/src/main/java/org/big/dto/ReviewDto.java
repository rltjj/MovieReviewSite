package org.big.dto;

import lombok.Data;

@Data
public class ReviewDto {
    private Long movieId; 
    private Long userId;
    private int rating; 
    private String movieTitle;
    private String reviewComment;
    
}
