package org.big.controller;

import org.big.dto.UserDto;
import org.big.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    // 회원가입 페이지 요청
    @GetMapping("/signup")
    public String showSignUpForm() {
        return "thymeleaf/signup"; // signup.html 뷰로 이동
    }

    // 회원가입 처리
    @PostMapping("/signup")
    public String registerUser(UserDto userDto, Model model) {
        try {
            userService.registerUser(userDto);
            return "thymeleaf/login"; // 회원가입 후 로그인 페이지로 이동
        } catch (Exception e) {
            model.addAttribute("errorMessage", "회원가입에 실패했습니다.");
            return "thymeleaf/signup"; // 에러 발생 시 다시 회원가입 페이지로 돌아감
        }
    }

    // 로그인 페이지 요청
    @GetMapping("/login")
    public String showLoginForm() {
        return "thymeleaf/login"; // login.html 뷰로 이동
    }

}
