package com.travelai.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

import lombok.Getter;

@Data
@Getter
public class TravelPlanDTO {
    private Long id;
    private String destination;
    private Date startDate;
    private Date endDate;
    private BigDecimal totalBudget;
    private Integer peopleCount;
    private String preferences;
    private String generatedItinerary;
    private Date createdTime;
}
