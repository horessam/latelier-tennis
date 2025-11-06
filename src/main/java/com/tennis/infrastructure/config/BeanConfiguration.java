package com.tennis.infrastructure.config;

import com.tennis.domain.port.out.PlayerRepository;
import com.tennis.domain.service.PlayerService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BeanConfiguration {
    @Bean
    public PlayerService playerService(PlayerRepository playerRepository) {
        return new PlayerService(playerRepository);
    }
}