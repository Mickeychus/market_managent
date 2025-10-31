package com.store.quanlysieuthi.Config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(authorizeRequests ->
                authorizeRequests
                    // Tĩnh resources
                    .requestMatchers("/css/**", "/js/**", "/images/**").permitAll()
                    // Các trang ADMIN ONLY
                    .requestMatchers("/sanpham/**").hasRole("ADMIN")
                    .requestMatchers("/hoadon/xoa/**").hasRole("ADMIN")
                    .requestMatchers("/hoadon/sua/**").hasRole("ADMIN") // **THÊM MỚI**
                    .requestMatchers("/thongke/**").hasRole("ADMIN")
                    .requestMatchers("/users/**").hasRole("ADMIN")
                    .requestMatchers("/auditlog/**").hasRole("ADMIN")
                    // Các trang ADMIN & USER
                    .requestMatchers("/hoadon/**").hasAnyRole("ADMIN", "USER") // Quy tắc chung này phải ở sau
                    .requestMatchers("/banhang/**").hasAnyRole("ADMIN", "USER")
                    // Trang login và trang chủ
                    .requestMatchers("/login", "/").permitAll()
                    // Còn lại yêu cầu đăng nhập
                    .anyRequest().authenticated()
            )
            // Cấu hình formLogin
            .formLogin(formLogin ->
                formLogin
                    .loginPage("/login")
                    .defaultSuccessUrl("/banhang", true)
                    .failureUrl("/login?error=true")
                    .permitAll()
            )
            // Cấu hình logout
            .logout(logout ->
                logout
                    .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                    .logoutSuccessUrl("/login?logout")
                    .invalidateHttpSession(true)
                    .deleteCookies("JSESSIONID")
                    .permitAll()
            );

         http.csrf(csrf -> csrf.disable());

        return http.build();
    }
}