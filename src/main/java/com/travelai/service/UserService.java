package com.travelai.service;

import com.travelai.dto.LoginRequest;
import com.travelai.dto.LoginResponse;
import com.travelai.dto.RegisterRequest;
import com.travelai.dto.UserDTO;

public interface UserService {
    // 用户注册
    UserDTO register(RegisterRequest request);
    
    // 用户登录
    LoginResponse login(LoginRequest request);
    
    // 根据ID获取用户信息
    UserDTO getUserById(Long id);
    
    // 密码加密
    String encodePassword(String password);
    
    // 密码验证
    boolean verifyPassword(String rawPassword, String encodedPassword);
}
