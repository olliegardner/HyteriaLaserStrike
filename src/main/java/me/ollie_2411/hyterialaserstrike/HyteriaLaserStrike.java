package me.ollie_2411.hyterialaserstrike;

import me.ollie_2411.hyterialaserstrike.arena.Arena;
import me.ollie_2411.hyterialaserstrike.arena.ArenaManager;
import me.ollie_2411.hyterialaserstrike.commands.LaserCommand;
import me.ollie_2411.hyterialaserstrike.commands.LeaveCommand;
import me.ollie_2411.hyterialaserstrike.game.Board;
import me.ollie_2411.hyterialaserstrike.game.Game;
import me.ollie_2411.hyterialaserstrike.game.GameState;
import me.ollie_2411.hyterialaserstrike.listeners.*;
import me.ollie_2411.hyterialaserstrike.utils.ArenaUtils;
import org.bukkit.Bukkit;
import org.bukkit.configuration.Configuration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.Team;

import java.io.File;

/**
 * Created by Ollie on 22/02/2016.
 */
public class HyteriaLaserStrike extends JavaPlugin {

    public void onEnable() {
        new ClickListener(this);
        new QuitListener(this);
        new CommandListener(this);
        new InteractListener(this);
        new EntityDamageEntityListener(this);
        new PlaceListener(this);
        new RespawnListener(this);
        new JoinListener(this);
        new FoodChangeListener(this);
        new MoveListener(this);
        new CreatureSpawnListener(this);
        new SignChangeListener(this);
        getCommand("laser").setExecutor(new LaserCommand(this));
        getCommand("leave").setExecutor(new LeaveCommand(this));

        getConfig().options().copyDefaults(true);
        saveDefaultConfig();

        File file = new File(getDataFolder().getPath() + "/arenas");
        if (!file.exists()) {
            file.mkdir();
        }
        ArenaManager.getManager().loadArenas();

        Bukkit.getScheduler().scheduleSyncDelayedTask(this, new Runnable() {
            @Override
            public void run() {
                for (Player online : Bukkit.getOnlinePlayers()) {
                    if (!ArenaManager.getManager().inGame(online)) {
                        Board board = new Board();
                        board.setLobbyBoard(online);
                    } else {
                        online.teleport(online.getWorld().getSpawnLocation());
                    }
                    online.getInventory().clear();
                    online.getInventory().setArmorContents(null);
                }

                for (Arena arena : ArenaManager.getManager().getArenas()) {
                    arena.setState(GameState.LOBBY);
                    ArenaUtils.updateSign(arena);
                    for (Player online : Bukkit.getOnlinePlayers()) {
                        new Game().restartGame(arena, online);
                    }
                }
            }
        }, 1L);
    }

    public static Plugin getInstance() {
        return Bukkit.getPluginManager().getPlugin("HyteriaLaserStrike");
    }

    public static Configuration getPluginConfig() {
        return Bukkit.getPluginManager().getPlugin("HyteriaLaserStrike").getConfig();
    }
}
