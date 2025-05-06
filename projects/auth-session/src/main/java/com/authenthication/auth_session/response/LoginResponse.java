package com.authenthication.auth_session.response;

import java.time.LocalDateTime;

public class LoginResponse {

    String message;
    Boolean status;
    String sessionId;  // Add this field
    LocalDateTime expiresAt;  // Add this field

    // Update constructors
    public LoginResponse(String message, Boolean status, String sessionId, LocalDateTime expiresAt) {
        this.message = message;
        this.status = status;
        this.sessionId = sessionId;
        this.expiresAt = expiresAt;
    }
    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(LocalDateTime expiresAt) {
        this.expiresAt = expiresAt;
    }
    
    public LoginResponse(String message, Boolean status) {
        this.message = message;
        this.status = status;
    }

    public LoginResponse() {
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "LoginResponse{" +
                "message='" + message + '\'' +
                ", status=" + status +
                '}';
    }


}


