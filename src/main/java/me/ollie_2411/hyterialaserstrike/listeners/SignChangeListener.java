package me.ollie_2411.hyterialaserstrike.listeners;

import me.ollie_2411.hyterialaserstrike.HyteriaLaserStrike;
import me.ollie_2411.hyterialaserstrike.arena.Arena;
import me.ollie_2411.hyterialaserstrike.arena.ArenaManager;
import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.configuration.Configuration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;

/**
 * Created by Ollie on 27/01/2017.
 */
public class SignChangeListener implements Listener {

    public SignChangeListener(HyteriaLaserStrike instance) {
        instance.getServer().getPluginManager().registerEvents(this, instance);
    }

    @EventHandler
    public void onSignChange(SignChangeEvent e) {
        Player player = e.getPlayer();
        Configuration config = HyteriaLaserStrike.getPluginConfig();

        if (e.getLine(0).equalsIgnoreCase("[LaserStrike]")) {
            String arenaName = e.getLine(1);
            if (ArenaManager.getManager().getArena(arenaName) != null) {
                Arena arena = ArenaManager.getManager().getArena(arenaName);

                for (int i = 1; i < 5; i++) {
                    e.setLine(i - 1, ChatColor.translateAlternateColorCodes('&', config.getString("sign." + i).replaceAll("<arena>", arena.getName()).replaceAll("<state>", StringUtils.capitalize(arena.getState().name().toLowerCase()))
                            .replaceAll("<players>", String.valueOf(arena.getPlayers().size())).replaceAll("<max>", String.valueOf(arena.getMax()))));
                }
            } else {
                e.setCancelled(true);
                player.sendMessage(ChatColor.RED + "Invalid arena name");
            }
        }
    }
}
