package com.tennis.infrastructure.adapter.in.rest.dto;

import com.tennis.domain.model.Statistic;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CountryStatsResponse {
    private String countryCode;
    private Double winRatio;
    private Double averageBMI;
    private Double medianHeight;

    public static CountryStatsResponse fromDomain(Statistic stats) {
        return CountryStatsResponse.builder()
                .countryCode(stats.getCountryCode())
                .winRatio(Math.round(stats.getWinRatio() * 100.0) / 100.0)
                .averageBMI(Math.round(stats.getAverageBMI() * 100.0) / 100.0)
                .medianHeight(Math.round(stats.getMedianHeight() * 100.0) / 100.0)
                .build();
    }
}
