package org.big.controller;

import java.io.File;
import java.io.IOException;
import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.big.dto.MovieDto;
import org.big.dto.ReviewDto;
import org.big.dto.UserDto;
import org.big.service.BookmarkService;
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
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@Controller
public class MovieController {

	@Autowired
	private MovieService movieService;
	@Autowired
	private ReviewService reviewService;
	@Autowired
	private UserService userService;
	@Autowired
	private BookmarkService bookmarkService;

	@GetMapping("/main")
	public String getMovieList(@RequestParam(value = "action", required = false) String action, Model model) {
	    try {
	        // 영화 목록 가져오기
	        List<MovieDto> movies = movieService.getAllMovies();
	        model.addAttribute("movies", movies);
	        model.addAttribute("action", action); // action 값을 모델에 추가
	    } catch (Exception e) {
	        e.printStackTrace();
	        model.addAttribute("errorMessage", "영화 목록을 불러오는 중 오류가 발생했습니다.");
	    }
	    return "thymeleaf/main";
	}


	@GetMapping("/movie/{movieId}")
	public String movieDetail(@PathVariable Long movieId, Principal principal, Model model) throws Exception {
	    // 영화 정보 가져오기
	    MovieDto movie = movieService.getMovieById(movieId);
	    model.addAttribute("movie", movie);

	    // 로그인한 사용자 ID 가져오기
	    String username = (principal != null) ? principal.getName() : null;
	    model.addAttribute("username", username);

	    // 로그인한 경우 userId 가져오기
	    Long userId = null;
	    if (username != null) {
	        userId = userService.findUserIdByUsername(username);
	    }

	    // 북마크 여부
	    boolean isBookmarked = (userId != null) ? bookmarkService.isBookmarked(userId, movieId) : false;
	    model.addAttribute("isBookmarked", isBookmarked);

	    // 영화 리뷰 목록 가져오기
	    List<ReviewDto> reviews = reviewService.getReviewsByMovie(movieId); // 메소드 이름 수정
	    model.addAttribute("reviews", reviews);

	    return "thymeleaf/moviedetail";
	}

	// 리뷰 작성 페이지
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

	// 리뷰 제출
	@PostMapping("/movie/review-submit")
	public String submitReview(@RequestParam Long movieId, @RequestParam int rating, @RequestParam String reviewComment,
			Model model) {
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

	// 영화 추가 폼 페이지로 이동
	@GetMapping("/movies/add")
	public String showAddMovieForm(Model model) {
		model.addAttribute("movie", new MovieDto());
		return "thymeleaf/addmovie"; // addmovie.html을 반환
	}

	// 영화 추가 처리
	@PostMapping("/movies/add")
	public String addMovie(@ModelAttribute MovieDto movie, @RequestParam("posterFile") MultipartFile file) {
		if (!file.isEmpty()) {
			String fileName = file.getOriginalFilename(); // 업로드된 파일 이름 가져오기
			movie.setPosterImageName(fileName); // DTO에 파일명 저장

			// 실제 파일 저장 로직 (예: resources/static/images에 저장)
			String filePath = "C:/movie_posters/" + fileName;
			try {
				file.transferTo(new File(filePath));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		System.out.println("포스터 파일명: " + movie.getPosterImageName()); // 값이 들어갔는지 확인

		movieService.addMovie(movie);
		return "redirect:/main";
	}

	// 영화 수정 페이지
	@GetMapping("/movies/update/{id}")
	public String updateMovie(@PathVariable Long id, Model model) {
		MovieDto movie = null;
		try {
			movie = movieService.getMovieById(id); // 영화 정보 가져오기
		} catch (Exception e) {
			e.printStackTrace();
			return "thymeleaf/error"; // 오류 발생 시 에러 페이지로 이동
		}
		model.addAttribute("movie", movie); // 수정할 영화 정보 모델에 추가
		return "thymeleaf/updatemovie"; // 영화 수정 화면으로 이동
	}

	// 영화 수정 처리
	@PostMapping("/movies/update/{id}")
	public String updateMovie(@PathVariable Long id, @ModelAttribute MovieDto movie,
			@RequestParam(value = "posterFile", required = false) MultipartFile file) {
		try {
			// 영화 정보 조회
			MovieDto existingMovie = movieService.getMovieById(id);

			// 포스터 파일이 업로드된 경우 처리
			if (file != null && !file.isEmpty()) {
				String fileName = file.getOriginalFilename(); // 파일 이름 가져오기
				movie.setPosterImageName(fileName); // 영화 DTO에 새 포스터 파일명 저장

				// 실제 파일 저장 (예: resources/static/images에 저장)
				String filePath = "C:/Temp/movie_posters/" + fileName;
				file.transferTo(new File(filePath)); // 파일 저장
			} else {
				// 파일이 업로드되지 않은 경우 기존 포스터를 유지
				movie.setPosterImageName(existingMovie.getPosterImageName());
			}

			// 영화 수정
			movieService.updateMovie(movie); // 영화 서비스에서 수정

			return "redirect:/movie/" + id; // 수정 후 영화 상세 페이지로 리디렉션

		} catch (Exception e) {
			e.printStackTrace();
			return "thymeleaf/error"; // 에러 발생 시 에러 페이지로 이동
		}
	}

	@PostMapping("/movies/delete/{id}")
	public String deleteMovie(@PathVariable Long id) {
		try {
			movieService.deleteMovie(id); // 영화 삭제
			return "redirect:/main"; // 삭제 후 메인 페이지로 리디렉션
		} catch (Exception e) {
			e.printStackTrace();
			return "thymeleaf/error"; // 에러 발생 시 에러 페이지로 이동
		}
	}

}
