package com.tennis.domain.port.in;

import com.tennis.domain.model.Player;

public interface CreatePlayerUseCase {
    Player createPlayer(Player player);
}
