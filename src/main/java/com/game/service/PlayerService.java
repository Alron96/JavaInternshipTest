package com.game.service;

import com.game.entity.Player;
import com.game.repository.PlayerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.Calendar;
import java.util.List;

@Service
public class PlayerService implements PlayerServiseInterface {

    private PlayerRepository playerRepository;

    @Autowired
    public PlayerService(PlayerRepository playerRepository) {
        this.playerRepository = playerRepository;
    }

    @Override
    public int calcLevel(Player currentPlayer) {
        Integer exp = currentPlayer.getExperience();
        return (int) ((Math.sqrt(2500 + 200 * exp) - 50) / 100);
    }

    @Override
    public Integer calcUntilNextLevel(Player currentPlayer) {
        Integer exp = currentPlayer.getExperience();
        int level = calcLevel(currentPlayer);
        return (50 * (level + 1) * (level + 2) - exp);
    }

    @Override
    public List<Player> getPlayersList(List<Player> players) {
        return playerRepository.findAll();
    }

    @Override
    public Integer getPlayersCount(List<Player> players) {
        throw new NotImplementedException();
    }

    @Override
    public Player createPlayer(Player playerNew) {
        Calendar date = Calendar.getInstance();
        date.setTime(playerNew.getBirthday());

        if (playerNew.getName() == null
                || playerNew.getName().length() > 12
                || playerNew.getName().length() < 1

                || playerNew.getTitle().length() > 30
                || playerNew.getTitle() == null

                || playerNew.getExperience() < 0
                || playerNew.getExperience() > 10_000_000
                || playerNew.getExperience() == null

                || playerNew.getBirthday().getTime() < 0
                || date.get(Calendar.YEAR) < 2000
                || date.get(Calendar.YEAR) > 3000
                || playerNew.getBirthday() == null) {
            return null;
        }

        if (playerNew.getBanned() == null) playerNew.setBanned(false);
        return playerRepository.saveAndFlush(playerNew);
    }

    @Override
    public Player getPlayer(Long id) {
        if (!playerRepository.findById(id).isPresent()) return null;
        return playerRepository.findById(id).get();
    }

    @Override
    public Player updatePlayer(Long id, Player updatePlayer) {
        if (!playerRepository.findById(id).isPresent()) {
            return null;
        }

        Player updatingPlayer = getPlayer(id);

        if (updatePlayer.getName() != null) updatingPlayer.setName(updatePlayer.getName());

        if (updatePlayer.getTitle() != null) updatingPlayer.setTitle(updatePlayer.getTitle());

        if (updatePlayer.getRace() != null) updatingPlayer.setRace(updatePlayer.getRace());

        if (updatePlayer.getProfession() != null) updatingPlayer.setProfession(updatePlayer.getProfession());

        if (updatePlayer.getBirthday() != null) updatingPlayer.setBirthday(updatePlayer.getBirthday());

        if (updatePlayer.getBanned() != null) updatingPlayer.setBanned(updatePlayer.getBanned());

        if (updatePlayer.getExperience() != null) updatingPlayer.setExperience(updatePlayer.getExperience());

        return playerRepository.saveAndFlush(updatingPlayer); //Если у него id не меняется, то по логике, я перезапишу персонажа?
    }

    @Override
    public void deletePlayer(Long id) {
        if (!playerRepository.findById(id).isPresent()) {
        } //как оформить фильтр в невозвращаемом методе? или нужен возвращаемый, тогда какой тип данных?
        playerRepository.deleteById(id);
    }
}
