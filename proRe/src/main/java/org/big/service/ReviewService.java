package org.big.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.big.dto.MovieDto;
import org.big.dto.ReviewDto;
import org.big.mapper.ReviewMapper;
import org.big.mapper.MovieMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ReviewService {

    @Autowired
    private ReviewMapper reviewMapper;

    @Autowired
    private MovieMapper movieMapper;

    // 사용자가 작성한 리뷰들을 조회
    public List<ReviewDto> getReviewsByUser(Long userId) {
        return reviewMapper.findReviewsByUser(userId);
    }

    // 리뷰 제출 및 영화 평점 및 리뷰 개수 업데이트
    public void submitReview(Long movieId, int rating, String reviewContent, Long userId) {
        // 리뷰 저장
        ReviewDto review = new ReviewDto();
        review.setMovieId(movieId);
        review.setUserId(userId);
        review.setRating(rating);
        review.setReviewComment(reviewContent);
        reviewMapper.insertReview(review);

        // 영화 정보 업데이트
        MovieDto movie = movieMapper.findById(movieId);
        reviewMapper.updateMovieAverageRating(movieId);
    }

    public ReviewDto getReviewById(Long reviewId) {
        return reviewMapper.getReviewById(reviewId);
    }
    
    public void updateReview(Long reviewId, Long movieId, int rating, String reviewComment) {
    	reviewMapper.updateReview(reviewId, rating, reviewComment);
    	MovieDto movie = movieMapper.findById(movieId);
        reviewMapper.updateMovieAverageRating(movieId);
    }
    
    public ReviewDto getReviewById(int reviewId) {
        return reviewMapper.getReviewById(reviewId);
    }

    public void deleteReview(Long reviewId, Long movieId) {
    	reviewMapper.deleteReview(reviewId, movieId); 
        MovieDto movie = movieMapper.findById(movieId);
        reviewMapper.updateMovieAverageRating(movieId);
    }

    // 리뷰 목록을 페이지네이션 없이 반환
    public List<ReviewDto> getReviewsByMovie(Long id) {
        return reviewMapper.findReviewsByMovie(id);
    }

	public void likeReview(Long reviewId, Long userId) {
		reviewMapper.insertLike(reviewId, userId);
	    reviewMapper.incrementLikeCount(reviewId);
	}

	public void unlikeReview(Long reviewId, Long userId) {
		reviewMapper.deleteLike(reviewId, userId);
	    reviewMapper.decrementLikeCount(reviewId);
	}

	public int getLikeCount(Long reviewId) {
		return reviewMapper.getLikeCount(reviewId);
	}

	public boolean didUserLikeReview(Long reviewId, Long userId) {
	    return reviewMapper.didUserLikeReview(reviewId, userId) > 0;
	}

}
