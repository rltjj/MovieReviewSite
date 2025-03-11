package org.big.controller;

import org.big.dto.MovieDto;
import org.big.service.MovieService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

@Controller
public class ProReController {

    @Autowired
    private MovieService movieService;

    @GetMapping("/main")
    public ModelAndView getMovieList() {
        ModelAndView mv = new ModelAndView("thymeleaf/main");

        try {
            List<MovieDto> movies = movieService.getAllMovies();
            mv.addObject("movies", movies);
        } catch (Exception e) {
            e.printStackTrace();
            mv.addObject("errorMessage", "영화 목록을 불러오는 중 오류가 발생했습니다.");
        }

        return mv;
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
            model.addAttribute("movie", movieService.getMovieById(id));
            return "thymeleaf/writereview";
        } catch (Exception e) {
            e.printStackTrace(); 
            return "thymeleaf/error"; 
        }
    }
}
