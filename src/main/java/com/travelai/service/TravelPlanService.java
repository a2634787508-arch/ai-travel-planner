package com.travelai.service;

import com.travelai.dto.TravelPlanDTO;
import com.travelai.entity.TravelPlan;

import java.util.List;

public interface TravelPlanService {
    // 获取用户所有行程
    List<TravelPlanDTO> getUserTravelPlans(Long userId);
    
    // 获取行程详情
    TravelPlanDTO getTravelPlanById(Long id, Long userId);
    
    // 创建新行程
    TravelPlanDTO createTravelPlan(TravelPlan travelPlan);
    
    // 更新行程
    TravelPlanDTO updateTravelPlan(Long id, TravelPlanDTO travelPlanDTO, Long userId);
    
    // 删除行程
    void deleteTravelPlan(Long id, Long userId);
}
