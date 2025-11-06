package com.tennis.infrastructure.adapter.in.rest.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlayerDataDto {
    private Integer rank;
    private Integer points;
    private Integer weight;
    private Integer height;
    private Integer age;
    private List<Integer> last;
    private Double winRate;
    private String bmi;
}
