package com.travelai.mapper;

import com.travelai.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface UserMapper {
    // 根据用户名查询用户
    User findByUsername(@Param("username") String username);
    
    // 根据ID查询用户
    User findById(@Param("id") Long id);
    
    // 插入用户
    int insert(User user);
    
    // 更新用户
    int update(User user);
}
