package com.authenthication.auth_session.UserController;

import com.authenthication.auth_session.Dto.LoginDto;
import com.authenthication.auth_session.Dto.UserDto;
import com.authenthication.auth_session.Service.UserService;
import com.authenthication.auth_session.Service.Implementation.UserImpl;
import com.authenthication.auth_session.response.AddUserResponse;
import com.authenthication.auth_session.response.LoginResponse;
import com.authenthication.auth_session.response.MessageResponse;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.List;

import com.authenthication.auth_session.Entity.SessionInfo;
import com.authenthication.auth_session.Entity.User;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin
@RequestMapping("api/v1/user")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping(path = "/register")
    public ResponseEntity<?> registerUser(@RequestBody UserDto userDto) {
        AddUserResponse response = userService.addUser(userDto);
        
        if (response.getStatus()) {
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        }
        
        // Handle specific error cases
        if (response.getMessage().contains("cannot be empty")) {
            return ResponseEntity.badRequest().body(response);
        }
        if (response.getMessage().contains("already registered")) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
        }
        if (response.getMessage().contains("too long") || 
            response.getMessage().contains("Invalid email") || 
            response.getMessage().contains("at least 8 characters")) {
            return ResponseEntity.badRequest().body(response);
        }
        
        return ResponseEntity.internalServerError().body(response);
    }

    @PostMapping(path = "/login")
    public ResponseEntity<?> loginUser(@RequestBody LoginDto loginDto, HttpServletResponse response) {
        LoginResponse loginResponse = userService.loginUser(loginDto);
        
        if (loginResponse.getStatus()) {
            // Set the session ID as a cookie
            Cookie sessionCookie = new Cookie("SESSION_ID", loginResponse.getSessionId());
            sessionCookie.setHttpOnly(true);
            sessionCookie.setSecure(true);
            sessionCookie.setPath("/");
            sessionCookie.setMaxAge(60 * 30);
            response.addCookie(sessionCookie);
            return ResponseEntity.ok(loginResponse);
        }
        
        if (loginResponse.getMessage().equals("Email does not exist")) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(loginResponse);
        }
        if (loginResponse.getMessage().equals("Password does not match")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(loginResponse);
        }
        
        return ResponseEntity.badRequest().body(loginResponse);
    }

    @PostMapping(path = "/logout")
    public ResponseEntity<?> logoutUser(HttpServletRequest request, HttpServletResponse response) {
        String sessionId = null;
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("SESSION_ID".equals(cookie.getName())) {
                    sessionId = cookie.getValue();
                    break;
                }
            }
        }
        
        if (sessionId != null && ((UserImpl)userService).logoutUser(sessionId)) {
            // Invalidate the cookie
            Cookie sessionCookie = new Cookie("SESSION_ID", null);
            sessionCookie.setHttpOnly(true);
            sessionCookie.setSecure(true);
            sessionCookie.setPath("/");
            sessionCookie.setMaxAge(0);
            response.addCookie(sessionCookie);
            
            return ResponseEntity.ok(new MessageResponse("Logout successful"));
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new MessageResponse("Invalid session"));
    }

    @GetMapping(path = "/sessions")
    public ResponseEntity<?> getActiveSessions(HttpServletRequest request) {
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
        
        Optional<User> userOpt = ((UserImpl)userService).getUserFromSession(sessionId);
        if (!userOpt.isPresent()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new MessageResponse("Invalid session"));
        }
        
        User user = userOpt.get();
        List<SessionInfo> sessions = ((UserImpl)userService).getAllActiveSessionsForUser(user.getUserid());
        
        List<Map<String, Object>> response = sessions.stream()
            .map(session -> {
                Map<String, Object> sessionMap = new HashMap<>();
                sessionMap.put("sessionId", session.getSessionId());
                sessionMap.put("createdAt", session.getCreatedAt());
                sessionMap.put("expiresAt", session.getExpiresAt());
                return sessionMap;
            })
            .collect(Collectors.toList());
        
        return ResponseEntity.ok(response);
    }

    @GetMapping(path = "/current")
    public ResponseEntity<?> getCurrentUser(HttpServletRequest request) {
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
        
        Optional<User> userOpt = ((UserImpl)userService).getUserFromSession(sessionId);
        
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            Map<String, Object> response = new HashMap<>();
            response.put("username", user.getUsername());
            response.put("email", user.getEmail());
            response.put("userId", user.getUserid());
            return ResponseEntity.ok(response);
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new MessageResponse("Invalid session"));
    }
}