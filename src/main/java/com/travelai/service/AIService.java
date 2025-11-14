package com.travelai.service;

import com.travelai.dto.GeneratePlanRequest;

public interface AIService {
    // 生成旅行计划
    String generateTravelPlan(GeneratePlanRequest request);
    
    // 根据文本描述生成旅行计划
    String generatePlanFromText(String text);
}
