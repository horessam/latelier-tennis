package com.tennis.infrastructure.adapter.in.rest.dto;

import com.tennis.domain.model.Country;
import com.tennis.domain.model.Player;
import com.tennis.domain.model.PlayerData;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlayerCreateRequest {

    @NotBlank(message = "First name is required")
    private String firstname;

    @NotBlank(message = "Last name is required")
    private String lastname;

    private String shortname;
    private String sex;

    @NotNull(message = "Country is required")
    private CountryRequest country;

    private String picture;

    @NotNull(message = "Player data is required")
    private PlayerDataRequest data;

    public Player toDomain() {
        return Player.builder()
                .firstname(firstname)
                .lastname(lastname)
                .shortname(shortname)
                .sex(sex)
                .country(Country.builder()
                        .code(country.getCode())
                        .picture(country.getPicture())
                        .build())
                .picture(picture)
                .data(PlayerData.builder()
                        .rank(data.getRank())
                        .points(data.getPoints())
                        .weight(data.getWeight())
                        .height(data.getHeight())
                        .age(data.getAge())
                        .last(data.getLast())
                        .build())
                .build();
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CountryRequest {
        @NotBlank(message = "Country code is required")
        private String code;
        private String picture;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PlayerDataRequest {
        @NotNull(message = "Rank is required")
        private Integer rank;
        private Integer points;
        private Integer weight;
        private Integer height;
        private Integer age;
        private List<Integer> last;
    }
}
