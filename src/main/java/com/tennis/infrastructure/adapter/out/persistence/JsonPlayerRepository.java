package com.tennis.infrastructure.adapter.out.persistence;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tennis.domain.model.Player;
import com.tennis.domain.port.out.PlayerRepository;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Repository
@Slf4j
public class JsonPlayerRepository implements PlayerRepository {

    @Value("classpath:headtohead.json")
    private Resource playersResource;

    private final ObjectMapper objectMapper;
    private final Map<Long, Player> playerCache = new ConcurrentHashMap<>();

    public JsonPlayerRepository(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @PostConstruct
    public void init() {
        try {
            loadPlayers();
        } catch (Exception e) {
            log.warn("Unable to load the headtohead.json file, the repository will be empty at start: {}", e.getMessage());
        }
    }

    private void loadPlayers() {
        try {
            if (playersResource == null || !playersResource.exists()) {
                log.warn("File headtohead.json not find, the repository will be empty at start");
                return;
            }

            PlayerListWrapper wrapper = objectMapper.readValue(playersResource.getInputStream(), PlayerListWrapper.class);

            if (wrapper.getPlayers() != null) {
                wrapper.getPlayers().forEach(player -> playerCache.put(player.getId(), player));
                log.info("Loading {} players from the JSON file", playerCache.size());
            }
        } catch (IOException e) {
            log.error("Error loading players", e);
            throw new RuntimeException("Unable to load player data", e);
        }
    }

    @Override
    public List<Player> findAll() {
        return new ArrayList<>(playerCache.values());
    }

    @Override
    public Optional<Player> findById(Long id) {
        return Optional.ofNullable(playerCache.get(id));
    }

    @Override
    public void save(Player player) {
        if (player.getId() == null) {
            player.setId(generateNewId());
        }
        playerCache.put(player.getId(), player);
        log.info("Saved player: {}", player.getFullName());
    }

    @Override
    public void deleteById(Long id) {
        Player removed = playerCache.remove(id);
        if (removed != null) {
            log.info("Deleted player: {}", removed.getFullName());
        }
    }

    private Long generateNewId() {
        return playerCache.keySet().stream()
                .max(Long::compareTo)
                .map(id -> id + 1)
                .orElse(1L);
    }

    private static class PlayerListWrapper {
        private List<Player> players;

        public List<Player> getPlayers() {
            return players != null ? players : Collections.emptyList();
        }
    }
}

