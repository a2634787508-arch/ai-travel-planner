package com.travelai.entity;

import lombok.Data;
import java.math.BigDecimal;
import java.util.Date;

@Data
public class Expense {
    private Long id;
    private Long travelPlanId;
    private String category;
    private String itemName;
    private BigDecimal amount;
    private String note;
    private Date createdTime;
}
