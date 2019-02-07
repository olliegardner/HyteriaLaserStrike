package me.ollie_2411.hyterialaserstrike.listeners;

import me.ollie_2411.hyterialaserstrike.HyteriaLaserStrike;
import me.ollie_2411.hyterialaserstrike.arena.Arena;
import me.ollie_2411.hyterialaserstrike.arena.ArenaManager;
import me.ollie_2411.hyterialaserstrike.game.GameState;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

/**
 * Created by Ollie on 22/02/2016.
 */
public class CommandListener implements Listener {

    public CommandListener(HyteriaLaserStrike instance) {
        instance.getServer().getPluginManager().registerEvents(this, instance);
    }

    @EventHandler
    public void onPreProcess(PlayerCommandPreprocessEvent e) {
        Player player = e.getPlayer();
        if (ArenaManager.getManager().inGame(player)) {
            Arena arena = ArenaManager.getManager().getArena(player);
            if (arena.getState() == GameState.LOBBY || arena.getState() == GameState.GAME) {
                if (!e.getMessage().startsWith("/leave")) {
                    e.setCancelled(true);
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', HyteriaLaserStrike.getPluginConfig().getString("messages.command")));
                }
            }
        }
    }
}
