package com.authenthication.auth_session.Service.Implementation;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import com.authenthication.auth_session.Dto.LoginDto;
import com.authenthication.auth_session.Dto.ProductsDto;
import com.authenthication.auth_session.Dto.UserDto;
import com.authenthication.auth_session.Entity.SessionInfo;
import com.authenthication.auth_session.Entity.User;
import com.authenthication.auth_session.Repository.SessionRepo;
import com.authenthication.auth_session.Repository.UserRepo;
import com.authenthication.auth_session.response.AddUserResponse;
import com.authenthication.auth_session.response.LoginResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class UserImplTest {

    @Mock
    private UserRepo userRepo;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private SessionRepo sessionRepo;

    @InjectMocks
    private UserImpl userService;

    private UserDto validUserDto;
    private LoginDto validLoginDto;
    private User existingUser;
    private SessionInfo activeSession;

    @BeforeEach
    void setUp() {
        validUserDto = new UserDto(
            0, "testuser", "test@example.com", "password123", "USER"
        );

        validLoginDto = new LoginDto("test@example.com", "password123");

        existingUser = new User(
            1, "testuser", "test@example.com", 
            "encodedPassword", "USER"
        );
        
        activeSession = new SessionInfo(
            "session123", existingUser, LocalDateTime.now().plusHours(1)
        );
        activeSession.setActive(true);
    }

    @Test
    void registerUser_ValidInput_ReturnsSuccessResponse() {
        when(userRepo.findByEmail(anyString())).thenReturn(null);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(userRepo.save(any(User.class))).thenReturn(existingUser);

        AddUserResponse response = userService.addUser(validUserDto);

        assertTrue(response.getStatus());
        assertEquals("User registered successfully", response.getMessage());
        assertEquals("testuser", response.getUsername());
        assertEquals("test@example.com", response.getEmail());
        assertEquals("USER", response.getRole());
        verify(userRepo, times(1)).save(any(User.class));
    }

    @Test
    void registerUser_EmptyUsername_ReturnsError() {
        validUserDto.setUsername("");
        AddUserResponse response = userService.addUser(validUserDto);
        
        assertFalse(response.getStatus());
        assertEquals("Username cannot be empty", response.getMessage());
    }

    @Test
    void registerUser_EmptyEmail_ReturnsError() {
        validUserDto.setEmail("");
        AddUserResponse response = userService.addUser(validUserDto);
        
        assertFalse(response.getStatus());
        assertEquals("Email cannot be empty", response.getMessage());
    }

    @Test
    void registerUser_EmptyPassword_ReturnsError() {
        validUserDto.setPassword("");
        AddUserResponse response = userService.addUser(validUserDto);
        
        assertFalse(response.getStatus());
        assertEquals("Password cannot be empty", response.getMessage());
    }

    @Test
    void registerUser_InvalidEmail_ReturnsError() {
        validUserDto.setEmail("invalid-email");
        AddUserResponse response = userService.addUser(validUserDto);
        
        assertFalse(response.getStatus());
        assertEquals("Invalid email format", response.getMessage());
    }

    @Test
    void registerUser_ShortPassword_ReturnsError() {
        validUserDto.setPassword("short");
        AddUserResponse response = userService.addUser(validUserDto);
        
        assertFalse(response.getStatus());
        assertEquals("Password must be at least 8 characters", response.getMessage());
    }

    @Test
    void registerUser_ExistingEmail_ReturnsError() {
        when(userRepo.findByEmail(anyString())).thenReturn(existingUser);
        AddUserResponse response = userService.addUser(validUserDto);
        
        assertFalse(response.getStatus());
        assertEquals("Email already registered", response.getMessage());
    }

    @Test
    void registerUser_LongUsername_ReturnsError() {
        validUserDto.setUsername("a".repeat(51));
        AddUserResponse response = userService.addUser(validUserDto);
        
        assertFalse(response.getStatus());
        assertEquals("Username too long (max 50 chars)", response.getMessage());
    }

    @Test
    void registerUser_EmptyRole_ReturnsError() {
        validUserDto.setRole("");
        AddUserResponse response = userService.addUser(validUserDto);
        
        assertFalse(response.getStatus());
        assertEquals("Role cannot be empty", response.getMessage());
    }

    @Test
    void registerUser_InvalidRole_ReturnsError() {
        validUserDto.setRole("INVALID_ROLE");
        AddUserResponse response = userService.addUser(validUserDto);
        
        assertFalse(response.getStatus());
        assertEquals("Invalid role. Must be USER or ADMIN", response.getMessage());
    }

    @Test
    void loginUser_ValidCredentials_ReturnsSuccess() {
        when(userRepo.findByEmail(anyString())).thenReturn(existingUser);
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);
        when(sessionRepo.save(any(SessionInfo.class))).thenReturn(activeSession);

        LoginResponse response = userService.loginUser(validLoginDto);

        assertTrue(response.getStatus());
        assertEquals("Login Success", response.getMessage());
        assertNotNull(response.getSessionId());
        assertNotNull(response.getExpiresAt());
    }

    @Test
    void loginUser_InvalidEmail_ReturnsError() {
        when(userRepo.findByEmail(anyString())).thenReturn(null);
        
        LoginResponse response = userService.loginUser(validLoginDto);
        
        assertFalse(response.getStatus());
        assertEquals("Email does not exist", response.getMessage());
    }

    @Test
    void loginUser_WrongPassword_ReturnsError() {
        when(userRepo.findByEmail(anyString())).thenReturn(existingUser);
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(false);
        
        LoginResponse response = userService.loginUser(validLoginDto);
        
        assertFalse(response.getStatus());
        assertEquals("Password does not match", response.getMessage());
    }

    @Test
    void logoutUser_ValidSession_ReturnsTrue() {
        when(sessionRepo.findBySessionId(anyString())).thenReturn(Optional.of(activeSession));
        
        boolean result = userService.logoutUser("valid-session-id");
        
        assertTrue(result);
        assertFalse(activeSession.isActive());
        verify(sessionRepo, times(1)).save(activeSession);
    }

    @Test
    void logoutUser_InvalidSession_ReturnsFalse() {
        when(sessionRepo.findBySessionId(anyString())).thenReturn(Optional.empty());
        
        boolean result = userService.logoutUser("invalid-session-id");
        
        assertFalse(result);
    }

    @Test
    void isValidSession_ValidActiveSession_ReturnsTrue() {
        when(sessionRepo.findBySessionId(anyString())).thenReturn(Optional.of(activeSession));
        
        boolean result = userService.isValidSession("valid-session");
        
        assertTrue(result);
    }

    @Test
    void isValidSession_ExpiredSession_ReturnsFalse() {
        SessionInfo expiredSession = new SessionInfo();
        expiredSession.setActive(true);
        expiredSession.setExpiresAt(LocalDateTime.now().minusHours(1));
        when(sessionRepo.findBySessionId(anyString())).thenReturn(Optional.of(expiredSession));
        
        boolean result = userService.isValidSession("expired-session");
        
        assertFalse(result);
    }

    @Test
    void isValidSession_InactiveSession_ReturnsFalse() {
        SessionInfo inactiveSession = new SessionInfo();
        inactiveSession.setActive(false);
        inactiveSession.setExpiresAt(LocalDateTime.now().plusHours(1));
        when(sessionRepo.findBySessionId(anyString())).thenReturn(Optional.of(inactiveSession));
        
        boolean result = userService.isValidSession("inactive-session");
        
        assertFalse(result);
    }

    @Test
    void getAllActiveSessionsForUser_ReturnsSessions() {
        List<SessionInfo> sessions = Arrays.asList(activeSession);
        when(sessionRepo.findByUser_UseridAndActiveTrue(anyInt())).thenReturn(sessions);
        
        List<SessionInfo> result = userService.getAllActiveSessionsForUser(1);
        
        assertEquals(1, result.size());
        assertEquals(activeSession, result.get(0));
    }

    @Test
    void getUserFromSession_ValidSession_ReturnsUser() {
        when(sessionRepo.findBySessionId(anyString())).thenReturn(Optional.of(activeSession));
        
        Optional<User> result = userService.getUserFromSession("valid-session");
        
        assertTrue(result.isPresent());
        assertEquals(existingUser, result.get());
    }

    @Test
    void getUserFromSession_InvalidSession_ReturnsEmpty() {
        when(sessionRepo.findBySessionId(anyString())).thenReturn(Optional.empty());
        
        Optional<User> result = userService.getUserFromSession("invalid-session");
        
        assertFalse(result.isPresent());
    }

    @Test
    void isAdmin_AdminUser_ReturnsTrue() {
        User adminUser = new User(2, "admin", "admin@example.com", "password", "ADMIN");
        SessionInfo adminSession = new SessionInfo("admin-session", adminUser, LocalDateTime.now().plusHours(1));
        when(sessionRepo.findBySessionId(anyString())).thenReturn(Optional.of(adminSession));
        
        boolean result = userService.isAdmin("admin-session");
        
        assertTrue(result);
    }

    @Test
    void isAdmin_RegularUser_ReturnsFalse() {
        when(sessionRepo.findBySessionId(anyString())).thenReturn(Optional.of(activeSession));
        
        boolean result = userService.isAdmin("user-session");
        
        assertFalse(result);
    }

    @Test
    void validateProductNumbers_ValidNumbers_ReturnsTrue() {
        ProductsDto validProduct = new ProductsDto(
            1, "Product", 10, 9.99, "image.jpg", "Good", 1
        );
        
        boolean result = userService.validateProductNumbers(validProduct);
        
        assertTrue(result);
    }

    @Test
    void validateProductNumbers_NegativeStock_ReturnsFalse() {
        ProductsDto invalidProduct = new ProductsDto(
            1, "Product", -1, 9.99, "image.jpg", "Good", 1
        );
        
        boolean result = userService.validateProductNumbers(invalidProduct);
        
        assertFalse(result);
    }

    @Test
    void validateProductNumbers_NegativePrice_ReturnsFalse() {
        ProductsDto invalidProduct = new ProductsDto(
            1, "Product", 10, -1.0, "image.jpg", "Good", 1
        );
        
        boolean result = userService.validateProductNumbers(invalidProduct);
        
        assertFalse(result);
    }
}