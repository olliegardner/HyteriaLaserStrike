package me.ollie_2411.hyterialaserstrike.listeners;

import me.ollie_2411.hyterialaserstrike.HyteriaLaserStrike;
import me.ollie_2411.hyterialaserstrike.arena.Arena;
import me.ollie_2411.hyterialaserstrike.arena.ArenaManager;
import me.ollie_2411.hyterialaserstrike.game.Board;
import me.ollie_2411.hyterialaserstrike.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.configuration.Configuration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 * Created by Ollie on 22/02/2016.
 */
public class QuitListener implements Listener {

    private Board board = new Board();

    public QuitListener(HyteriaLaserStrike instance) {
        instance.getServer().getPluginManager().registerEvents(this, instance);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        Player player = e.getPlayer();
        Configuration config = HyteriaLaserStrike.getPluginConfig();

        if (ArenaManager.getManager().inGame(player)) {
            player.getInventory().clear();
            player.getInventory().setArmorContents(null);

            Arena arena = ArenaManager.getManager().getArena(player);
            ArenaManager.getManager().removePlayer(player);
            Utils.sendArenaMessage(arena, config.getString("messages.leftArena").replaceAll("<player>", player.getName()).replaceAll("<players>", String.valueOf(arena.getPlayers().size())).replaceAll("<max>", String.valueOf(arena.getMax())));
        } else {
            Bukkit.getScheduler().scheduleSyncDelayedTask(HyteriaLaserStrike.getInstance(), new Runnable() {
                @Override
                public void run() {
                    for (Player online : Bukkit.getOnlinePlayers()) {
                        board.setLobbyBoard(online);
                    }
                }
            }, 1L);
        }
    }
}
