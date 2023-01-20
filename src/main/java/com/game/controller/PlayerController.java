package com.game.controller;

import com.game.entity.Player;
import com.game.entity.Profession;
import com.game.entity.Race;
import com.game.service.PlayerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

@RestController
@RequestMapping("/rest")
public class PlayerController {
    private PlayerService playerService;

    @Autowired
    public void PlayerService(PlayerService playerService) {
        this.playerService = playerService;
    }

    @GetMapping("/players")
    @ResponseStatus(HttpStatus.OK)
    public Player[] getPlayersList() {

//        return playerService.getPlayersList();

        Player player = new Player();
        player.setId(10L);
        player.setName("Petya");
        player.setRace(Race.ELF);
        player.setProfession(Profession.CLERIC);
        player.setExperience(500);
        player.setTitle("Полупокер");
        player.setLevel(1);
        player.setBanned(false);
        player.setBirthday(new Date());
        player.setUntilNextLevel(500);

        return new Player[]{
                player,
                player,
                player,
        };
    }

    @GetMapping("/players/count")
    @ResponseStatus(HttpStatus.OK)
    public int getPlayersCount() {
        return 3;
    }
}
