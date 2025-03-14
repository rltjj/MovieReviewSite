package org.big.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.big.dto.ReviewDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Mapper
public interface ReviewMapper {

	List<ReviewDto> findReviewsByUser(Long userId);
	
	ReviewDto getReviewById(Long reviewId);

	void insertReview(ReviewDto review);

	void updateReview(Long reviewId, int rating, String reviewComment);

	void deleteReview(Long reviewId);

	public Page<ReviewDto> findReviewsByMovie(Long id, Pageable pageable);

	List<ReviewDto> findReviewsByMovie(Long id);

	int countReviewsByMovie(Long movieId);

}
