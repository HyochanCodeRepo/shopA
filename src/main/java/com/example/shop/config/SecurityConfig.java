package com.example.shop.config;


import jakarta.servlet.annotation.WebListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration //환경설정 클래스
@EnableMethodSecurity
@EnableWebSecurity
@WebListener
public class SecurityConfig {

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http)throws Exception {


        http
                .authorizeHttpRequests(
                        //url을 통해서 들어오는 인가, 권한에 따른 접속여부

                        (AuthorizeHttpRequests) -> AuthorizeHttpRequests
                                //해당 url은 모든 사용자가 접속 가능
                                .requestMatchers("/user/login/**", "/user/signUp", "/css/**", "/js/**", "/").permitAll()
                                //권한이 ADMIN인사람은 해당페이지 접속 가능
                                .requestMatchers("/admin/item/**").hasRole("ADMIN")
                                .requestMatchers("/orders/**").authenticated() //로그인이 되어있어야만 하는 주소
                                .anyRequest().permitAll()


                )
                //csrf 토큰: 서버에서 생성되는 임의 값으로 페이지 요청시 항상
                //다른값으로 생성된다.
                //토큰 : 요청을 식별하고 검증하는데 사용하는 특수한 문자열 또는 값
                //미지정시 csrf 방어기능에 의한 접근 거부
                .csrf(csrf -> csrf.disable())
                .formLogin(
                        formLogin -> formLogin.loginPage("/user/login") //로그인 페이지 지정
                                .defaultSuccessUrl("/")                 //로그인 성공 시 이동페이지
                                .usernameParameter("email")
                        //로그인시 작성 parameter 명               //실패시 url
                )
                .logout(logout -> logout.logoutUrl("/user/logout")
                        .logoutSuccessUrl("/").invalidateHttpSession(true) //로그아웃시 이동할 url, 로그아웃 url, 로그아웃시 세션 초기화
                )
                .exceptionHandling(
                        a -> a.accessDeniedHandler(new CustomAccessDeniedHandler())
                );
        
        
        //로그인 : 인증
        
        //로그아웃
        

        
        
        //로그인, 로그아웃, 페이지 접속 시 각각 Handler 지정. 기본적으로 제공되는 기능과 다르게
        //직접 설정


        return http.build();



    }

    @Bean
    PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }
    
    
    
    
}
