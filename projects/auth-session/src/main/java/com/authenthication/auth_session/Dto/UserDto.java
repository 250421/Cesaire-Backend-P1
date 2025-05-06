package com.authenthication.auth_session.Dto;

// Removed unused imports

public class UserDto {

 
    private int userid;
    private String username;
    private String email;
    private String password;
    private String role;

    public UserDto(int userid, String username, String email, String password, String role) {
        this.userid = userid;
        this.username = username;
        this.email = email;
        this.password = password;
        this.role = role;
    }

    public UserDto() {
    }

    public int getUserid() {
        return userid;
    }

    public void setUserid(int employeeid) {
        this.userid = employeeid;
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    @Override
    public String toString() {
        return "UserDTO{" +
                "userid=" + userid +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", role='" + role + '\''+
                 '}';


}
}



