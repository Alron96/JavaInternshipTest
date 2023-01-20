package com.game.service;

import com.game.entity.Player;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

public interface PlayerServiseInterface {

    int calcLevel(Player currentPlayer);

    Integer calcUntilNextLevel(Player currentPlayer);

    List<Player> getPlayersList(List<Player> players);

    Integer getPlayersCount(List<Player> players);

    Player createPlayer(Player playerNew);

    Player getPlayer(Long id);

    Player updatePlayer(Long id, Player updatePlayer);

    void deletePlayer(Long id);
}
