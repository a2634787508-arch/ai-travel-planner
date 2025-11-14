package com.travelai.controller;

import com.travelai.dto.Result;
import com.travelai.dto.TravelPlanDTO;
import com.travelai.entity.TravelPlan;
import com.travelai.service.TravelPlanService;
import com.travelai.util.UserContext;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/travel-plans")
public class TravelPlanController {

    @Autowired
    private TravelPlanService travelPlanService;

    // 获取用户所有行程
    @GetMapping
    public Result<List<TravelPlanDTO>> getUserTravelPlans() {
        Long userId = UserContext.getCurrentUserId();
        List<TravelPlanDTO> travelPlans = travelPlanService.getUserTravelPlans(userId);
        return Result.success(travelPlans);
    }

    // 获取行程详情
    @GetMapping("/{id}")
    public Result<TravelPlanDTO> getTravelPlanById(@PathVariable Long id) {
        Long userId = UserContext.getCurrentUserId();
        TravelPlanDTO travelPlan = travelPlanService.getTravelPlanById(id, userId);
        return Result.success(travelPlan);
    }

    // 创建新行程
    @PostMapping
    public Result<Long> createTravelPlan(@RequestBody TravelPlanDTO travelPlanDTO) {
        Long userId = UserContext.getCurrentUserId();
        TravelPlan travelPlan = new TravelPlan();
        // 复制属性
        BeanUtils.copyProperties(travelPlanDTO, travelPlan);
        travelPlan.setUserId(userId);
        
        TravelPlanDTO createdPlan = travelPlanService.createTravelPlan(travelPlan);
        return Result.success(createdPlan.getId());
    }

    // 更新行程
    @PutMapping("/{id}")
    public Result<TravelPlanDTO> updateTravelPlan(@PathVariable Long id, @RequestBody TravelPlanDTO travelPlanDTO) {
        Long userId = UserContext.getCurrentUserId();
        TravelPlanDTO updatedPlan = travelPlanService.updateTravelPlan(id, travelPlanDTO, userId);
        return Result.success(updatedPlan);
    }

    // 删除行程
    @DeleteMapping("/{id}")
    public Result<Boolean> deleteTravelPlan(@PathVariable Long id) {
        Long userId = UserContext.getCurrentUserId();
        travelPlanService.deleteTravelPlan(id, userId);
        return Result.success(true);
    }
}