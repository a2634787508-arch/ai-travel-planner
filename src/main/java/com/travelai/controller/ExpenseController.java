package com.travelai.controller;

import com.travelai.dto.ExpenseDTO;
import com.travelai.dto.Result;
import com.travelai.entity.Expense;
import com.travelai.service.ExpenseService;
import com.travelai.util.UserContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/expenses")
public class ExpenseController {

    @Autowired
    private ExpenseService expenseService;

    // 获取行程的所有费用记录
    @GetMapping("/travel-plan/{travelPlanId}")
    public Result<List<ExpenseDTO>> getExpensesByTravelPlanId(@PathVariable Long travelPlanId) {
        Long userId = UserContext.getCurrentUserId();
        List<ExpenseDTO> expenses = expenseService.getExpensesByTravelPlanId(travelPlanId, userId);
        return Result.success(expenses);
    }

    // 获取费用详情
    @GetMapping("/{id}")
    public Result<ExpenseDTO> getExpenseById(@PathVariable Long id) {
        ExpenseDTO expense = expenseService.getExpenseById(id);
        return Result.success(expense);
    }

    // 创建新费用记录
    @PostMapping
    public Result<ExpenseDTO> createExpense(@RequestBody ExpenseDTO expenseDTO) {
        Long userId = UserContext.getCurrentUserId();
        Expense expense = new Expense();
        expense.setTravelPlanId(expenseDTO.getTravelPlanId());
        expense.setCategory(expenseDTO.getCategory());
        expense.setItemName(expenseDTO.getItemName());
        expense.setAmount(expenseDTO.getAmount());
        expense.setNote(expenseDTO.getNote());
        
        ExpenseDTO createdExpense = expenseService.createExpense(expense, userId);
        return Result.success(createdExpense);
    }

    // 更新费用记录
    @PutMapping("/{id}")
    public Result<ExpenseDTO> updateExpense(@PathVariable Long id, @RequestBody ExpenseDTO expenseDTO) {
        Long userId = UserContext.getCurrentUserId();
        ExpenseDTO updatedExpense = expenseService.updateExpense(id, expenseDTO, userId);
        return Result.success(updatedExpense);
    }

    // 删除费用记录
    @DeleteMapping("/{id}")
    public Result<Boolean> deleteExpense(@PathVariable Long id) {
        Long userId = UserContext.getCurrentUserId();
        expenseService.deleteExpense(id, userId);
        return Result.success(true);
    }
}