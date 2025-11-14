package com.travelai.service.impl;

import com.travelai.dto.LoginRequest;
import com.travelai.dto.LoginResponse;
import com.travelai.dto.RegisterRequest;
import com.travelai.dto.UserDTO;
import com.travelai.entity.User;
import com.travelai.exception.BusinessException;
import com.travelai.mapper.UserMapper;
import com.travelai.service.UserService;
import com.travelai.util.JwtUtil;
import com.travelai.util.UserContext;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private JwtUtil jwtUtil;

    private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Override
    public UserDTO register(RegisterRequest request) {
        // 检查用户名是否已存在
        User existingUser = userMapper.findByUsername(request.getUsername());
        if (existingUser != null) {
            throw new BusinessException(5001, "用户名已存在");
        }

        // 创建新用户
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(encodePassword(request.getPassword()));
        user.setEmail(request.getEmail());

        // 保存用户
        userMapper.insert(user);

        // 返回用户DTO
        return convertToDTO(user);
    }

    @Override
    public LoginResponse login(LoginRequest request) {
        // 查找用户
        User user = userMapper.findByUsername(request.getUsername());
        if (user == null || !verifyPassword(request.getPassword(), user.getPassword())) {
            throw new BusinessException(5002, "用户名或密码错误");
        }

        // 生成token
        String token = jwtUtil.generateToken(user.getId());
        log.info("生成的JWT Token: {}", token);

        UserContext.setUserId(user.getId());

        // 创建响应
        LoginResponse response = new LoginResponse();
        response.setToken(token);
        response.setUser(convertToDTO(user));

        return response;
    }

    @Override
    public UserDTO getUserById(Long id) {
        User user = userMapper.findById(id);
        if (user == null) {
            throw new BusinessException(404, "用户不存在");
        }
        return convertToDTO(user);
    }

    @Override
    public String encodePassword(String password) {
        return passwordEncoder.encode(password);
    }

    @Override
    public boolean verifyPassword(String rawPassword, String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }

    // 将实体转换为DTO
    private UserDTO convertToDTO(User user) {
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        return dto;
    }
}
