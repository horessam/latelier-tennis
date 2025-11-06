package com.tennis.infrastructure.adapter.in.rest.dto;

import com.tennis.domain.model.Country;
import com.tennis.domain.model.Player;
import com.tennis.domain.model.PlayerData;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlayerUpdateRequest {

    private String firstname;
    private String lastname;
    private String shortname;
    private String sex;
    private CountryRequest country;
    private String picture;
    private PlayerDataRequest data;

    public Player toDomain() {
        return Player.builder()
                .firstname(firstname)
                .lastname(lastname)
                .shortname(shortname)
                .sex(sex)
                .country(country != null ? Country.builder()
                        .code(country.getCode())
                        .picture(country.getPicture())
                        .build() : null)
                .picture(picture)
                .data(data != null ? PlayerData.builder()
                        .rank(data.getRank())
                        .points(data.getPoints())
                        .weight(data.getWeight())
                        .height(data.getHeight())
                        .age(data.getAge())
                        .last(data.getLast())
                        .build() : null)
                .build();
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CountryRequest {
        private String code;
        private String picture;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PlayerDataRequest {
        private Integer rank;
        private Integer points;
        private Integer weight;
        private Integer height;
        private Integer age;
        private List<Integer> last;
    }
}
