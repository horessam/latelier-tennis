package com.tennis.infrastructure.adapter.in.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tennis.domain.model.Country;
import com.tennis.domain.model.Player;
import com.tennis.domain.model.PlayerData;
import com.tennis.domain.model.Statistic;
import com.tennis.domain.port.in.*;
import com.tennis.infrastructure.adapter.in.rest.dto.PlayerCreateRequest;
import com.tennis.infrastructure.adapter.in.rest.dto.PlayerUpdateRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PlayerStatsController.class)
class PlayerStatsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private GetPlayerStatsUseCase getPlayerStatsUseCase;

    @MockBean
    private GetPlayerRankingUseCase getPlayerRankingUseCase;

    @MockBean
    private GetCountryWithBestRatioUseCase getCountryWithBestRatioUseCase;

    @MockBean
    private CreatePlayerUseCase createPlayerUseCase;

    @MockBean
    private UpdatePlayerUseCase updatePlayerUseCase;

    @MockBean
    private DeletePlayerUseCase deletePlayerUseCase;

    private Player testPlayer;
    private List<Player> testPlayers;

    @BeforeEach
    void setUp() {
        Country france = Country.builder()
                .code("FRA")
                .picture("france.png")
                .build();

        testPlayer = Player.builder()
                .id(1L)
                .firstname("Rafael")
                .lastname("Nadal")
                .shortname("R.NAD")
                .sex("M")
                .country(france)
                .picture("nadal.png")
                .data(PlayerData.builder()
                        .rank(1)
                        .points(10000)
                        .weight(85000)
                        .height(185)
                        .age(37)
                        .last(Arrays.asList(1, 1, 1, 0, 1))
                        .build())
                .build();

        Player testPlayer2 = Player.builder()
                .id(2L)
                .firstname("Roger")
                .lastname("Federer")
                .shortname("R.FED")
                .sex("M")
                .country(Country.builder().code("SUI").picture("swiss.png").build())
                .data(PlayerData.builder()
                        .rank(2)
                        .points(9500)
                        .last(Arrays.asList(1, 1, 0, 1, 1))
                        .build())
                .build();

        testPlayers = Arrays.asList(testPlayer, testPlayer2);
    }

    @Test
    void shouldReturnAllPlayers() throws Exception {
        // Given
        when(getPlayerStatsUseCase.getAllPlayers()).thenReturn(testPlayers);

        // When & Then
        mockMvc.perform(get("/api/players")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].firstname", is("Rafael")))
                .andExpect(jsonPath("$[0].lastname", is("Nadal")))
                .andExpect(jsonPath("$[0].country.code", is("FRA")))
                .andExpect(jsonPath("$[0].data.rank", is(1)))
                .andExpect(jsonPath("$[0].data.winRate", notNullValue()))
                .andExpect(jsonPath("$[1].id", is(2)))
                .andExpect(jsonPath("$[1].firstname", is("Roger")));
    }

    @Test
    void shouldReturnPlayerById() throws Exception {
        // Given
        when(getPlayerStatsUseCase.getPlayerById(1L)).thenReturn(Optional.of(testPlayer));

        // When & Then
        mockMvc.perform(get("/api/players/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.firstname", is("Rafael")))
                .andExpect(jsonPath("$.lastname", is("Nadal")))
                .andExpect(jsonPath("$.shortname", is("R.NAD")))
                .andExpect(jsonPath("$.sex", is("M")))
                .andExpect(jsonPath("$.country.code", is("FRA")))
                .andExpect(jsonPath("$.data.rank", is(1)))
                .andExpect(jsonPath("$.data.points", is(10000)))
                .andExpect(jsonPath("$.data.weight", is(85000)))
                .andExpect(jsonPath("$.data.height", is(185)))
                .andExpect(jsonPath("$.data.age", is(37)))
                .andExpect(jsonPath("$.data.winRate", is(80.0)))
                .andExpect(jsonPath("$.data.bmi", notNullValue()));
    }

    @Test
    void shouldReturn404WhenPlayerNotFound() throws Exception {
        // Given
        when(getPlayerStatsUseCase.getPlayerById(anyLong())).thenReturn(Optional.empty());

        // When & Then
        mockMvc.perform(get("/api/players/999")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturnPlayersRanking() throws Exception {
        // Given
        when(getPlayerRankingUseCase.getPlayersSortedByRank()).thenReturn(testPlayers);

        // When & Then
        mockMvc.perform(get("/api/players/ranking")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].data.rank", is(1)))
                .andExpect(jsonPath("$[1].data.rank", is(2)));
    }

    @Test
    void shouldReturnCountryWithBestRatio() throws Exception {
        // Given
        Statistic stats = Statistic.builder()
                .countryCode("FRA")
                .winRatio(75.5)
                .averageBMI(23.45)
                .medianHeight(182.5)
                .build();

        when(getCountryWithBestRatioUseCase.getCountryWithBestWinRatio()).thenReturn(stats);

        // When & Then
        mockMvc.perform(get("/api/players/country/best-ratio")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.countryCode", is("FRA")))
                .andExpect(jsonPath("$.winRatio", is(75.5)))
                .andExpect(jsonPath("$.averageBMI", is(23.45)))
                .andExpect(jsonPath("$.medianHeight", is(182.5)));
    }

    @Test
    void shouldCreateNewPlayer() throws Exception {
        // Given
        PlayerCreateRequest request = PlayerCreateRequest.builder()
                .firstname("New")
                .lastname("Player")
                .shortname("N.PLA")
                .sex("M")
                .country(PlayerCreateRequest.CountryRequest.builder()
                        .code("FRA")
                        .picture("france.png")
                        .build())
                .data(PlayerCreateRequest.PlayerDataRequest.builder()
                        .rank(10)
                        .points(5000)
                        .weight(80000)
                        .height(180)
                        .age(25)
                        .last(Arrays.asList(1, 0, 1))
                        .build())
                .build();

        Player createdPlayer = request.toDomain();
        createdPlayer.setId(10L);

        when(createPlayerUseCase.createPlayer(any(Player.class))).thenReturn(createdPlayer);

        // When & Then
        mockMvc.perform(post("/api/players")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(10)))
                .andExpect(jsonPath("$.firstname", is("New")))
                .andExpect(jsonPath("$.lastname", is("Player")))
                .andExpect(jsonPath("$.country.code", is("FRA")))
                .andExpect(jsonPath("$.data.rank", is(10)));
    }

    @Test
    void shouldReturn400WhenCreatePlayerWithInvalidData() throws Exception {
        // Given
        PlayerCreateRequest invalidRequest = PlayerCreateRequest.builder()
                .lastname("Joueur")
                .build();

        // When & Then
        mockMvc.perform(post("/api/players")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldUpdatePlayer() throws Exception {
        // Given
        PlayerUpdateRequest request = PlayerUpdateRequest.builder()
                .firstname("Rafael Updated")
                .lastname("Nadal Updated")
                .data(PlayerUpdateRequest.PlayerDataRequest.builder()
                        .rank(2)
                        .points(9000)
                        .build())
                .build();

        Player updatedPlayer = testPlayer;
        updatedPlayer.setFirstname("Rafael Updated");
        updatedPlayer.setLastname("Nadal Updated");

        when(updatePlayerUseCase.updatePlayer(any(Long.class), any(Player.class)))
                .thenReturn(updatedPlayer);

        // When & Then
        mockMvc.perform(put("/api/players/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstname", is("Rafael Updated")))
                .andExpect(jsonPath("$.lastname", is("Nadal Updated")));
    }

    @Test
    void shouldReturn404WhenUpdateNonExistentPlayer() throws Exception {
        // Given
        PlayerUpdateRequest request = PlayerUpdateRequest.builder()
                .firstname("Test")
                .build();

        when(updatePlayerUseCase.updatePlayer(any(Long.class), any(Player.class)))
                .thenReturn(null);

        // When & Then
        mockMvc.perform(put("/api/players/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldDeletePlayer() throws Exception {
        // When & Then
        mockMvc.perform(delete("/api/players/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void shouldCalculateWinRateInResponse() throws Exception {
        // Given
        when(getPlayerStatsUseCase.getPlayerById(1L)).thenReturn(Optional.of(testPlayer));

        // When & Then
        mockMvc.perform(get("/api/players/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.last", hasSize(5)))
                .andExpect(jsonPath("$.data.winRate", is(80.0)));
    }

    @Test
    void shouldIncludeBMIInResponse() throws Exception {
        // Given
        when(getPlayerStatsUseCase.getPlayerById(1L)).thenReturn(Optional.of(testPlayer));

        // When & Then
        mockMvc.perform(get("/api/players/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.bmi", matchesPattern("\\d+\\.\\d{2}")));
    }
}