package org.big.service;

import java.util.List;

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
        if (movie != null) {
            // 기존 리뷰 개수와 평균 평점
            int currentReviewCount = movie.getReviewCount();
            double currentAverageRating = movie.getAverageRating();

            // 새로운 리뷰 개수와 평균 평점 계산
            int newReviewCount = currentReviewCount + 1;
            double newAverageRating = ((currentAverageRating * currentReviewCount) + rating) / newReviewCount;

            // 영화 테이블 업데이트
            movie.setReviewCount(newReviewCount);
            movie.setAverageRating(newAverageRating);
            movieMapper.updateMovieCount(movie);
        }
    }

}
