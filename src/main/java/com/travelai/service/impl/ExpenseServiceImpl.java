package com.travelai.service.impl;

import com.travelai.dto.ExpenseDTO;
import com.travelai.entity.Expense;
import com.travelai.exception.BusinessException;
import com.travelai.mapper.ExpenseMapper;
import com.travelai.mapper.TravelPlanMapper;
import com.travelai.service.ExpenseService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ExpenseServiceImpl implements ExpenseService {

    @Autowired
    private ExpenseMapper expenseMapper;

    @Autowired
    private TravelPlanMapper travelPlanMapper;

    @Override
    public List<ExpenseDTO> getExpensesByTravelPlanId(Long travelPlanId, Long userId) {
        // 验证行程是否存在且属于当前用户
        validateTravelPlanAccess(travelPlanId, userId);
        
        List<Expense> expenses = expenseMapper.findByTravelPlanId(travelPlanId);
        List<ExpenseDTO> dtoList = new ArrayList<>();
        for (Expense expense : expenses) {
            ExpenseDTO dto = new ExpenseDTO();
            BeanUtils.copyProperties(expense, dto);
            dtoList.add(dto);
        }
        return dtoList;
    }

    @Override
    public ExpenseDTO getExpenseById(Long id) {
        Expense expense = expenseMapper.findById(id);
        if (expense == null) {
            throw new BusinessException(404, "费用记录不存在");
        }
        ExpenseDTO dto = new ExpenseDTO();
        BeanUtils.copyProperties(expense, dto);
        return dto;
    }

    @Override
    public ExpenseDTO createExpense(Expense expense, Long userId) {
        // 验证行程是否存在且属于当前用户
        validateTravelPlanAccess(expense.getTravelPlanId(), userId);
        
        int result = expenseMapper.insert(expense);
        if (result <= 0) {
            throw new BusinessException(500, "创建费用记录失败");
        }
        ExpenseDTO dto = new ExpenseDTO();
        BeanUtils.copyProperties(expense, dto);
        return dto;
    }

    @Override
    public ExpenseDTO updateExpense(Long id, ExpenseDTO expenseDTO, Long userId) {
        // 获取费用记录
        Expense existingExpense = expenseMapper.findById(id);
        if (existingExpense == null) {
            throw new BusinessException(404, "费用记录不存在");
        }
        
        // 验证行程是否存在且属于当前用户
        validateTravelPlanAccess(existingExpense.getTravelPlanId(), userId);
        
        // 更新费用记录
        Expense updatedExpense = new Expense();
        BeanUtils.copyProperties(expenseDTO, updatedExpense);
        updatedExpense.setId(id);
        updatedExpense.setTravelPlanId(existingExpense.getTravelPlanId()); // 不允许修改行程ID
        
        int result = expenseMapper.update(updatedExpense);
        if (result <= 0) {
            throw new BusinessException(500, "更新费用记录失败");
        }
        
        // 返回更新后的费用记录
        return getExpenseById(id);
    }

    @Override
    public void deleteExpense(Long id, Long userId) {
        // 获取费用记录
        Expense expense = expenseMapper.findById(id);
        if (expense == null) {
            throw new BusinessException(404, "费用记录不存在");
        }
        
        // 验证行程是否存在且属于当前用户
        validateTravelPlanAccess(expense.getTravelPlanId(), userId);
        
        int result = expenseMapper.delete(id);
        if (result <= 0) {
            throw new BusinessException(500, "删除费用记录失败");
        }
    }
    
    // 验证行程访问权限
    private void validateTravelPlanAccess(Long travelPlanId, Long userId) {
        com.travelai.entity.TravelPlan travelPlan = travelPlanMapper.findById(travelPlanId);
        if (travelPlan == null || !travelPlan.getUserId().equals(userId)) {
            throw new BusinessException(404, "行程不存在或无权访问");
        }
    }
}