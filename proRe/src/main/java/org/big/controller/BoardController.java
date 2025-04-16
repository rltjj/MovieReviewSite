package org.big.controller;

import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.big.dto.BoardDto;
import org.big.dto.BoardFileDto;
import org.big.service.BoardService;
import org.big.service.BoardServiceImpl;
import org.big.service.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import jakarta.servlet.MultipartConfigElement;
import jakarta.servlet.http.HttpServletResponse;

@Controller
public class BoardController {

    private final MultipartConfigElement multipartConfigElement;

    private final UserDetailsServiceImpl userDetailsServiceImpl;

    private final BoardServiceImpl boardServiceImpl;
	
	@Autowired
	private BoardService boardService;

    BoardController(BoardServiceImpl boardServiceImpl, UserDetailsServiceImpl userDetailsServiceImpl, MultipartConfigElement multipartConfigElement) {
        this.boardServiceImpl = boardServiceImpl;
        this.userDetailsServiceImpl = userDetailsServiceImpl;
        this.multipartConfigElement = multipartConfigElement;
    }

	@GetMapping("/board")
    public String showBoardForm(Model model, 
    							@RequestParam(defaultValue = "1") int page,
    							@RequestParam(defaultValue = "5") int size) // 페이지당 항목 수 (기본값: 10)// 페이지 번호 (기본값: 1)) 
    							throws Exception {
		List<BoardDto> boards = boardService.selectBoardList(page, size);
		int totalPages = boardService.getTotalPages(size);

		model.addAttribute("boards", boards);
		model.addAttribute("currentPage", page);
		model.addAttribute("totalPages", totalPages);
		model.addAttribute("currentPage", page);   // 현재 페이지 번호
        model.addAttribute("totalPages", totalPages); // 총 페이지 수
        model.addAttribute("size", size);          // 페이지당 항목 수
        
		System.out.println("boardService: " + boardService); // null이면 주입 안 됨

        return "thymeleaf/board/boardList";
    }
	
	@GetMapping("/board/{boardIdx}")
    public String showBoardDetailForm(@PathVariable("boardIdx") int boardIdx, Model model) throws Exception {
		boardService.updateHitCount(boardIdx);
		BoardDto board = boardService.selectBoardDetail(boardIdx);
		model.addAttribute("board", board);
		
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            System.out.println("인증 정보: " + authentication);
            if (authentication.getPrincipal() instanceof UserDetails) {
                UserDetails userDetails = (UserDetails) authentication.getPrincipal();
                System.out.println("로그인한 사용자: " + userDetails.getUsername());
                model.addAttribute("loggedInUser", userDetails.getUsername()); // 사용자 ID 추가
            } else {
                System.out.println("익명 사용자입니다.");
                model.addAttribute("loggedInUser", null);
            }
        } else {
            System.out.println("SecurityContext에서 Authentication이 null입니다.");
            model.addAttribute("loggedInUser", null);
        }
	    
        return "thymeleaf/board/boardDetail"; 
    }
	
	@GetMapping("/admin/boardWrite")
    public String showBoardWriteForm() {
        return "thymeleaf/board/boardWrite";
    }
	
	@PostMapping("/admin/boardWrite")
	public String addboard(BoardDto board, MultipartHttpServletRequest multipartHttpServletRequest) throws Exception {
	    System.out.println("📌 요청된 파일 필드 목록: " + multipartHttpServletRequest.getFileNames());

	    List<MultipartFile> fileList = multipartHttpServletRequest.getFiles("files"); // "files"로 가져오기
	    System.out.println("📌 업로드된 파일 개수: " + fileList.size());

	    for (MultipartFile file : fileList) {
	        if (!file.isEmpty()) {
	            System.out.println("✅ 업로드된 파일 이름: " + file.getOriginalFilename());
	        } else {
	            System.out.println("⚠️ 파일이 비어 있음!");
	        }
	    }

	    boardService.insertBoard(board, multipartHttpServletRequest);
	    return "redirect:/board";
	}


	
	@PostMapping("/board/update/{boardIdx}")
	public String updateboard(BoardDto board) throws Exception {
		boardService.updateBoard(board);
		return "redirect:/board";
	}
	
	@PostMapping("/board/delete/{boardIdx}")
	public String deleteboard(@PathVariable("boardIdx") int boardIdx) throws Exception {
		boardService.deleteBoard(boardIdx);
		return "redirect:/board";
	}
	
	@GetMapping("/board/file")
	public void downloadBoardFile(
	        @RequestParam("idx") int idx, 
	        @RequestParam("boardIdx") int boardIdx, 
	        HttpServletResponse response) throws Exception {

	    // 파일 정보 조회
	    BoardFileDto boardFile = boardService.selectBoardFileInformation(idx, boardIdx);

	    // 파일이 존재하는지 확인
	    if (boardFile != null && boardFile.getStoredFilePath() != null) {
	        File file = new File(boardFile.getStoredFilePath());

	        if (!file.exists()) {
	            response.sendError(HttpServletResponse.SC_NOT_FOUND, "파일을 찾을 수 없습니다.");
	            return;
	        }

	        String fileName = boardFile.getOriginalFileName();
	        byte[] files = FileUtils.readFileToByteArray(file);

	        // 다운로드 응답 헤더 설정
	        response.setContentType("application/octet-stream");
	        response.setContentLength(files.length);
	        response.setHeader("Content-Disposition", "attachment; filename=\"" + URLEncoder.encode(fileName, "UTF-8") + "\";");
	        response.setHeader("Content-Transfer-Encoding", "binary");

	        // 파일 출력
	        response.getOutputStream().write(files);
	        response.getOutputStream().flush();
	        response.getOutputStream().close();
	    } else {
	        response.sendError(HttpServletResponse.SC_NOT_FOUND, "파일 정보가 없습니다.");
	    }
	}
	
}
