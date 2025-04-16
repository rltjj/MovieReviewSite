package org.big.dto;

import java.time.LocalDateTime;
import java.util.List;

import lombok.Data;

@Data
public class BoardDto {
	private int boardIdx;
    private String title;
    private String content;
    private int hitCnt;
    private LocalDateTime createdDatetime;
    private String creatorId;
    
    private List<BoardFileDto> fileList;
}
