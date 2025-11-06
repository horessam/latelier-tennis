package com.tennis.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Locale;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlayerData {
    private Integer rank;
    private Integer points;
    private Integer weight;
    private Integer height;
    private Integer age;
    private List<Integer> last;

    public double getWinRate() {
        if (last == null || last.isEmpty()) {
            return 0.0;
        }
        long wins = last.stream().filter(result -> result == 1).count();
        return (double) wins / last.size() * 100;
    }

    public double getWeightInKg() {
        return weight != null ? weight / 1000.0 : 0.0;
    }

    public double getHeightInMeters() {
        return height != null ? height / 100.0 : 0.0;
    }

    public String getBMI() {
        if (weight == null || height == null || height == 0) {
            return "N/A";
        }

        double bmi = getWeightInKg() / (getHeightInMeters() * getHeightInMeters());
        return String.format(Locale.US, "%.2f", bmi);
    }

}
