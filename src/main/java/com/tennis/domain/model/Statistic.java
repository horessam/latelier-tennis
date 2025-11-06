package com.tennis.domain.model;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Statistic {
    private String countryCode;
    private double winRatio;
    private double averageBMI;
    private double medianHeight;
}
