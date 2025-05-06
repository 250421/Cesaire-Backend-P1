package com.authenthication.auth_session;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.lang.NonNull;

@SpringBootApplication 
public class RegisterLogin {

	public static void main(String[] args) {
		SpringApplication.run(RegisterLogin.class, args);
	}

	@Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(@NonNull CorsRegistry registry) {
                registry.addMapping("/**")
                    .allowedOrigins(
                        "http://localhost:5173"
                    )
                    .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                    .allowedHeaders(
                        "Authorization", "Cache-Control", "Content-Type",
                        "X-Session-ID", "X-Requested-With"
                    )
                    .allowCredentials(true)
                    .maxAge(3600);
            }
        };
    }

}


