package org.big.common;

import java.io.File;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.big.dto.BoardFileDto;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

@Component
public class FileUtils {
	
	public List<BoardFileDto> parseFileInfo(int boardIdx, MultipartHttpServletRequest multipartHttpServletRequest) throws Exception{
		if(ObjectUtils.isEmpty(multipartHttpServletRequest)){
			return null;
		}
		
		System.out.println("업로드된 파일 이름들: ");
	    System.out.println(multipartHttpServletRequest.getFileNames());
		
		List<BoardFileDto> fileList = new ArrayList<>();
		
		DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyyMMdd"); 
    	ZonedDateTime current = ZonedDateTime.now();
    	String path = "C:/Temp/board/images/"+current.format(format);
    	File file = new File(path);
		if(file.exists() == false){
			file.mkdirs();
		}
		
		// getFileNames()에서 넘어온 파일 필드명을 리스트에 저장
		Iterator<String> iterator = multipartHttpServletRequest.getFileNames();
		List<String> fileFieldNames = new ArrayList<>();

		while (iterator.hasNext()) {
		    String fieldName = iterator.next();
		    fileFieldNames.add(fieldName);  // 필드명 저장
		    System.out.println("📌 업로드된 파일 필드명: " + fieldName);
		}

		// 저장된 필드명을 사용하여 파일 리스트 가져오기
		for (String fieldName : fileFieldNames) {
		    List<MultipartFile> list = multipartHttpServletRequest.getFiles(fieldName);
		    for (MultipartFile multipartFile : list) {
		        if (!multipartFile.isEmpty()) {
		            System.out.println("✅ 업로드된 파일 이름: " + multipartFile.getOriginalFilename());
		            System.out.println("✅ 업로드된 파일 타입: " + multipartFile.getContentType());

		            // 확장자 처리 코드 유지
		            String contentType = multipartFile.getContentType();
		            String originalFileExtension = "";

		            if (contentType.contains("image/jpeg")) {
		                originalFileExtension = ".jpg";
		            } else if (contentType.contains("image/png")) {
		                originalFileExtension = ".png";
		            } else if (contentType.contains("image/gif")) {
		                originalFileExtension = ".gif";
		            } else if (contentType.equals("text/plain")) {
		                originalFileExtension = ".txt";
		            } else if (contentType.equals("application/msword")) {
		                originalFileExtension = ".doc";
		            } else if (contentType.equals("application/vnd.openxmlformats-officedocument.wordprocessingml.document")) {
		                originalFileExtension = ".docx";
		            } else if (contentType.equals("application/vnd.ms-powerpoint")) {
		                originalFileExtension = ".ppt";
		            } else if (contentType.equals("application/vnd.openxmlformats-officedocument.presentationml.presentation")) {
		                originalFileExtension = ".pptx";
		            } else if (contentType.equals("application/haansoft-hwp") || contentType.equals("application/x-hwp")) {
		                originalFileExtension = ".hwp";
		            } else {
		                if (multipartFile.getOriginalFilename().contains(".")) {
		                    originalFileExtension = multipartFile.getOriginalFilename()
		                                                          .substring(multipartFile.getOriginalFilename().lastIndexOf("."));
		                }
		            }

		            // 파일 저장
		            String newFileName = Long.toString(System.nanoTime()) + originalFileExtension;
		            BoardFileDto boardFile = new BoardFileDto();
		            boardFile.setBoardIdx(boardIdx);
		            boardFile.setFileSize(multipartFile.getSize());
		            boardFile.setOriginalFileName(multipartFile.getOriginalFilename());
		            boardFile.setStoredFilePath(path + "/" + newFileName);
		            fileList.add(boardFile);

		            try {
		                file = new File(path + "/" + newFileName);
		                multipartFile.transferTo(file);
		                System.out.println("✅ 파일 저장 완료: " + file.getAbsolutePath());
		            } catch (Exception e) {
		                System.err.println("❌ 파일 저장 실패: " + e.getMessage());
		                e.printStackTrace();
		            }
		        }
		    }
		}

		System.out.println("📌 파일 리스트 크기: " + fileList.size());

		return fileList;
	}
}
