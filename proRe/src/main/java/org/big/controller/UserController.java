package org.big.controller;

import java.util.ArrayList;
import java.util.List;

import org.big.dto.MovieDto;
import org.big.dto.ReviewDto;
import org.big.dto.UserDto;
import org.big.mapper.UserMapper;
import org.big.service.BookmarkService;
import org.big.service.MovieService;
import org.big.service.ReviewService;
import org.big.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

@Controller
public class UserController {

	@Autowired
    private UserService userService;
    @Autowired
    private MovieService movieService;
    @Autowired
    private ReviewService reviewService;
    @Autowired
    private BookmarkService bookmarkService;
    @Autowired
    private UserMapper userMapper;
    
    // 회원가입 페이지 요청
    @GetMapping("/signup")
    public String showSignUpForm() {
        return "thymeleaf/signup"; // signup.html 뷰로 이동
    }

    // 회원가입 처리
    @PostMapping("/signup-ing")
    public String registerUser(@ModelAttribute UserDto userDto, Model model) {
        try {	
        	// 아이디 중복 체크
            if (userService.isUsernameTaken(userDto.getUsername())) {
                model.addAttribute("error", "이미 사용 중인 아이디입니다.");
                return "thymeleaf/signup";
            }

            // 이메일 중복 체크
            if (userService.isEmailTaken(userDto.getEmail())) {
                model.addAttribute("error", "이미 사용 중인 이메일입니다.");
                return "thymeleaf/signup";
            }
            
            userService.registerUser(userDto);
            System.out.println("회원가입 성공! 로그인 페이지로 이동(제발)");
            return "redirect:/login"; 
        } catch (Exception e) {
            e.printStackTrace(); // 예외 로그 출력
            model.addAttribute("errorMessage", "회원가입에 실패했습니다.");
            return "thymeleaf/signup";
        }
    }

    // 로그인 페이지 요청
    @GetMapping("/login")
    public String showLoginForm(HttpSession session, Model model) {
    	String errorMessage = (String) session.getAttribute("error");

        if (errorMessage != null) {
            model.addAttribute("error", errorMessage);
            session.removeAttribute("error"); // 메시지를 한 번 표시한 후 삭제
        }
        
        return "thymeleaf/login"; // login.html 뷰로 이동
    }

    // 로그인 처리
    @PostMapping("/login-ing")
    public String login(@ModelAttribute UserDto userDto, Model model) {
    	System.out.println("로그인 시도: " + userDto.getUsername());
    	model.addAttribute("error", "로그인에 실패했습니다. 아이디와 비밀번호를 확인해주세요.");
        return "thymeleaf/login"; 
    }

    // 로그아웃 처리
    @GetMapping("/logout")
    public String logout() {
        SecurityContextHolder.clearContext(); // Spring Security 로그아웃 처리
        return "redirect:/login"; 
    }

    // 마이페이지
    @GetMapping("/mypage")
    public String myPage(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated() || 
            authentication.getPrincipal().equals("anonymousUser")) {
            return "redirect:/login"; // 로그인되지 않은 경우 로그인 페이지로 이동
        }

        // 인증된 사용자라면 UserDetails로 변환 후 username 가져오기
        String username = getAuthenticatedUsername();

        // 사용자 정보 가져오기
        UserDto user = userService.findUserByUsername(username);
        if (user == null) {
            model.addAttribute("errorMessage", "사용자 정보를 찾을 수 없습니다.");
            return "thymeleaf/mypage"; // 사용자 정보가 없으면 마이페이지에 오류 메시지 표시
        }

        // 북마크한 영화 목록 가져오기
        List<MovieDto> bookmarkedMovies = movieService.getBookmarkedMoviesByUser(user.getId());
        if (bookmarkedMovies == null) {
            bookmarkedMovies = new ArrayList<>(); // 빈 리스트로 초기화
        }

        // 내가 쓴 리뷰 목록 가져오기
        List<ReviewDto> myReviews = reviewService.getReviewsByUser(user.getId());
        if (myReviews == null) {
            myReviews = new ArrayList<>(); // 빈 리스트로 초기화
        }

        // 모델에 데이터 추가
        model.addAttribute("user", user);  // UserDto 객체를 model에 전달
        model.addAttribute("bookmarkedMovies", bookmarkedMovies);
        model.addAttribute("myReviews", myReviews);
        
