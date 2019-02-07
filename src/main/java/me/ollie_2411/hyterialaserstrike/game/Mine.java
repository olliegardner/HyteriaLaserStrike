package me.ollie_2411.hyterialaserstrike.game;

import org.bukkit.Location;
import org.bukkit.entity.Player;

/**
 * Created by Ollie on 06/02/2017.
 */
public class Mine {

    private Player player;
    private Location location;

    public Mine(Player player, Location location) {
        this.player = player;
        this.location = location;
    }

    public Player getPlayer() {
        return player;
    }

    public Location getLocation() {
        return location;
    }
}
