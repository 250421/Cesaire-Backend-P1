package com.authenthication.auth_session.response;

public class AddUserResponse {
    private String message;
    private Boolean status;
    private String username;  // Additional field for successful registration
    private String email;     // Additional field for successful registration
    private String role;

    public AddUserResponse(String message, Boolean status) {
        this.message = message;
        this.status = status;
    }

    public AddUserResponse(String message, Boolean status, String username, String email, String role) {
        this.message = message;
        this.status = status;
        this.username = username;
        this.email = email;
        this.role = role;
    }

    public AddUserResponse() {
    }

    // Getters and Setters
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

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    @Override
    public String toString() {
        return "AddUserResponse{" +
                "message='" + message + '\'' +
                ", status=" + status +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}


