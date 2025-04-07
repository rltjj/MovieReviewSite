package org.big.dto;

import java.util.Date;

import lombok.Data;

@Data
public class ReviewDto {
    private Long id; // id 필드 추가
    private Long movieId; 
    private Long userId;
    private String username;
    private int rating; 
    private String movieTitle;
    private String reviewComment;
    private Date reviewCreatedAt;
    private char isModified;
    private int likeCount;
}
