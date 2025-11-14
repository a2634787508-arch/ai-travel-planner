package com.travelai.controller;

import com.travelai.dto.GeneratePlanRequest;
import com.travelai.dto.Result;
import com.travelai.service.AIService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/ai")
public class AIController {

    @Autowired
    private AIService aiService;

    // AI生成行程
    @PostMapping("/generate-plan")
    public Result<Map<String, String>> generatePlan(@RequestBody GeneratePlanRequest request) {
        String generatedItinerary = aiService.generateTravelPlan(request);
        Map<String, String> resultMap = new HashMap<>();
        resultMap.put("generatedItinerary", generatedItinerary);
        return Result.success(resultMap);
    }

    // 根据文本生成行程
    @PostMapping("/generate-plan-from-text")
    public Result<Map<String, String>> generatePlanFromText(@RequestBody Map<String, String> request) {
        String text = request.get("text");
        if (text == null || text.trim().isEmpty()) {
            return Result.fail(400, "文本内容不能为空");
        }
        String generatedItinerary = aiService.generatePlanFromText(text);
        Map<String, String> resultMap = new HashMap<>();
        resultMap.put("generatedItinerary", generatedItinerary);
        return Result.success(resultMap);
    }
}
