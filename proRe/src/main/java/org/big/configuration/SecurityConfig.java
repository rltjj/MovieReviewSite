package org.big.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.firewall.DefaultHttpFirewall;
import org.springframework.security.web.firewall.HttpFirewall;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
	    http
	        .csrf(csrf -> csrf.disable()) // CSRF 비활성화
	        .authorizeHttpRequests(auth -> auth
	            .requestMatchers("/mypage", "/movie/review/*", "/movie/bookmark/*").authenticated() // 로그인 필요
	            .requestMatchers("/admin/**").hasAuthority("ROLE_ADMIN") // 관리자만 접근 가능
	            .requestMatchers("/movies/add", "/main?action=update").hasAuthority("ROLE_ADMIN") // 영화 추가/수정/삭제 관리자만
	            .requestMatchers(HttpMethod.POST, "/changepw-ing").permitAll() 
	            .anyRequest().permitAll() // 나머지는 모두 접근 허용
	        )
	        .formLogin(login -> login
	            .loginPage("/login") 
	            .loginProcessingUrl("/login-ing")
	            .failureHandler((request, response, exception) -> {
	                request.getSession().setAttribute("error", "로그인 실패: 아이디와 비밀번호를 확인하세요.");
	                response.sendRedirect("/login");
	            })  
	            .defaultSuccessUrl("/main", true) // 로그인 성공 후 이동할 페이지
	            .permitAll()
	        )
	        .logout(logout -> logout
	            .logoutUrl("/logout")
	            .logoutSuccessUrl("/login")
	            .invalidateHttpSession(true)
	            .deleteCookies("JSESSIONID")
	            .permitAll()
	        );

	    return http.build();
	}
	
	@Bean
	public AuthenticationManager authenticationManager(UserDetailsService userDetailsService, PasswordEncoder passwordEncoder) {
	    DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
	    provider.setUserDetailsService(userDetailsService);
	    provider.setPasswordEncoder(passwordEncoder);
	    return new ProviderManager(provider);
	}
	


	@Bean
	public WebSecurityCustomizer webSecurityCustomizer() {
		return (web) -> web.ignoring().requestMatchers("/error", "/error/*", "/img/**", "/favicon.ico");
	}

	@Bean
	public HttpFirewall defaultHttpFirewall() {
		return new DefaultHttpFirewall();
	}
	
	@Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
