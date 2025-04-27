package com.authenthication.auth_session.Config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    
    
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(
        HttpSecurity http, 
        SessionAuthenticationFilter sessionAuthenticationFilter
    ) throws Exception {
        http
            .csrf(csrf -> csrf.ignoringRequestMatchers(
                "/api/v1/user/register",
                "/api/v1/user/login",
                "/api/v1/user/logout",  // Add this line
                "/api/v1/user/current"  // Ensure this matches your logout endpoint
            ))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(
                    "/api/v1/user/register",
                    "/api/v1/user/login"
                ).permitAll()
                .requestMatchers("/api/v1/admin/**").hasRole("ADMIN")
                .anyRequest().authenticated()
            )
            .addFilterBefore(sessionAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            );
        
        return http.build();
    }
}

  