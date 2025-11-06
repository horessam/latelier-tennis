package com.tennis.infrastructure.adapter.out.persistence;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tennis.domain.model.Country;
import com.tennis.domain.model.Player;
import com.tennis.domain.model.PlayerData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class JsonPlayerRepositoryTest {

    private JsonPlayerRepository repository;
    private Player testPlayer1;
    private Player testPlayer2;

    @BeforeEach
    void setUp() {
        repository = new JsonPlayerRepository(new ObjectMapper());

        Country srb = Country.builder().code("SRB").picture("serbie.png").build();
        Country spain = Country.builder().code("ESP").picture("spain.png").build();

        testPlayer1 = Player.builder()
                .firstname("Rafael")
                .lastname("Nadal")
                .shortname("R.NAD")
                .sex("M")
                .country(spain)
                .data(PlayerData.builder()
                        .rank(1)
                        .points(10000)
                        .weight(85000)
                        .height(185)
                        .age(37)
                        .last(Arrays.asList(1, 1, 1, 0, 1))
                        .build())
                .build();

        testPlayer2 = Player.builder()
                .firstname("Novak")
                .lastname("Djokovic")
                .shortname("N.DJO")
                .sex("M")
                .country(srb)
                .data(PlayerData.builder()
                        .rank(2)
                        .points(9500)
                        .weight(80000)
                        .height(188)
                        .age(36)
                        .last(Arrays.asList(1, 1, 1, 1, 0))
                        .build())
                .build();

        repository.save(testPlayer1);
        repository.save(testPlayer2);
    }

    @Test
    void shouldLoadPlayersFromRepository() {
        // When
        List<Player> players = repository.findAll();

        // Then
        assertThat(players).isNotEmpty();
        assertThat(players).hasSize(2);
        assertThat(players).extracting(Player::getFirstname)
                .containsExactlyInAnyOrder("Rafael", "Novak");
    }

    @Test
    void shouldFindPlayerById() {
        // Given
        Long playerId = testPlayer1.getId();

        // When
        Optional<Player> player = repository.findById(playerId);

        // Then
        assertThat(player).isPresent();
        assertThat(player.get().getFirstname()).isEqualTo("Rafael");
        assertThat(player.get().getLastname()).isEqualTo("Nadal");
    }

    @Test
    void shouldReturnEmptyWhenPlayerNotFound() {
        // When
        Optional<Player> player = repository.findById(99999L);

        // Then
        assertThat(player).isEmpty();
    }

    @Test
    void shouldSaveNewPlayer() {
        // Given
        Player newPlayer = Player.builder()
                .firstname("Test")
                .lastname("Player")
                .country(Country.builder().code("FRA").build())
                .data(PlayerData.builder().rank(100).build())
                .build();

        int initialSize = repository.findAll().size();

        // When
        repository.save(newPlayer);

        // Then
        assertThat(repository.findAll()).hasSize(initialSize + 1);
        assertThat(newPlayer.getId()).isNotNull();
    }

    @Test
    void shouldUpdateExistingPlayer() {
        // Given
        Long playerId = testPlayer1.getId();
        testPlayer1.setFirstname("Rafael Updated");

        // When
        repository.save(testPlayer1);

        // Then
        Optional<Player> updated = repository.findById(playerId);
        assertThat(updated).isPresent();
        assertThat(updated.get().getFirstname()).isEqualTo("Rafael Updated");
    }

    @Test
    void shouldDeletePlayer() {
        // Given
        Player player = Player.builder()
                .firstname("To Delete")
                .lastname("Player")
                .country(Country.builder().code("FRA").build())
                .data(PlayerData.builder().rank(200).build())
                .build();

        repository.save(player);
        Long playerId = player.getId();
        int sizeAfterSave = repository.findAll().size();

        // When
        repository.deleteById(playerId);

        // Then
        assertThat(repository.findAll()).hasSize(sizeAfterSave - 1);
        assertThat(repository.findById(playerId)).isEmpty();
    }

    @Test
    void shouldGenerateUniqueIds() {
        // Given
        Player player1 = Player.builder()
                .firstname("Player1")
                .lastname("Test")
                .country(Country.builder().code("FRA").build())
                .data(PlayerData.builder().rank(10).build())
                .build();

        Player player2 = Player.builder()
                .firstname("Player2")
                .lastname("Test")
                .country(Country.builder().code("FRA").build())
                .data(PlayerData.builder().rank(11).build())
                .build();

        // When
        repository.save(player1);
        repository.save(player2);

        // Then
        assertThat(player1.getId()).isNotNull();
        assertThat(player2.getId()).isNotNull();
        assertThat(player1.getId()).isNotEqualTo(player2.getId());
    }
}