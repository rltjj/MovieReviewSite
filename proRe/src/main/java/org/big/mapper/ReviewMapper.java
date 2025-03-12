package org.big.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.big.dto.ReviewDto;

@Mapper
public interface ReviewMapper {

	List<ReviewDto> findReviewsByUser(Long userId);

	void insertReview(ReviewDto review);

}
