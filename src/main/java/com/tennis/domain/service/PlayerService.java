package com.tennis.domain.service;

import com.tennis.domain.model.Player;
import com.tennis.domain.model.Statistic;
import com.tennis.domain.port.in.*;
import com.tennis.domain.port.out.PlayerRepository;
import com.tennis.domain.service.exception.NoStatisticAvailableException;
import lombok.RequiredArgsConstructor;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class PlayerService implements
        GetPlayerStatsUseCase,
        GetPlayerRankingUseCase,
        GetCountryWithBestRatioUseCase,
        CreatePlayerUseCase,
        UpdatePlayerUseCase,
        DeletePlayerUseCase {

    private final PlayerRepository playerRepository;

    @Override
    public List<Player> getAllPlayers() {
        return playerRepository.findAll();
    }

    @Override
    public Optional<Player> getPlayerById(Long id) {
        return playerRepository.findById(id);
    }

    @Override
    public List<Player> getPlayersSortedByRank() {
        return playerRepository.findAll().stream()
                .filter(player -> player.getData() != null)
                .sorted(Comparator.comparing(player -> player.getData().getRank()))
                .collect(Collectors.toList());
    }

    @Override
    public Player createPlayer(Player player) {
        if (player.getFirstname() == null || player.getLastname() == null) {
            throw new IllegalArgumentException("First name and last name are required");
        }

        playerRepository.save(player);
        return player;
    }

    @Override
    public Player updatePlayer(Long id, Player updatedPlayer) {
        return playerRepository.findById(id)
                .map(existing -> {
                    updateFieldIfNotNull(updatedPlayer.getFirstname(), existing::setFirstname);
                    updateFieldIfNotNull(updatedPlayer.getLastname(), existing::setLastname);
                    updateFieldIfNotNull(updatedPlayer.getShortname(), existing::setShortname);
                    updateFieldIfNotNull(updatedPlayer.getSex(), existing::setSex);
                    updateFieldIfNotNull(updatedPlayer.getCountry(), existing::setCountry);
                    updateFieldIfNotNull(updatedPlayer.getPicture(), existing::setPicture);
                    updateFieldIfNotNull(updatedPlayer.getData(), existing::setData);

                    playerRepository.save(existing);
                    return existing;
                })
                .orElse(null);
    }

    private <T> void updateFieldIfNotNull(T value, Consumer<T> setter) {
        if (value != null) {
            setter.accept(value);
        }
    }

    @Override
    public void deletePlayer(Long id) {
        playerRepository.deleteById(id);
    }

    @Override
    public Statistic getCountryWithBestWinRatio() {
        return playerRepository.findAll().stream()
                .filter(Player::hasValidCountry)
                .collect(Collectors.groupingBy(player -> player.getCountry().getCode()))
                .entrySet().stream()
                .map(entry -> calculateCountryStatistic(entry.getKey(), entry.getValue()))
                .max(Comparator.comparing(Statistic::getWinRatio))
                .orElseThrow(() -> new NoStatisticAvailableException("No statistic available"));
    }

    private Statistic calculateCountryStatistic(String countryCode, List<Player> players) {
        List<Player> playersWithData = filterPlayersWithData(players);

        return Statistic.builder()
                .countryCode(countryCode)
                .winRatio(calculateAverageWinRatio(playersWithData))
                .averageBMI(calculateAverageBMI(playersWithData))
                .medianHeight(calculateMedianHeight(playersWithData))
                .build();
    }

    private List<Player> filterPlayersWithData(List<Player> players) {
        return players.stream()
                .filter(Player::hasData)
                .collect(Collectors.toList());
    }

    private double calculateAverageWinRatio(List<Player> players) {
        return players.stream()
                .mapToDouble(player -> player.getData().getWinRate())
                .average()
                .orElse(0.0);
    }

    private double calculateAverageBMI(List<Player> players) {
        return players.stream()
                .filter(Player::hasValidBMIData)
                .mapToDouble(Player::calculateBMI)
                .average()
                .orElse(0.0);
    }

    private double calculateMedianHeight(List<Player> players) {
        List<Integer> heights = players.stream()
                .map(player -> player.getData().getHeight())
                .filter(Objects::nonNull)
                .sorted()
                .toList();

        if (heights.isEmpty()) {
            return 0.0;
        }

        int size = heights.size();
        int midIndex = size / 2;

        return size % 2 == 0
                ? (heights.get(midIndex - 1) + heights.get(midIndex)) / 2.0
                : heights.get(midIndex);
    }
}
