package com.tennis.domain.port.in;

import com.tennis.domain.model.Player;

public interface UpdatePlayerUseCase {
    Player updatePlayer(Long id, Player player);
}
