package com.authenthication.auth_session.Service.Implementation;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import com.authenthication.auth_session.Dto.LoginDto;
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
    }



    @Test
void registerUser_ValidInput_ReturnsSuccessResponse() {
    when(userRepo.findByEmail(anyString())).thenReturn(null);
    when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
    when(userRepo.save(any(User.class))).thenReturn(existingUser);

    AddUserResponse response = userService.addUser(validUserDto);

    assertTrue(response.getStatus());
    assertEquals("User registered successfully", response.getMessage());
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
void loginUser_ValidCredentials_ReturnsSuccess() {
    when(userRepo.findByEmail(anyString())).thenReturn(existingUser);
    when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);
    when(sessionRepo.save(any(SessionInfo.class))).thenReturn(new SessionInfo());

    LoginResponse response = userService.loginUser(validLoginDto);

    assertTrue(response.getStatus());
    assertEquals("Login Success", response.getMessage());
    assertNotNull(response.getSessionId());
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
    SessionInfo session = new SessionInfo();
    when(sessionRepo.findBySessionId(anyString())).thenReturn(Optional.of(session));
    
    boolean result = userService.logoutUser("valid-session-id");
    
    assertTrue(result);
    assertFalse(session.isActive());
    verify(sessionRepo, times(1)).save(session);
}

@Test
void logoutUser_InvalidSession_ReturnsFalse() {
    when(sessionRepo.findBySessionId(anyString())).thenReturn(Optional.empty());
    
    boolean result = userService.logoutUser("invalid-session-id");
    
    assertFalse(result);
}

@Test
void isValidSession_ValidActiveSession_ReturnsTrue() {
    SessionInfo activeSession = new SessionInfo();
    activeSession.setActive(true);
    activeSession.setExpiresAt(LocalDateTime.now().plusHours(1));
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
}