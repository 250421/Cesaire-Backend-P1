package com.authenthication.auth_session.Service;


import com.authenthication.auth_session.Dto.LoginDto;
import com.authenthication.auth_session.Dto.UserDto;
import com.authenthication.auth_session.response.LoginResponse;
import com.authenthication.auth_session.response.AddUserResponse;


public interface UserService {

    LoginResponse loginUser(LoginDto loginDto);
    AddUserResponse addUser(UserDto userDto);  

}


