package com.tennis.infrastructure.adapter.in.rest;

import com.tennis.domain.model.Player;
import com.tennis.domain.model.Statistic;
import com.tennis.domain.port.in.*;
import com.tennis.infrastructure.adapter.in.rest.dto.CountryStatsResponse;
import com.tennis.infrastructure.adapter.in.rest.dto.PlayerCreateRequest;
import com.tennis.infrastructure.adapter.in.rest.dto.PlayerStatsResponse;
import com.tennis.infrastructure.adapter.in.rest.dto.PlayerUpdateRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/players")
@RequiredArgsConstructor
@Slf4j
public class PlayerStatsController {

    private final GetPlayerStatsUseCase getPlayerStatsUseCase;
    private final GetPlayerRankingUseCase getPlayerRankingUseCase;
    private final GetCountryWithBestRatioUseCase getCountryWithBestRatioUseCase;
    private final CreatePlayerUseCase createPlayerUseCase;
    private final UpdatePlayerUseCase updatePlayerUseCase;
    private final DeletePlayerUseCase deletePlayerUseCase;
    @GetMapping
    public ResponseEntity<List<PlayerStatsResponse>> getAllPlayers() {
        log.info("GET /api/players - Get all players");

        List<PlayerStatsResponse> response = getPlayerStatsUseCase.getAllPlayers()
                .stream()
                .map(PlayerStatsResponse::fromDomain)
                .collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PlayerStatsResponse> getPlayerById(@PathVariable Long id) {
        log.info("GET /api/players/{} - Get player by id", id);

        return getPlayerStatsUseCase.getPlayerById(id)
                .map(PlayerStatsResponse::fromDomain)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/ranking")
    public ResponseEntity<List<PlayerStatsResponse>> getPlayerRanking() {
        log.info("GET /api/players/ranking - Get player by ranking");

        List<PlayerStatsResponse> response = getPlayerRankingUseCase.getPlayersSortedByRank()
                .stream()
                .map(PlayerStatsResponse::fromDomain)
                .collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/country/best-ratio")
    public ResponseEntity<CountryStatsResponse> getCountryWithBestRatio() {
        log.info("GET /api/players/country/best-ratio - Get country with best ratio");

        Statistic countryStats = getCountryWithBestRatioUseCase.getCountryWithBestWinRatio();
        CountryStatsResponse response = CountryStatsResponse.fromDomain(countryStats);

        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<PlayerStatsResponse> createPlayer(@Valid @RequestBody PlayerCreateRequest request) {
        log.info("POST /api/players - Create a new player: {} {}",
                request.getFirstname(), request.getLastname());

        try {
            Player player = request.toDomain();
            Player createdPlayer = createPlayerUseCase.createPlayer(player);
            PlayerStatsResponse response = PlayerStatsResponse.fromDomain(createdPlayer);

            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IllegalArgumentException e) {
            log.error("Error when create player: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<PlayerStatsResponse> updatePlayer(
            @PathVariable Long id,
            @Valid @RequestBody PlayerUpdateRequest request) {
        log.info("PUT /api/players/{} - Update player", id);

        Player updatedPlayer = request.toDomain();
        Player result = updatePlayerUseCase.updatePlayer(id, updatedPlayer);

        if (result == null) {
            return ResponseEntity.notFound().build();
        }

        PlayerStatsResponse response = PlayerStatsResponse.fromDomain(result);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePlayer(@PathVariable Long id) {
        log.info("DELETE /api/players/{} - Delete player", id);

        deletePlayerUseCase.deletePlayer(id);
        return ResponseEntity.noContent().build();
    }
}
