package org.big.controller;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class CustomErrorController implements ErrorController {

    @RequestMapping("/error")
    public String handleError() {
        // 여기에 오류 처리 로직을 추가할 수 있습니다.
        return "thymeleaf/error"; // custom error page
    }
}
