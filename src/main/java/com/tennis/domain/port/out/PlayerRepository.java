package com.tennis.domain.port.out;

import com.tennis.domain.model.Player;

import java.util.List;
import java.util.Optional;

public interface PlayerRepository {
    List<Player> findAll();
    Optional<Player> findById(Long id);
    void save(Player player);
   void deleteById(Long id);
}
