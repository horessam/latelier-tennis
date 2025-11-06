package com.tennis.domain.service;

import com.tennis.domain.model.Country;
import com.tennis.domain.model.Player;
import com.tennis.domain.model.PlayerData;
import com.tennis.domain.model.Statistic;
import com.tennis.domain.port.out.PlayerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PlayerServiceTest {

    @Mock
    private PlayerRepository playerRepository;

    @InjectMocks
    private PlayerService playerService;

    private Player playerFRA1;
    private Player playerFRA2;
    private Player playerUSA;

    @BeforeEach
    void setUp() {
        Country france = Country.builder().code("FRA").picture("fr.png").build();
        Country usa = Country.builder().code("USA").picture("us.png").build();

        playerFRA1 = Player.builder()
                .id(1L)
                .firstname("Celine")
                .lastname("Dion")
                .country(france)
                .data(PlayerData.builder()
                        .rank(1)
                        .points(5000)
                        .weight(75000)
                        .height(180)
                        .age(28)
                        .last(Arrays.asList(1, 1, 1, 0, 1)) // 80% winrate
                        .build())
                .build();

        playerFRA2 = Player.builder()
                .id(2L)
                .firstname("Zinedine")
                .lastname("Zidane")
                .country(france)
                .data(PlayerData.builder()
                        .rank(2)
                        .points(4500)
                        .weight(80000)
                        .height(185)
                        .age(25)
                        .last(Arrays.asList(1, 0, 1, 0, 1)) // 60% winrate
                        .build())
                .build();

        playerUSA = Player.builder()
                .id(3L)
                .firstname("Michael")
                .lastname("Jackson")
                .country(usa)
                .data(PlayerData.builder()
                        .rank(3)
                        .points(4000)
                        .weight(78000)
                        .height(183)
                        .age(30)
                        .last(Arrays.asList(1, 0, 0, 1, 0)) // 40% winrate
                        .build())
                .build();
    }

    @Test
    void shouldReturnAllPlayers() {
        // Given
        List<Player> expectedPlayers = Arrays.asList(playerFRA1, playerFRA2, playerUSA);
        when(playerRepository.findAll()).thenReturn(expectedPlayers);

        // When
        List<Player> actualPlayers = playerService.getAllPlayers();

        // Then
        assertThat(actualPlayers).hasSize(3);
        assertThat(actualPlayers).containsExactlyInAnyOrderElementsOf(expectedPlayers);
    }

    @Test
    void shouldReturnPlayerById() {
        // Given
        when(playerRepository.findById(1L)).thenReturn(Optional.of(playerFRA1));

        // When
        Optional<Player> result = playerService.getPlayerById(1L);

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(1L);
        assertThat(result.get().getFullName()).isEqualTo("Celine Dion");
    }

    @Test
    void shouldReturnEmptyWhenPlayerNotFound() {
        // Given
        when(playerRepository.findById(999L)).thenReturn(Optional.empty());

        // When
        Optional<Player> result = playerService.getPlayerById(999L);

        // Then
        assertThat(result).isEmpty();
    }

    @Test
    void shouldReturnPlayersSortedByRank() {
        // Given
        List<Player> players = Arrays.asList(playerUSA, playerFRA2, playerFRA1);
        when(playerRepository.findAll()).thenReturn(players);

        // When
        List<Player> sortedPlayers = playerService.getPlayersSortedByRank();

        // Then
        assertThat(sortedPlayers).hasSize(3);
        assertThat(sortedPlayers.get(0).getData().getRank()).isEqualTo(1);
        assertThat(sortedPlayers.get(1).getData().getRank()).isEqualTo(2);
        assertThat(sortedPlayers.get(2).getData().getRank()).isEqualTo(3);
    }

    @Test
    void shouldCalculateWinRateCorrectly() {
        // When
        double winRate1 = playerFRA1.getData().getWinRate();
        double winRate2 = playerFRA2.getData().getWinRate();
        double winRateUSA = playerUSA.getData().getWinRate();

        // Then
        assertThat(winRate1).isEqualTo(80.0);
        assertThat(winRate2).isEqualTo(60.0);
        assertThat(winRateUSA).isEqualTo(40.0);
    }

    @Test
    void shouldReturnCountryWithBestWinRatio() {
        // Given
        when(playerRepository.findAll()).thenReturn(Arrays.asList(playerFRA1, playerFRA2, playerUSA));

        // When
        Statistic stats = playerService.getCountryWithBestWinRatio();

        // Then
        assertThat(stats).isNotNull();
        assertThat(stats.getCountryCode()).isEqualTo("FRA");
        assertThat(stats.getWinRatio()).isEqualTo(70.0); // (80 + 60) / 2
    }

    @Test
    void shouldCalculateAverageBMIForCountry() {
        // Given
        when(playerRepository.findAll()).thenReturn(Arrays.asList(playerFRA1, playerFRA2, playerUSA));

        // When
        Statistic stats = playerService.getCountryWithBestWinRatio();

        // Then
        // FRA: 75/(1.8 x 1.8) = 23.15, 80/(1.85 x 1.85) = 23.37 -> averege = 23.26
        assertThat(stats.getAverageBMI()).isBetween(23.0, 24.0);
    }

    @Test
    void shouldCalculateMedianHeightForCountry() {
        // Given
        when(playerRepository.findAll()).thenReturn(Arrays.asList(playerFRA1, playerFRA2, playerUSA));

        // When
        Statistic stats = playerService.getCountryWithBestWinRatio();

        // Then
        // FRA: 180 et 185 -> median = 182.5
        assertThat(stats.getMedianHeight()).isEqualTo(182.5);
    }

    @Test
    void shouldCalculateBMI() {
        // When
        String bmi1 = playerFRA1.getData().getBMI();
        String bmi2 = playerFRA2.getData().getBMI();

        // Then
        assertThat(bmi1).matches("\\d+\\.\\d{2}");
        assertThat(Double.parseDouble(bmi1)).isBetween(23.0, 24.0);
        assertThat(Double.parseDouble(bmi2)).isBetween(23.0, 24.0);
    }

    @Test
    void shouldReturnNAForBMIWhenDataMissing() {
        // Given
        Player playerWithoutData = Player.builder()
                .data(PlayerData.builder().build())
                .build();

        // When
        String bmi = playerWithoutData.getData().getBMI();

        // Then
        assertThat(bmi).isEqualTo("N/A");
    }

    @Test
    void shouldCreateNewPlayer() {
        // Given
        Player newPlayer = Player.builder()
                .firstname("Jean-Jacques")
                .lastname("Golman")
                .country(Country.builder().code("FRA").build())
                .data(PlayerData.builder()
                        .rank(10)
                        .points(3000)
                        .weight(75000)
                        .height(180)
                        .age(25)
                        .last(Arrays.asList(1, 0, 1))
                        .build())
                .build();

        // When
        Player createdPlayer = playerService.createPlayer(newPlayer);

        // Then
        assertThat(createdPlayer).isNotNull();
        assertThat(createdPlayer.getFirstname()).isEqualTo("Jean-Jacques");
        assertThat(createdPlayer.getLastname()).isEqualTo("Golman");

        verify(playerRepository).save(newPlayer);
    }

    @Test
    void shouldThrowExceptionWhenFirstnameIsNull() {
        // Given
        Player invalidPlayer = Player.builder()
                .lastname("Last name")
                .build();

        // When & Then
        assertThatThrownBy(() -> playerService.createPlayer(invalidPlayer))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("First name and last name are required");
    }

    @Test
    void shouldThrowExceptionWhenLastnameIsNull() {
        // Given
        Player invalidPlayer = Player.builder()
                .firstname("Fisr name")
                .build();

        // When & Then
        assertThatThrownBy(() -> playerService.createPlayer(invalidPlayer))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("First name and last name are required");
    }

    @Test
    void shouldUpdateExistingPlayer() {
        // Given
        when(playerRepository.findById(1L)).thenReturn(Optional.of(playerFRA1));

        Player updatedData = Player.builder()
                .firstname("Celine Updated")
                .lastname("Dion Updated")
                .country(playerFRA1.getCountry())
                .data(PlayerData.builder()
                        .rank(2)
                        .points(4500)
                        .weight(76)
                        .height(181)
                        .age(29)
                        .last(Arrays.asList(1, 1, 1, 1, 0))
                        .build())
                .build();

        // When
        Player result = playerService.updatePlayer(1L, updatedData);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getFirstname()).isEqualTo("Celine Updated");
    }

    @Test
    void shouldThrowExceptionWhenUpdatingNonExistentPlayer() {
        // Given
        when(playerRepository.findById(999L)).thenReturn(Optional.empty());

        Player updatedData = Player.builder().firstname("Test").build();

        // When & Then
        assertThat(playerService.updatePlayer(999L, updatedData))
                .isNull();
    }

    @Test
    void shouldDeletePlayer() {
        // Given
        Long playerId = 1L;

        // When
        playerService.deletePlayer(playerId);

        // Then
        verify(playerRepository).deleteById(playerId);
    }

    @Test
    void shouldDeletePlayerEvenIfNotExists() {
        // Given
        Long nonExistentId = 999L;

        // When
        playerService.deletePlayer(nonExistentId);

        // Then
        verify(playerRepository).deleteById(nonExistentId);
    }
}