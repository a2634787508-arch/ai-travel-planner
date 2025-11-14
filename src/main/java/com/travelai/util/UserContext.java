package com.travelai.util;

public class UserContext {
    private static final ThreadLocal<Long> USER_ID_HOLDER = new ThreadLocal<>();

    // 设置当前用户ID
    public static void setUserId(Long userId) {
        USER_ID_HOLDER.set(userId);
    }

    // 获取当前用户ID
    public static Long getUserId() {
        return USER_ID_HOLDER.get();
    }

    // 清除当前用户ID
    public static void clear() {
        USER_ID_HOLDER.remove();
    }

    // 判断是否登录
    public static boolean isLoggedIn() {
        return USER_ID_HOLDER.get() != null;
    }
    
    // 获取当前用户ID（为兼容控制器调用）
    public static Long getCurrentUserId() {
        return getUserId();
    }
}
