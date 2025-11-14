package com.travelai.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class ExpenseDTO {
    private Long id;
    private Long travelPlanId;
    private String category;
    private String itemName;
    private BigDecimal amount;
    private String note;
    private Date createdTime;
}
