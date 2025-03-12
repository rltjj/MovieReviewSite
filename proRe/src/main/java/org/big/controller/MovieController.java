package org.big.controller;

import java.util.List;

import org.big.dto.MovieDto;
import org.big.service.MovieService;
import org.big.service.ReviewService;
import org.big.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.HttpSession;

@Controller
public class MovieController {

    @Autowired
    private MovieService movieService;

    @Autowired
    private ReviewService reviewService; 

    @Autowired
    private UserService userService;

    @GetMapping("/main")
    public String getMovieList(Model model) {
        try {
            // 영화 목록 가져오기
            List<MovieDto> movies = movieService.getAllMovies();
            model.addAttribute("movies", movies);
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("errorMessage", "영화 목록을 불러오는 중 오류가 발생했습니다.");
        }
        return "thymeleaf/main";
    }

    @GetMapping("/movie/{id}")
    public String movieDetail(@PathVariable Long id, Model model) {
        try {
            MovieDto movie = movieService.getMovieById(id);
            if (movie == null) {
                return "thymeleaf/error"; // 영화가 없으면 에러 페이지로 이동
            }
            model.addAttribute("movie", movie);
            return "thymeleaf/moviedetail"; // 상세 페이지 뷰 반환
        } catch (Exception e) {
            e.printStackTrace();  // 예외 출력
            return "thymeleaf/error"; // 예외 발생 시 에러 페이지로 이동
        }
    }

    @GetMapping("/movie/review/{id}")
    public String reviewWrite(@PathVariable Long id, Model model) {
        try {
            MovieDto movie = movieService.getMovieById(id);
            if (movie == null) {
                return "thymeleaf/error";
            }
            model.addAttribute("movie", movie); // 리뷰 작성 폼에 영화 정보 전달
            return "thymeleaf/writereview";
        } catch (Exception e) {
            e.printStackTrace(); 
            return "thymeleaf/error"; 
        }
    }

    @PostMapping("/movie/review-submit")
    public String submitReview(@RequestParam Long movieId, @RequestParam int rating, @RequestParam String reviewComment, Model model) {
        // 인증된 사용자 정보 가져오기
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getPrincipal().equals("anonymousUser")) {
            // 로그인하지 않은 사용자일 경우 로그인 페이지로 리디렉션
            return "redirect:/login";
        }

        // 사용자 정보가 있으면 리뷰 저장
        String username = ((UserDetails) authentication.getPrincipal()).getUsername();
        Long userId = userService.findUserIdByUsername(username); // 사용자 ID를 가져오는 방법이 필요

        // 리뷰 저장 및 영화 정보 업데이트
        reviewService.submitReview(movieId, rating, reviewComment, userId);

        return "redirect:/movie/" + movieId; // 리뷰 작성 후 영화 상세 페이지로 리디렉션
    }

}
