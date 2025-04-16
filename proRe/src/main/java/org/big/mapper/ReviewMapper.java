package org.big.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.big.dto.ReviewDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;

@Mapper
public interface ReviewMapper {

	List<ReviewDto> findReviewsByUser(Long userId);
	
	ReviewDto getReviewById(Long reviewId);

	void insertReview(ReviewDto review);

	void updateReview(@Param("reviewId") Long reviewId, 
            @Param("rating") double rating, 
            @Param("reviewComment") String reviewComment);

	ReviewDto getReviewById(@Param("reviewId") int reviewId);

	void deleteReview(@Param("reviewId") Long reviewId, @Param("movieId") Long movieId);

	public Page<ReviewDto> findReviewsByMovie(Long id, Pageable pageable);

	List<ReviewDto> findReviewsByMovie(Long id);

	int countReviewsByMovie(Long movieId);

	void updateMovieAverageRating(Long movieId);

	void insertLike(Long reviewId, Long userId);

	void incrementLikeCount(Long reviewId);

	void deleteLike(Long reviewId, Long userId);

	void decrementLikeCount(Long reviewId);
	
	int getLikeCount(Long reviewId);

	int didUserLikeReview(@Param("reviewId") Long reviewId, @Param("userId") Long userId);

}
