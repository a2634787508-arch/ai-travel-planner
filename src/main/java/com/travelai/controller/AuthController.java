package com.travelai.controller;

import com.travelai.dto.LoginRequest;
import com.travelai.dto.LoginResponse;
import com.travelai.dto.RegisterRequest;
import com.travelai.dto.Result;
import com.travelai.dto.UserDTO;
import com.travelai.service.UserService;
import com.travelai.util.UserContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    // 用户注册
    @PostMapping("/register")
    public Result<UserDTO> register(@RequestBody RegisterRequest request) {
        UserDTO userDTO = userService.register(request);
        return Result.success(userDTO);
    }

    // 用户登录
    @PostMapping("/login")
    public Result<LoginResponse> login(@RequestBody LoginRequest request) {
        LoginResponse loginResponse = userService.login(request);
        return Result.success(loginResponse);
    }

    // 用户退出
    @PostMapping("/logout")
    public Result<?> logout() {
        // 清除ThreadLocal中的用户信息
        UserContext.clear();
        return Result.success();
    }
    
    // 验证用户登录状态
    @GetMapping("/validate")
    public Result<?> validate() {
        return Result.success();
    }
}
