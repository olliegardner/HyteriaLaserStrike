package me.ollie_2411.hyterialaserstrike.game;

import me.ollie_2411.hyterialaserstrike.HyteriaLaserStrike;
import me.ollie_2411.hyterialaserstrike.arena.Arena;
import me.ollie_2411.hyterialaserstrike.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.configuration.Configuration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * Created by Ollie on 22/02/2016.
 */
public class Countdown extends BukkitRunnable {

    private Plugin plugin;
    private int seconds;
    private Arena arena;
    public static boolean running;
    private Configuration config = HyteriaLaserStrike.getPluginConfig();

    public Countdown(Plugin plugin, int seconds, Arena arena) {
        this.plugin = plugin;
        this.seconds = seconds;
        this.arena = arena;
    }

    @Override
    public void run() {
        if (seconds > 0) {
            for (String s : arena.getPlayers()) {
                Player player = Bukkit.getPlayer(s);
                player.playSound(player.getLocation(), Sound.NOTE_PLING, 10F, 10F);
            }
            Utils.sendArenaActionBarMessage(arena, config.getString("messages.countdown").replaceAll("<seconds>", String.valueOf(seconds--)));
        } else {
            Game game = new Game();
            for (String s : arena.getPlayers()) {
                Player player = Bukkit.getPlayer(s);
                game.startGame(player);
                player.playSound(player.getLocation(), Sound.LEVEL_UP, 10F, 10F);
            }

            Utils.sendArenaActionBarMessage(arena, config.getString("messages.gameStarted"));
            running = false;
            this.cancel();
        }
    }
}
