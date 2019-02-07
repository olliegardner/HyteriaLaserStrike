package me.ollie_2411.hyterialaserstrike.listeners;

import me.ollie_2411.hyterialaserstrike.HyteriaLaserStrike;
import me.ollie_2411.hyterialaserstrike.arena.Arena;
import me.ollie_2411.hyterialaserstrike.arena.ArenaManager;
import me.ollie_2411.hyterialaserstrike.game.TeamManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRespawnEvent;

/**
 * Created by Ollie on 28/02/2016.
 */
public class RespawnListener implements Listener {

    public RespawnListener(HyteriaLaserStrike instance) {
        instance.getServer().getPluginManager().registerEvents(this, instance);
    }

    @EventHandler
    public void onRespawn(PlayerRespawnEvent e) {
        Player player = e.getPlayer();
        if (ArenaManager.getManager().inGame(player)) {
            Arena arena = ArenaManager.getManager().getArena(player);
            TeamManager tm = TeamManager.getManager();

            if (tm.getTeam(player) == tm.getBlue()) {
                e.setRespawnLocation(arena.randomSpawn("blueSpawn"));
            } else if (tm.getTeam(player) == tm.getGreen()) {
                e.setRespawnLocation(arena.randomSpawn("greenSpawn"));
            }
        }
    }
}
