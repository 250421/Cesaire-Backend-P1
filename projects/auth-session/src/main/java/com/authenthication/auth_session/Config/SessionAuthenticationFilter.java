package com.authenthication.auth_session.Config;

import com.authenthication.auth_session.Service.Implementation.UserImpl;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Cookie;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.lang.NonNull;


import java.io.IOException;
import java.util.Optional;
import com.authenthication.auth_session.Entity.User;


@Component
public class SessionAuthenticationFilter extends OncePerRequestFilter {

    private final UserImpl userService;

    public SessionAuthenticationFilter(UserImpl userService) {
        this.userService = userService;
    }

    @Override
    protected void doFilterInternal(
        @NonNull HttpServletRequest request, 
        @NonNull HttpServletResponse response, 
        @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        
        // Check both header and cookie for session ID
        String sessionId = request.getHeader("X-Session-ID");
        if (sessionId == null) {
            Cookie[] cookies = request.getCookies();
            if (cookies != null) {
                for (Cookie cookie : cookies) {
                    if ("SESSION_ID".equals(cookie.getName())) {
                        sessionId = cookie.getValue();
                        break;
                    }
                }
            }
        }
        
        if (sessionId != null) {
            Optional<User> userOpt = userService.getUserFromSession(sessionId);
            
            if (userOpt.isPresent()) {
                User user = userOpt.get();
                UserDetails userDetails = org.springframework.security.core.userdetails.User
                    .withUsername(user.getEmail())
                    .password(user.getPassword())
                    .roles(user.getRole()) 
                    .build();
                
                UsernamePasswordAuthenticationToken authentication = 
                    new UsernamePasswordAuthenticationToken(
                        userDetails, 
                        null, 
                        userDetails.getAuthorities());
                
                authentication.setDetails(
                    new WebAuthenticationDetailsSource().buildDetails(request));
                
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }
        
        filterChain.doFilter(request, response);
    }
}


