package com.travelai.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.util.Date;

@Data
public class GeneratePlanRequest {
    private String destination;
    private Date startDate;
    private Date endDate;
    private BigDecimal totalBudget;
    private Integer peopleCount;
    private String preferences;
    private String textInput; // 用户的文本描述
}
