package com.tennis.domain.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Player {
    private Long id;
    private String firstname;
    private String lastname;
    private String shortname;
    private String sex;
    private Country country;
    private String picture;
    private PlayerData data;

    public String getFullName() {
        return firstname + " " + lastname;
    }

    public boolean hasValidCountry() {
        return country != null && country.getCode() != null;
    }

    public boolean hasValidBMIData() {
        return data != null
                && data.getWeight() != null
                && data.getHeight() != null
                && data.getHeight() > 0;
    }

    public double calculateBMI() {
        if (!hasValidBMIData()) {
            return 0.0;
        }

        double heightInMeters = data.getHeightInMeters();
        double weightInKg = data.getWeightInKg();
        return weightInKg / (heightInMeters * heightInMeters);
    }

    public boolean hasData() {
        return data != null;
    }

}