        return "thymeleaf/mypage"; // mypage.html 뷰로 이동
    }


    
    // 비밀번호 변경
    @PostMapping("/mypage/updatePassword")
    public String updatePassword(UserDto userDto, Model model) {
        String username = getAuthenticatedUsername();
        if (username == null) {
            return "redirect:/login"; // 로그인 안 됐으면 로그인 페이지로 리다이렉트
        }

        try {
            boolean isUpdated = userService.updatePassword(username, userDto.getOldPassword(), userDto.getNewPassword());
            if (isUpdated) {
                model.addAttribute("successMessage", "비밀번호가 성공적으로 변경되었습니다.");
            } else {
                model.addAttribute("errorMessage", "비밀번호 변경에 실패했습니다.");
            }
        } catch (Exception e) {
            model.addAttribute("errorMessage", "오류가 발생했습니다.");
        }
        return "thymeleaf/mypage"; // 변경 후 마이페이지로 리다이렉트
    }


    // 회원 탈퇴
    @PostMapping("/mypage/deleteAccount")
    public String deleteAccount(Model model) {
        String username = getAuthenticatedUsername();
        if (username == null) {
            return "redirect:/login"; // 로그인 안 됐으면 로그인 페이지로 리다이렉트
        }

        try {
            userService.deleteAccount(username);
            return "redirect:/logout"; // 탈퇴 후 로그아웃
        } catch (Exception e) {
            model.addAttribute("errorMessage", "회원 탈퇴에 실패했습니다.");
        }
        return "thymeleaf/mypage"; // 실패하면 마이페이지로 돌아감
    }

    private String getAuthenticatedUsername() {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return (userDetails != null) ? userDetails.getUsername() : null;
    }
    
    // 북마크 추가
    @PostMapping("/movie/bookmark/{movieId}")
    public String addBookmark(@PathVariable Long movieId, 
                              @AuthenticationPrincipal UserDetails userDetails) {
        String username = userDetails.getUsername();  // 로그인한 사용자의 username 가져오기
        Long userId = userMapper.findUserIdByUsername(username);  // 올바른 메서드명 사용

        bookmarkService.addBookmark(userId, movieId);

        return "redirect:/movie/" + movieId;
    }

    // 북마크 삭제
    @PostMapping("/movie/bookmark/remove")
    public String removeBookmark(@RequestParam Long movieId, 
                                 @AuthenticationPrincipal UserDetails userDetails) {
        String username = userDetails.getUsername();  // 로그인한 사용자의 username 가져오기
        Long userId = userMapper.findUserIdByUsername(username);  // 올바른 메서드명 사용

        bookmarkService.removeBookmark(userId, movieId);

        return "redirect:/movie/" + movieId;
    }
    
    // 북마크 삭제
    @PostMapping("/movie/bookmark/removemypage")
    public String removeBookmarkMypage(@RequestParam Long movieId, 
                                 @AuthenticationPrincipal UserDetails userDetails) {
        String username = userDetails.getUsername();  // 로그인한 사용자의 username 가져오기
        Long userId = userMapper.findUserIdByUsername(username);  // 사용자 ID 조회

        bookmarkService.removeBookmark(userId, movieId);

        return "redirect:/mypage";  // 북마크 해제 후 마이페이지로 리디렉트
    }
    
    // 리뷰 수정 페이지로 이동
    @GetMapping("/review/edit/{reviewId}")
    public String editReview(@PathVariable Long reviewId, Model model, 
                             @AuthenticationPrincipal UserDetails userDetails) {
        String username = userDetails.getUsername();
        Long userId = userMapper.findUserIdByUsername(username);
        
        ReviewDto review = reviewService.getReviewById(reviewId);
        
        // 본인이 작성한 리뷰인지 검증
        if (review == null || !review.getUserId().equals(userId)) {
            return "redirect:/mypage"; // 권한 없으면 마이페이지로 이동
        }

        model.addAttribute("review", review);
        return "thymeleaf/review-edit";
    }
    
    // 리뷰 업데이트
    @PostMapping("/review/update")
    public String updateReview(@RequestParam Long reviewId, 
                               @RequestParam int rating, 
                               @RequestParam String reviewComment,
                               @AuthenticationPrincipal UserDetails userDetails,
                               Model model) {
        String username = userDetails.getUsername();
        Long userId = userMapper.findUserIdByUsername(username);

        // 본인 리뷰인지 확인
        ReviewDto review = reviewService.getReviewById(reviewId);
        if (review == null || !review.getUserId().equals(userId)) {
            return "redirect:/mypage";
        }

        // 리뷰 업데이트
        Long movieId = review.getMovieId(); 
        reviewService.updateReview(reviewId, movieId, rating, reviewComment);

        // 영화 제목 가져오기
        String movieTitle = review.getMovieTitle();
        model.addAttribute("movieTitle", movieTitle);

        return "redirect:/mypage";
    }


    // 리뷰 삭제
    @PostMapping("/review/delete")
    public String deleteReview(@RequestParam Long reviewId, 
                               @AuthenticationPrincipal UserDetails userDetails) {
        String username = userDetails.getUsername();
        Long userId = userMapper.findUserIdByUsername(username);

        // 본인 리뷰인지 확인 후 삭제 실행
        ReviewDto review = reviewService.getReviewById(reviewId);
        if (review == null || !review.getUserId().equals(userId)) {
            return "redirect:/mypage";
        }

        Long movieId = review.getMovieId(); 
        reviewService.deleteReview(reviewId, movieId);
        return "redirect:/mypage";
    }

}

