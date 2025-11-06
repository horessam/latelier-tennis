package com.tennis.domain.port.in;

import com.tennis.domain.model.Player;

import java.util.List;

public interface GetPlayerRankingUseCase {
    List<Player> getPlayersSortedByRank();
}
