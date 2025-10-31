package com.example.SmartLibrary.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // Disable CSRF for simplicity
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/", "/login.html", "/register.html", "/css/**", "/js/**", "/api/auth/**").permitAll() // Allow public pages
                        .anyRequest().permitAll() // Everything else accessible (you can later tighten this)
                )
                .formLogin(form -> form.disable()) // Disable Spring’s default login form
                .httpBasic(httpBasic -> httpBasic.disable()); // Disable popup login

        return http.build();
    }
}
