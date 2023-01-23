package com.game.service;

import com.game.entity.Player;
import com.game.entity.Profession;
import com.game.entity.Race;
import com.game.repository.IPlayerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Service
public class PlayerService implements IPlayerService {

    private IPlayerRepository playerRepository;

    @Autowired
    public void setPlayerRepository(IPlayerRepository playerRepository) {
        this.playerRepository = playerRepository;
    }

    private boolean invalidParameters(Player player) {
        if (player.getName().length() < 1 || player.getName().length() > 12) return true;

        if (player.getTitle().length() > 30) return true;

        if (player.getExperience() < 0 || player.getExperience() > 10_000_000) return true;

        if (player.getBirthday().getTime() < 0) return true;

        Calendar date = Calendar.getInstance();
        date.setTime(player.getBirthday());

        if (date.get(Calendar.YEAR) < 2_000 || date.get(Calendar.YEAR) > 3_000) return true;

        return false;
    }

    private boolean nullableParameters(Player player) {
        if (player.getName() == null
                || player.getTitle() == null
                || player.getRace() == null
                || player.getProfession() == null
                || player.getBirthday() == null
                || player.getExperience() == null) return true;

        return false;
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
    public List<Player> getPlayersList(Specification<Player> specification) {
        return playerRepository.findAll(specification);
    }

    @Override
    public Page<Player> getPlayersList(Specification<Player> specification, Pageable pageable) {
        return playerRepository.findAll(specification, pageable);
    }

    @Override
    public Player createPlayer(Player playerNew) {
        if (nullableParameters(playerNew)) return null;

        if (invalidParameters(playerNew)) return null;

        if (playerNew.getBanned() == null) playerNew.setBanned(false);

        playerNew.setLevel(calcLevel(playerNew));
        playerNew.setUntilNextLevel(calcUntilNextLevel(playerNew));

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

        updatingPlayer.setLevel(calcLevel(updatingPlayer));
        updatingPlayer.setUntilNextLevel(calcUntilNextLevel(updatingPlayer));

        return playerRepository.save(updatingPlayer);
    }

    @Override
    public boolean deletePlayer(Long id) {
        if (!playerRepository.findById(id).isPresent()) return false;
        playerRepository.deleteById(id);
        return true;
    }

    @Override
    public Specification<Player> nameFilter(String name) {
        return (root, query, criteriaBuilder) -> name == null ? null : criteriaBuilder.like(root.get("name"), "%" + name + "%");
    }

    @Override
    public Specification<Player> titleFilter(String title) {
        return (root, query, criteriaBuilder) -> title == null ? null : criteriaBuilder.like(root.get("title"), "%" + title + "%");
    }

    @Override
    public Specification<Player> raceFilter(Race race) {
        return (root, query, criteriaBuilder) -> race == null ? null : criteriaBuilder.equal(root.get("race"), race);
    }

    @Override
    public Specification<Player> professionFilter(Profession profession) {
        return (root, query, criteriaBuilder) -> profession == null ? null : criteriaBuilder.equal(root.get("profession"), profession);
    }

    @Override
    public Specification<Player> experienceFilter(Integer min, Integer max) {
        return (root, query, criteriaBuilder) -> {
            if (min == null && max == null) return null;
            if (min == null) return criteriaBuilder.lessThanOrEqualTo(root.get("experience"), max);
            if (max == null) return criteriaBuilder.greaterThanOrEqualTo(root.get("experience"), min);

            return criteriaBuilder.between(root.get("experience"), min, max);
        };
    }

    @Override
    public Specification<Player> levelFilter(Integer min, Integer max) {
        return (root, query, criteriaBuilder) -> {
            if (min == null && max == null) return null;
            if (min == null) return criteriaBuilder.lessThanOrEqualTo(root.get("level"), max);
            if (max == null) return criteriaBuilder.greaterThanOrEqualTo(root.get("level"), min);

            return criteriaBuilder.between(root.get("level"), min, max);
        };
    }

    @Override
    public Specification<Player> birthdayFilter(Long after, Long before) {
        return (root, query, criteriaBuilder) -> {
            if (after == null && before == null) return null;

            if (after == null) {
                Date beforeNow = new Date(before);
                return criteriaBuilder.lessThanOrEqualTo(root.get("birthday"), beforeNow);
            }
            if (before == null) {
                Date afterNow = new Date(after);
                return criteriaBuilder.greaterThanOrEqualTo(root.get("birthday"), afterNow);
            }

            Date beforeNow = new Date(before - 3_600_001);
            Date afterNow = new Date(after);

            return criteriaBuilder.between(root.get("birthday"), afterNow, beforeNow);
        };
    }

    @Override
    public Specification<Player> bannedFilter(Boolean isBanned) {
        return (root, query, criteriaBuilder) -> {
            if (isBanned == null) return null;
            if (isBanned) return criteriaBuilder.isTrue(root.get("banned"));
            else return criteriaBuilder.isFalse(root.get("banned"));
        };
    }
}