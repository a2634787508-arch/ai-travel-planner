package com.travelai.config;

import com.travelai.util.JwtUtil;
import com.travelai.util.UserContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class AuthInterceptor implements HandlerInterceptor {

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        // 跳过登录和注册接口
        String uri = request.getRequestURI();
        
        // 排除静态资源访问
        if (uri.contains("/api/auth/login") || uri.contains("/api/auth/register") || uri.contains("/api/auth/validate") || uri.contains("/api/voice/transcribe") ||
            uri.endsWith(".html") || uri.endsWith(".css") || uri.endsWith(".js") || uri.contains("/css/") || uri.contains("/js/") || uri.contains("/img/")) {
            return true;
        }

        // 获取Authorization头
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            log.info("Authorization header is missing or invalid");
            response.setStatus(401);
            return false;
        }

        // 提取和验证token
        String token = jwtUtil.extractToken(authHeader);
        if (token == null || !jwtUtil.validateToken(token)) {
            log.info("Token is missing or invalid");
            response.setStatus(401);
            return false;
        }

        log.info("Token is valid:{}", token);
        // 获取用户ID并设置到ThreadLocal
        Long userId = jwtUtil.getUserIdFromToken(token);
        UserContext.setUserId(userId);

        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        // 清除ThreadLocal中的用户信息，避免内存泄漏
        UserContext.clear();
    }
}
