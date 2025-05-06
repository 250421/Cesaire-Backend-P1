package com.authenthication.auth_session.Service.Implementation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.authenthication.auth_session.Dto.LoginDto;
import com.authenthication.auth_session.Dto.ProductsDto;
import com.authenthication.auth_session.Dto.UserDto;
import com.authenthication.auth_session.Entity.SessionInfo;
import com.authenthication.auth_session.Entity.User;
import com.authenthication.auth_session.Repository.UserRepo;
import com.authenthication.auth_session.response.LoginResponse;
import com.authenthication.auth_session.response.AddUserResponse;
import com.authenthication.auth_session.Service.UserService;
import com.authenthication.auth_session.Repository.SessionRepo;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.regex.Pattern;

@Service 
public class UserImpl implements UserService {

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private SessionRepo sessionRepo;

    


    private static final int SESSION_TIMEOUT_MINUTES = 30;
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");

    @Override
    public AddUserResponse addUser(UserDto userDto) {
        // Validation checks
        if (userDto.getUsername() == null || userDto.getUsername().trim().isEmpty()) {
            return new AddUserResponse("Username cannot be empty", false);
        }
        if (userDto.getEmail() == null || userDto.getEmail().trim().isEmpty()) {
            return new AddUserResponse("Email cannot be empty", false);
        }
        if (userDto.getPassword() == null || userDto.getPassword().trim().isEmpty()) {
            return new AddUserResponse("Password cannot be empty", false);
        }
        if (!EMAIL_PATTERN.matcher(userDto.getEmail()).matches() || 
            !userDto.getEmail().matches("^(?!.*\\.com\\.com$).*\\.com$")) {
            return new AddUserResponse("Invalid email format", false);
        }
        if (userDto.getPassword().length() < 8) {
            return new AddUserResponse("Password must be at least 8 characters", false);
        }
        if (userRepo.findByEmail(userDto.getEmail()) != null) {
            return new AddUserResponse("Email already registered", false);
        }
        if (userDto.getUsername().length() > 50) {
            return new AddUserResponse("Username too long (max 50 chars)", false);
        }

        if (userDto.getRole() == null || userDto.getRole().trim().isEmpty()) {
            return new AddUserResponse("Role cannot be empty", false);
        }
        
        if (!userDto.getRole().equals("USER") && !userDto.getRole().equals("ADMIN")) {
            return new AddUserResponse("Invalid role. Must be USER or ADMIN", false);
        }

        try {
            User user = new User(
                userDto.getUserid(),
                userDto.getUsername(),
                userDto.getEmail(),
                this.passwordEncoder.encode(userDto.getPassword()),
                userDto.getRole()
            );

            userRepo.save(user);

            return new AddUserResponse(
            "User registered successfully",
                true,
                user.getUsername(),
                user.getEmail(),
                user.getRole()  
                );
        } catch (Exception e) {
            return new AddUserResponse("Registration failed: " + e.getMessage(), false);
        }
    }

    @Override
    public LoginResponse loginUser(LoginDto loginDto) {
        User user = userRepo.findByEmail(loginDto.getEmail());
        if (user == null) {
            return new LoginResponse("Email does not exist", false, null, null);
        }
        if (!passwordEncoder.matches(loginDto.getPassword(), user.getPassword())) {
            return new LoginResponse("Password does not match", false, null, null);
        }
        
        String sessionId = UUID.randomUUID().toString();
        LocalDateTime expiresAt = LocalDateTime.now().plusMinutes(SESSION_TIMEOUT_MINUTES);
        SessionInfo session = new SessionInfo(sessionId, user, expiresAt);
        sessionRepo.save(session);
        return new LoginResponse("Login Success", true, sessionId, expiresAt);
    }

    public boolean logoutUser(String sessionId) {
        Optional<SessionInfo> sessionOpt = sessionRepo.findBySessionId(sessionId);
        if (sessionOpt.isPresent()) {
            SessionInfo session = sessionOpt.get();
            session.setActive(false);
            sessionRepo.save(session);
            return true;
        }
        return false;
    }

    public boolean isValidSession(String sessionId) {
        Optional<SessionInfo> sessionOpt = sessionRepo.findBySessionId(sessionId);
        return sessionOpt.isPresent() && 
               sessionOpt.get().isActive() && 
               LocalDateTime.now().isBefore(sessionOpt.get().getExpiresAt());
    }

    public List<SessionInfo> getAllActiveSessionsForUser(Integer userId) {
        return sessionRepo.findByUser_UseridAndActiveTrue(userId);
    }

    public Optional<User> getUserFromSession(String sessionId) {
        Optional<SessionInfo> sessionOpt = sessionRepo.findBySessionId(sessionId);
        return sessionOpt.map(SessionInfo::getUser);
    }

    // Add these helper methods for role and validation checks
    public boolean isAdmin(String sessionId) {
        Optional<User> userOpt = getUserFromSession(sessionId);
        return userOpt.isPresent() && "ADMIN".equals(userOpt.get().getRole());
    }

    public boolean validateProductNumbers(ProductsDto productsDto) {
        if (productsDto.getStock() != null && productsDto.getStock() < 0) {
            return false;
        }
        if (productsDto.getPrice() != null && productsDto.getPrice() < 0) {
            return false;
        }
        return true;
    }
     
}


