package org.big.dto;

import java.util.Date;

import lombok.Data;

@Data
public class UserDto {
    private Long id;
    private String username;
    private String password;
    private String email;
    private Date createdAt;
    private String role;
    
    // 비밀번호 변경 필드 추가
    private String encodedPassword;
    private String oldPassword;
    private String newPassword;
}
