package com.travelai.entity;

import lombok.Data;
import java.math.BigDecimal;
import java.util.Date;

@Data
public class TravelPlan {
    private Long id;
    private Long userId;
    private String destination;
    private Date startDate;
    private Date endDate;
    private BigDecimal totalBudget;
    private Integer peopleCount;
    private String preferences;
    private String generatedItinerary;
    private Date createdTime;
}
