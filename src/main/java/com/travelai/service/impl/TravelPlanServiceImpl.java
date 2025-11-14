package com.travelai.service.impl;

import com.alibaba.dashscope.aigc.generation.Generation;
import com.alibaba.dashscope.aigc.generation.GenerationParam;
import com.alibaba.dashscope.aigc.generation.GenerationResult;
import com.alibaba.dashscope.common.Message;
import com.alibaba.dashscope.exception.ApiException;
import com.alibaba.dashscope.exception.InputRequiredException;
import com.alibaba.dashscope.exception.NoApiKeyException;
import com.alibaba.dashscope.utils.Constants;
import com.travelai.dto.TravelPlanDTO;
import com.travelai.entity.TravelPlan;
import com.travelai.exception.BusinessException;
import com.travelai.mapper.TravelPlanMapper;
import com.travelai.service.TravelPlanService;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class TravelPlanServiceImpl implements TravelPlanService {

    @Value("${alibaba.dashscope.api-key}")
    private String apiKey;

    @Autowired
    private TravelPlanMapper travelPlanMapper;

    @Override
    public List<TravelPlanDTO> getUserTravelPlans(Long userId) {
        List<TravelPlan> travelPlans = travelPlanMapper.findByUserId(userId);
        List<TravelPlanDTO> dtoList = new ArrayList<>();
        for (TravelPlan plan : travelPlans) {
            TravelPlanDTO dto = new TravelPlanDTO();
            BeanUtils.copyProperties(plan, dto);
            dtoList.add(dto);
        }
        return dtoList;
    }

    @Override
    public TravelPlanDTO getTravelPlanById(Long id, Long userId) {
        TravelPlan travelPlan = travelPlanMapper.findById(id);
        if (travelPlan == null || !travelPlan.getUserId().equals(userId)) {
            throw new BusinessException(404, "行程不存在");
        }
        TravelPlanDTO dto = new TravelPlanDTO();
        BeanUtils.copyProperties(travelPlan, dto);
        return dto;
    }

    @Override
    @Transactional
    public TravelPlanDTO createTravelPlan(TravelPlan travelPlan) {
        // 生成计划
        String travelPlanJson = generateTravelPlanWithAI(travelPlan);
        travelPlan.setGeneratedItinerary(travelPlanJson);

        log.info("生成的行程: {}", travelPlanJson);
        
        int result = travelPlanMapper.insert(travelPlan);
        if (result <= 0) {
            throw new BusinessException(500, "创建行程失败");
        }
        TravelPlanDTO dto = new TravelPlanDTO();
        BeanUtils.copyProperties(travelPlan, dto);
        return dto;
    }

    @Override
    public TravelPlanDTO updateTravelPlan(Long id, TravelPlanDTO travelPlanDTO, Long userId) {
        // 先检查行程是否存在且属于当前用户
        TravelPlan existingPlan = travelPlanMapper.findById(id);
        if (existingPlan == null || !existingPlan.getUserId().equals(userId)) {
            throw new BusinessException(404, "行程不存在或无权修改");
        }

        log.info("用户id:{}, 更新行程ID: {}, 数据: {}", id, travelPlanDTO);

        // 更新行程信息
        TravelPlan updatedPlan = new TravelPlan();
        BeanUtils.copyProperties(travelPlanDTO, updatedPlan);
        updatedPlan.setId(id);
        updatedPlan.setUserId(userId);

        int result = travelPlanMapper.update(updatedPlan);
        if (result <= 0) {
            throw new BusinessException(500, "更新行程失败");
        }

        // 返回更新后的行程
        return getTravelPlanById(id, userId);
    }

    @Override
    public void deleteTravelPlan(Long id, Long userId) {
        // 先检查行程是否存在且属于当前用户
        TravelPlan travelPlan = travelPlanMapper.findById(id);
        if (travelPlan == null || !travelPlan.getUserId().equals(userId)) {
            throw new BusinessException(404, "行程不存在或无权删除");
        }
        log.info("用户id:{}, 删除行程ID: {}", userId, id);

        int result = travelPlanMapper.delete(id, userId);
        if (result <= 0) {
            throw new BusinessException(500, "删除行程失败");
        }
    }

    

    /**
     * 使用阿里云百炼生成旅行规划
     * 
     * @param request 旅行计划请求
     * @return 生成的旅行规划JSON
     */
    private String generateTravelPlanWithAI(TravelPlan request) {
        try {
            // 设置API密钥
            Constants.apiKey = apiKey;

            // 构建prompt
            String prompt = buildTravelPlanPrompt(request);

            // 构建请求参数
            List<Message> messages = new ArrayList<>();
            messages.add(Message.builder().role("system").content("你是一位专业的旅行规划师，请根据用户提供的旅行需求，生成一份详细的结构化旅行规划。").build());
            messages.add(Message.builder().role("user").content(prompt).build());

            GenerationParam param = GenerationParam.builder()
                    .model("qwen-flash")
                    .messages(messages)
                    .resultFormat(GenerationParam.ResultFormat.MESSAGE)
                    .topP(0.8)
                    .topK(50)
                    .temperature(0.7f)
                    .build();

            // 创建Generation实例并调用API
            Generation gen = new Generation();
            GenerationResult result = gen.call(param);

            // 解析响应
            String planJson = result.getOutput().getChoices().get(0).getMessage().getContent();
            // 有可能是markdown的json代码块
            if (planJson.startsWith("```json") && planJson.endsWith("```")) {
                planJson = planJson.substring(7, planJson.length() - 3).trim();
            }
            return planJson;
        } catch (ApiException | InputRequiredException | NoApiKeyException e) {
            // 如果调用AI失败，打印错误日志并返回默认的空规划
            e.printStackTrace();
            return "{\"status\": \"error\", \"message\": \"生成规划失败\", \"data\": {}}";
        }
    }

    /**
     * 构建旅行规划的prompt
     * 
     * @param request 旅行计划请求
     * @return 构建好的prompt
     */
    private String buildTravelPlanPrompt(TravelPlan request) {
        StringBuilder prompt = new StringBuilder();

        prompt.append("请为我生成一份详细的旅行规划，以下是我的旅行需求：\n");
        prompt.append("1. 旅行目的地：")
                .append(request.getDestination())
                .append("\n");
        prompt.append("2. 旅行时间：")
                .append(request.getStartDate())
                .append(" 至 ")
                .append(request.getEndDate())
                .append("\n");

        // 计算旅行天数
        long days = (request.getEndDate().getTime() - request.getStartDate().getTime()) / (1000 * 60 * 60 * 24) + 1;
        prompt.append("3. 旅行天数：")
                .append(days)
                .append(" 天\n");

        prompt.append("4. 预算：")
                .append(request.getTotalBudget())
                .append(" 元\n");

        prompt.append("5. 人数：")
                .append(request.getPeopleCount())
                .append(" 人\n");

        if (request.getPreferences() != null && !request.getPreferences().trim().isEmpty()) {
            prompt.append("6. 旅行偏好：")
                    .append(request.getPreferences())
                    .append("\n");
        }

        prompt.append("\n请按照以下JSON格式返回规划结果：")
                .append("\n{")
                .append("\n  \"plan\": {")
                .append("\n    \"destination\": \"目的地名称\",")
                .append("\n    \"start_date\": \"开始日期\",")
                .append("\n    \"end_date\": \"结束日期\",")
                .append("\n    \"total_days\": 天数,")
                .append("\n    \"budget\": 预算,")
                .append("\n    \"people_count\": 人数,")
                .append("\n    \"days\": [")
                .append("\n      {")
                .append("\n        \"day\": 1,")
                .append("\n        \"date\": \"日期\",")
                .append("\n        \"title\": \"当天主题\",")
                .append("\n        \"schedule\": [")
                .append("\n          {")
                .append("\n            \"time\": \"时间\",")
                .append("\n            \"activity\": \"活动内容\",")
                .append("\n            \"location\": \"地点\",")
                .append("\n            \"description\": \"详细描述\",")
                .append("\n            \"cost\": 费用")
                .append("\n          }")
                .append("\n        ]")
                .append("\n      }")
                .append("\n    ]")
                .append("\n  }")
                .append("\n}")
                .append("\n\n请严格按照上述JSON格式返回，不要添加任何额外的解释或说明，确保JSON格式正确。");

        return prompt.toString();
    }
}
