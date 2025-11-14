package com.travelai.mapper;

import com.travelai.entity.Expense;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ExpenseMapper {
    // 根据行程ID获取所有费用记录
    List<Expense> findByTravelPlanId(Long travelPlanId);
    
    // 根据费用记录ID获取费用详情
    Expense findById(Long id);
    
    // 创建新费用记录
    int insert(Expense expense);
    
    // 更新费用记录
    int update(Expense expense);
    
    // 删除费用记录
    int delete(Long id);
}
