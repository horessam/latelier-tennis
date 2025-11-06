package com.tennis.domain.port.in;

import com.tennis.domain.model.Player;

import java.util.List;
import java.util.Optional;

public interface GetPlayerStatsUseCase {
    List<Player> getAllPlayers();
    Optional<Player> getPlayerById(Long id);
}
