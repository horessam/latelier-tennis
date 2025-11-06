package com.tennis.infrastructure.adapter.in.rest.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.tennis.domain.model.Player;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PlayerStatsResponse {
    private Long id;
    private String firstname;
    private String lastname;
    private String shortname;
    private String sex;
    private CountryDto country;
    private String picture;
    private PlayerDataDto data;

    public static PlayerStatsResponse fromDomain(Player player) {
        return PlayerStatsResponse.builder()
                .id(player.getId())
                .firstname(player.getFirstname())
                .lastname(player.getLastname())
                .shortname(player.getShortname())
                .sex(player.getSex())
                .country(player.hasValidCountry() ?
                        CountryDto.builder()
                                .picture(player.getCountry().getPicture())
                                .code(player.getCountry().getCode())
                                .build() : null)
                .picture(player.getPicture())
                .data(player.getData() != null ?
                        PlayerDataDto.builder()
                                .rank(player.getData().getRank())
                                .points(player.getData().getPoints())
                                .weight(player.getData().getWeight())
                                .height(player.getData().getHeight())
                                .age(player.getData().getAge())
                                .last(player.getData().getLast())
                                .winRate(player.getData().getWinRate())
                                .bmi(player.getData().getBMI())
                                .build() : null)
                .build();
    }
}
