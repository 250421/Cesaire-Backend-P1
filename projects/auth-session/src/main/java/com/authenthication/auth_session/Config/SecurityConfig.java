package com.authenthication.auth_session.Config;

import java.util.Arrays;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import lombok.RequiredArgsConstructor;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
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
                "/api/auth/sign-up",
                "/api/auth/sign-in",
                "/api/auth/sign-out",
                "/api/auth/**" 
            ))

            .cors(withDefaults()) // Enable CORS

            .authorizeHttpRequests(auth -> auth
                .requestMatchers(
                    "/api/auth/sign-up",
                    "/api/auth/sign-in"
                ).permitAll()
                .requestMatchers("/api/auth/sign-out").authenticated()
            .requestMatchers("/api/auth/sessions").authenticated()
            .requestMatchers("/api/auth/current").authenticated()
            .requestMatchers("/api/auth/products/**").authenticated() // Changed to authenticated()
            .requestMatchers("/api/admin/**").hasRole("ADMIN") // Only ADMIN can create products
            .requestMatchers(HttpMethod.PUT, "/api/auth/products/**").authenticated() // Both can update but logic in controller
                .anyRequest().authenticated()
            )
            .addFilterBefore(sessionAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            );
        
        return http.build();
    }

// Add this new bean method (keep all your existing beans too)
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList(
            "http://localhost:5173" // Add your frontend URL here
        ));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Cache-Control",
         "Content-Type", "X-Session-ID"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}



  