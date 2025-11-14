package com.travelai.service.impl;

import com.alibaba.dashscope.aigc.generation.Generation;
import com.alibaba.dashscope.aigc.generation.GenerationParam;
import com.alibaba.dashscope.aigc.generation.GenerationResult;
import com.alibaba.dashscope.common.Message;
import com.alibaba.dashscope.common.Role;
import com.alibaba.dashscope.exception.ApiException;
import com.alibaba.dashscope.exception.InputRequiredException;
import com.alibaba.dashscope.exception.NoApiKeyException;
import com.travelai.dto.GeneratePlanRequest;
import com.travelai.exception.BusinessException;
import com.travelai.service.AIService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

@Service
public class AIServiceImpl implements AIService {

    @Value("${alibaba.dashscope.api-key}")
    private String apiKey;

    @Override
    public String generateTravelPlan(GeneratePlanRequest request) {
        StringBuilder prompt = new StringBuilder();
        prompt.append("请为我生成一份详细的旅行计划，具体信息如下：\n");
        
        if (request.getDestination() != null) {
            prompt.append("目的地：").append(request.getDestination()).append("\n");
        }
        
        if (request.getStartDate() != null && request.getEndDate() != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日");
            prompt.append("旅行日期：").append(sdf.format(request.getStartDate()))
                  .append(" 至 ").append(sdf.format(request.getEndDate())).append("\n");
        }
        
        if (request.getTotalBudget() != null) {
            prompt.append("预算：").append(request.getTotalBudget()).append("元\n");
        }
        
        if (request.getPeopleCount() != null) {
            prompt.append("人数：").append(request.getPeopleCount()).append("人\n");
        }
        
        if (request.getPreferences() != null && !request.getPreferences().isEmpty()) {
            prompt.append("旅行偏好：").append(request.getPreferences()).append("\n");
        }
        
        if (request.getTextInput() != null && !request.getTextInput().isEmpty()) {
            prompt.append("额外要求：").append(request.getTextInput()).append("\n");
        }
        
        prompt.append("\n请生成一份包含以下内容的详细旅行计划：\n");
        prompt.append("1. 每日行程安排（时间、地点、活动）\n");
        prompt.append("2. 交通建议\n");
        prompt.append("3. 住宿推荐\n");
        prompt.append("4. 美食推荐\n");
        prompt.append("5. 景点介绍\n");
        prompt.append("6. 费用预算明细\n");
        prompt.append("7. 注意事项\n");
        prompt.append("\n请使用清晰的格式，分点说明，便于阅读。");
        
        return callAI(prompt.toString());
    }

    @Override
    public String generatePlanFromText(String text) {
        StringBuilder prompt = new StringBuilder();
        prompt.append("请根据以下文本描述，推测旅行需求并生成一份详细的旅行计划：\n\n");
        prompt.append(text).append("\n\n");
        prompt.append("请推测目的地、日期、预算、人数、旅行偏好等信息，然后生成一份包含以下内容的详细旅行计划：\n");
        prompt.append("1. 每日行程安排（时间、地点、活动）\n");
        prompt.append("2. 交通建议\n");
        prompt.append("3. 住宿推荐\n");
        prompt.append("4. 美食推荐\n");
        prompt.append("5. 景点介绍\n");
        prompt.append("6. 费用预算明细\n");
        prompt.append("7. 注意事项\n");
        prompt.append("\n请使用清晰的格式，分点说明，便于阅读。");
        
        return callAI(prompt.toString());
    }

    // 调用阿里云百炼API
    private String callAI(String prompt) {
        try {
            List<Message> messages = new ArrayList<>();
            messages.add(Message.builder()
                    .role(Role.SYSTEM.getValue())
                    .content("你是一位专业的旅行规划师，擅长根据用户需求生成详细、实用的旅行计划。")
                    .build());
            messages.add(Message.builder()
                    .role(Role.USER.getValue())
                    .content(prompt)
                    .build());

            GenerationParam param = GenerationParam.builder()
                    .model("qwen-flash")
                    .messages(messages)
                    .apiKey(apiKey)
                    .build();

            Generation gen = new Generation();
            GenerationResult result = gen.call(param);

            if (result.getOutput() != null && result.getOutput().getChoices() != null && !result.getOutput().getChoices().isEmpty()) {
                return result.getOutput().getChoices().get(0).getMessage().getContent();
            } else {
                throw new BusinessException(5004, "AI生成失败，请重试");
            }
        } catch (ApiException | InputRequiredException | NoApiKeyException e) {
            throw new BusinessException(5004, "AI服务调用失败：" + e.getMessage());
        }
    }
}
