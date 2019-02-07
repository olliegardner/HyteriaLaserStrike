package me.ollie_2411.hyterialaserstrike.arena;

import me.ollie_2411.hyterialaserstrike.HyteriaLaserStrike;
import me.ollie_2411.hyterialaserstrike.game.GameState;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Ollie on 22/02/2016.
 */
public class Arena {

    private String name;
    private List<Location> blueSpawn;
    private List<Location> greenSpawn;
    private Location lobby;
    private int min;
    private int max;
    private int blueLives;
    private int greenLives;
    private GameState state;
    private File config;
    public FileConfiguration arenaConfig;
    private List<String> players = new ArrayList<String>();

    public Arena(String s) {
        this.name = s;
        File file = new File(HyteriaLaserStrike.getInstance().getDataFolder().getPath() + "/arenas", s + ".yml");
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException io) {
                io.printStackTrace();
                return;
            }
        }
        this.config = file;
        this.arenaConfig = YamlConfiguration.loadConfiguration(file);
        arenaConfig.set("name", name);
        this.blueSpawn = (List<Location>) arenaConfig.getList("blueSpawn");
        this.greenSpawn = (List<Location>) arenaConfig.getList("greenSpawn");
        this.lobby = (Location) arenaConfig.get("lobby");
        this.min = arenaConfig.getInt("min");
        this.max = arenaConfig.getInt("max");
        this.blueLives = arenaConfig.getInt("blueLives");
        this.greenLives = arenaConfig.getInt("greenLives");
        this.state = GameState.LOBBY;
        save();
    }

    public void save() {
        try {
            arenaConfig.save(config);
        } catch (IOException io) {
            io.printStackTrace();
        }
    }

    public String getName() {
        return name;
    }

    public List<Location> getBlueSpawn() {
        return blueSpawn;
    }

    public void setBlueSpawn(List<Location> blueSpawn) {
        this.blueSpawn = blueSpawn;
    }

    public List<Location> getGreenSpawn() {
        return greenSpawn;
    }

    public void setGreenSpawn(List<Location> greenSpawn) {
        this.greenSpawn = greenSpawn;
    }

    public Location getLobby() {
        return lobby;
    }

    public void setLobby(Location lobby) {
        this.lobby = lobby;
    }

    public int getMin() {
        return min;
    }

    public void setMin(int min) {
        this.min = min;
    }

    public int getMax() {
        return max;
    }

    public void setMax(int max) {
        this.max = max;
    }

    public int getBlueLives() {
        return blueLives;
    }

    public void setBlueLives(int blueLives) {
        this.blueLives = blueLives;
    }

    public int getGreenLives() {
        return greenLives;
    }

    public void setGreenLives(int greenLives) {
        this.greenLives = greenLives;
    }

    public GameState getState() {
        return state;
    }

    public void setState(GameState state) {
        this.state = state;
    }

    public List<String> getPlayers() {
        return players;
    }

    public Location randomSpawn(String team) {
        List<Location> spawns = (List<Location>) arenaConfig.getList(team);
        Collections.shuffle(spawns);
        return spawns.get(0);
    }
}
