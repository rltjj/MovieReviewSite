package org.big.Admin;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordEncoderTest {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String rawPassword = "admin1234"; // 관리자의 비밀번호
        String encodedPassword = encoder.encode(rawPassword);
        System.out.println(encodedPassword); // 암호화된 비밀번호 출력
    }
}