package com.travelai.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // Disable CSRF protection as we use JWT for authentication
                .csrf(AbstractHttpConfigurer::disable)

                // Configure authorization rules
                // .authorizeHttpRequests(authorize -> authorize
                //         // Allow access to static resources
                //         .requestMatchers("/static/login.html", "/static/register.html", "/static/index.html", "/static/css/**", "/static/js/**",
                //                 "/static/img/**")
                //         .permitAll()
                //         // Allow access to login, register and validate APIs
                //         .requestMatchers("/api/auth/login", "/api/auth/register", "/api/auth/validate", "/api/voice/transcribe").permitAll()
                //         // All other requests need authentication
                //         .anyRequest().authenticated())

                // Disable default form login handling
                .formLogin(form -> form.disable())

                // Configure basic authentication as stateless since we use JWT
                .sessionManagement(session -> session.disable());

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}