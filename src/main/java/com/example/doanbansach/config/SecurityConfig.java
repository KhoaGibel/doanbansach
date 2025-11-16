package com.example.doanbansach.config;

import com.example.doanbansach.service.CustomUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    @Autowired
    private AuthenticationSuccessHandler customAuthenticationSuccessHandler;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // Tắt CSRF để đơn giản hóa quá trình test
                .authorizeHttpRequests(authorize -> authorize

                        // 1. CHỈ ADMIN MỚI ĐƯỢC VÀO
                        .requestMatchers("/admin/**", "/categories/**", "/books/new", "/books/edit/**", "/books/delete/**")
                        .hasRole("ADMIN")

                        // 2. CHỈ CẦN ĐĂNG NHẬP (BƯỚC THANH TOÁN QUAN TRỌNG)
                        .requestMatchers("/checkout", "/place-order", "/order-success/**")
                        .authenticated()

                        // 3. TẤT CẢ CÁC ĐỊA CHỈ CÒN LẠI ĐỀU LÀ CÔNG KHAI (PUBLIC)
                        .anyRequest().permitAll() // <-- ĐÂY LÀ SỰ THAY ĐỔI QUAN TRỌNG NHẤT
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .loginProcessingUrl("/login")
                        .successHandler(customAuthenticationSuccessHandler)
                        .failureUrl("/login?error=true")
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login?logout")
                        .permitAll()
                )
                .userDetailsService(customUserDetailsService);

        return http.build();
    }
}