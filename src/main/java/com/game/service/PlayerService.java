package com.game.service;

import com.game.controller.PlayerOrder;
import com.game.entity.Player;
import com.game.entity.Profession;
import com.game.entity.Race;
import com.game.repository.PlayerRepository;
import com.game.repository.PlayerSpecificationBuilder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class PlayerService {

    private final PlayerRepository playerRepository;

    public PlayerService(PlayerRepository playerRepository) {
        this.playerRepository = playerRepository;
    }

    public Player create(Player player) {
        player.setLevel(player.getCalculatedLevel());
        player.setUntilNextLevel(player.getCalculatedNextLevel());
        return playerRepository.saveAndFlush(player);
    }

    public List<Player> readAll(Map<String, String> parameters) {
        Specification<Player> playerSpecification = makeSearchCriteria(parameters);
        String order="id";
        int pageNumber =0;
        int pageSize = 3;
        if (parameters.containsKey("order")) order = PlayerOrder.valueOf(parameters.get("order")).getFieldName();
        if (parameters.containsKey("pageNumber")) pageNumber = Integer.parseInt(parameters.get("pageNumber"));
        if (parameters.containsKey("pageSize")) pageSize = Integer.parseInt(parameters.get("pageSize"));

        Page<Player> page = playerRepository.findAll(playerSpecification, PageRequest.of(pageNumber, pageSize, Sort.by(order)));

        ArrayList<Player> players = new ArrayList<>();
        for (Player player : page) {
            players.add(player);
        }
        return players;
    }

    public Player read(long id) {
        Optional<Player> player =playerRepository.findById(id);
        if (!player.isPresent()) return null;
        return playerRepository.findById(id).get();
    }

    public Player update(Player player, long id) {

        player.setLevel(player.getCalculatedLevel());
        player.setUntilNextLevel(player.getCalculatedNextLevel());
        return playerRepository.save(player);
    }

    public boolean delete(long id) {
        Player player = read(id);
        if (player==null )return false;
        playerRepository.delete(player);
        return true;
    }

    public long size(Map<String, String> parameters) {
        Specification<Player> playerSpecification = makeSearchCriteria(parameters);
        return playerRepository.count(playerSpecification);
    }

    private Specification<Player> makeSearchCriteria(Map<String, String> parameters) {
        PlayerSpecificationBuilder builder = new PlayerSpecificationBuilder();
        if (parameters.containsKey("name"))
            builder.with("name", ":", parameters.get("name"));
        if (parameters.containsKey("title"))
            builder.with("title", ":", parameters.get("title"));
        if (parameters.containsKey("race"))
            builder.with("race", "==", Race.valueOf(parameters.get("race")));
        if (parameters.containsKey("profession"))
            builder.with("profession", "==", Profession.valueOf(parameters.get("profession")));
        if (parameters.containsKey("after"))
            builder.with("birthday", "D>=", dateFormatForMySql(parameters.get("after")));
        if (parameters.containsKey("before"))
            builder.with("birthday", "D<=", dateFormatForMySql(parameters.get("before")));
        if (parameters.containsKey("banned"))
            builder.with("banned", "true", parameters.get("banned"));
        if (parameters.containsKey("minExperience"))
            builder.with("experience", ">=", Integer.parseInt(parameters.get("minExperience")));
        if (parameters.containsKey("maxExperience"))
            builder.with("experience", "<=", Integer.parseInt(parameters.get("maxExperience")));
        if (parameters.containsKey("minLevel"))
            builder.with("level", ">=", Integer.parseInt(parameters.get("minLevel")));
        if (parameters.containsKey("maxLevel"))
            builder.with("level", "<=", Integer.parseInt(parameters.get("maxLevel")));
        return builder.build();
    }

    private Date dateFormatForMySql(String birthday){
        String b = birthday.replace("L","");
        return new Date(Long.parseLong(b));
    }

}
