package com.travelai.service;

import com.travelai.dto.ExpenseDTO;
import com.travelai.entity.Expense;

import java.util.List;

public interface ExpenseService {
    // 获取行程的所有费用记录
    List<ExpenseDTO> getExpensesByTravelPlanId(Long travelPlanId, Long userId);
    
    // 获取费用详情
    ExpenseDTO getExpenseById(Long id);
    
    // 创建新费用记录
    ExpenseDTO createExpense(Expense expense, Long userId);
    
    // 更新费用记录
    ExpenseDTO updateExpense(Long id, ExpenseDTO expenseDTO, Long userId);
    
    // 删除费用记录
    void deleteExpense(Long id, Long userId);
}
