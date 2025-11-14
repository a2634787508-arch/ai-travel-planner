package com.travelai.mapper;

import com.travelai.entity.TravelPlan;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface TravelPlanMapper {
    // 根据用户ID获取所有行程
    List<TravelPlan> findByUserId(Long userId);
    
    // 根据行程ID获取行程详情
    TravelPlan findById(Long id);
    
    // 创建新行程
    int insert(TravelPlan travelPlan);
    
    // 更新行程
    int update(TravelPlan travelPlan);
    
    // 删除行程
    int delete(Long id, Long userId);
}
