package com.game.controller;

import com.game.entity.Player;
import com.game.service.PlayerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/rest/players")
public class PlayersController {

    private final PlayerService playerService;

    @Autowired
    public PlayersController(PlayerService playerService) {
        this.playerService = playerService;
    }

    @GetMapping("/count")
    public long getCount(@RequestParam Map<String, String> parameters) {

        return playerService.size(parameters);
    }

    @GetMapping()
    public ResponseEntity<List<Player>> getAllUsers(@RequestParam Map<String, String> parameters) {
        List<Player> out = playerService.readAll(parameters);
        return new ResponseEntity<>(out, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Player> getPlayer(@PathVariable("id") String id) {
        if (isNotValidId(id)) return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        final Player player = playerService.read(Long.parseLong(id));

        return player != null
                ? new ResponseEntity<>(player, HttpStatus.OK)
                : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @RequestMapping()
    public ResponseEntity<?> createPlayer(@RequestBody Player player) {
        if (player == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        if (!isValidForCreate(player)) return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        final Player created = playerService.create(player);

        return created != null
                ? new ResponseEntity<>(created, HttpStatus.OK)
                : new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    @RequestMapping("/{id}")
    public ResponseEntity<?> updatePlayer(@PathVariable(name = "id") String id, @RequestBody Player player) {
        if (player == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        long long_id =Long.parseLong(id);
        if (isNotValidId(id)) return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        Player currentPlayer = playerService.read(Long.parseLong(id));
        if (currentPlayer == null) return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        if (isEmptyData(player)) return new ResponseEntity<>(currentPlayer, HttpStatus.OK);
        if (!updateDataIsValid(player)) return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        player.setId(long_id);
        player.updateFromRequest(currentPlayer);
        final Player updated = playerService.update(player, long_id);

        return updated != null
                ? new ResponseEntity<>(updated, HttpStatus.OK)
                : new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable(name = "id") String id) {

        if (isNotValidId(id)) return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        if (playerService.read(Long.parseLong(id)) == null) return new ResponseEntity<>(HttpStatus.NOT_FOUND);

        final boolean deleted = playerService.delete(Long.parseLong(id));

        return deleted
                ? new ResponseEntity<>(HttpStatus.OK)
                : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

/////////////////////////////////////////////////////////////////////////////////////////////

    private boolean isEmptyData(Player player) {
        boolean result = player.getName() == null;
        if ((player.getTitle() != null)) result = false;
        if ((player.getRace() != null)) result = false;
        if ((player.getProfession() != null)) result = false;
        if ((player.getExperience() != null)) result = false;
        if ((player.getBirthday() != null)) result = false;
        if ((player.getBanned() != null)) result = false;

        return result;
    }

    private boolean isValidForCreate(Player player) {
        boolean result = player.getName() != null;

        if ((player.getTitle() == null)) result = false;
        if ((player.getExperience() == null)) result = false;
        if ((player.getBirthday() == null)) result = false;

        return result && updateDataIsValid(player);
    }

    private boolean updateDataIsValid(Player player) {
        boolean result = (player.getName() == null) || (player.getName().length() <= 12);
        if ((player.getTitle() != null) && (player.getTitle().length() > 30)) result = false;
        if ((player.getExperience() != null) && ((player.getExperience() < 0) || player.getExperience() > 10_000_000))
            result = false;
        if ((player.getBirthday() != null) && (!validDate(player.getBirthday()))) result = false;
        return result;
    }

    private boolean isNotValidId(String id) {
        if (id == null) return true;

        try {
            //if (!(id.equals(Long.valueOf(id)))) return false;
            if (Long.parseLong(id) <= 0) return true;
        } catch (Exception e) {
            return true;
        }

        return false;
    }

    public boolean validDate(Date date) {
        Calendar cal = Calendar.getInstance();

        //cal.setTime(date);
        cal.set(Calendar.YEAR, 2000);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        long minDate = cal.getTimeInMillis();

        cal.set(Calendar.YEAR, 3000);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        long maxDate = cal.getTimeInMillis();

        return ((date.getTime() >= minDate) && (date.getTime() <= maxDate));
    }
}
