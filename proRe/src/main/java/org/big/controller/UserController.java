package org.big.controller;

import java.util.ArrayList;
import java.util.List;

import org.big.dto.MovieDto;
import org.big.dto.ReviewDto;
import org.big.dto.UserDto;
import org.big.service.MovieService;
import org.big.service.ReviewService;
import org.big.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

@Controller
public class UserController {

    private final UserService userService;
    private final MovieService movieService;
    private final ReviewService reviewService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    public UserController(UserService userService, MovieService movieService, ReviewService reviewService) {
        this.userService = userService;
        this.movieService = movieService;
        this.reviewService = reviewService;
    }

    // 회원가입 페이지 요청
    @GetMapping("/signup")
    public String showSignUpForm() {
        return "thymeleaf/signup"; // signup.html 뷰로 이동
    }

    // 회원가입 처리
    @PostMapping("/signup-ing")
    public String registerUser(UserDto userDto, Model model) {
        try {
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
    public String showLoginForm() {
        return "thymeleaf/login"; // login.html 뷰로 이동
    }

    // 로그인 처리
    @PostMapping("/login-ing")
    public String login(UserDto userDto, Model model) {
        model.addAttribute("errorMessage", "로그인에 실패했습니다.");
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
}

