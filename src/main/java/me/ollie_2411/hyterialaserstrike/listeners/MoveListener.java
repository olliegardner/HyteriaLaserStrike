package me.ollie_2411.hyterialaserstrike.listeners;

import me.ollie_2411.hyterialaserstrike.HyteriaLaserStrike;
import me.ollie_2411.hyterialaserstrike.arena.Arena;
import me.ollie_2411.hyterialaserstrike.arena.ArenaManager;
import me.ollie_2411.hyterialaserstrike.utils.ArenaUtils;
import org.bukkit.Effect;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;

/**
 * Created by Ollie on 06/03/2016.
 */
public class MoveListener implements Listener {

    private HashMap<String, BukkitRunnable> task = new HashMap<String, BukkitRunnable>();

    public MoveListener(HyteriaLaserStrike instance) {
        instance.getServer().getPluginManager().registerEvents(this, instance);
    }

    @EventHandler
    public void onMove(final PlayerMoveEvent e) {
        final Player player = e.getPlayer();
        if (ArenaManager.getManager().inGame(player)) {
            final Arena arena = ArenaManager.getManager().getArena(player);

            if (task.containsKey(player.getName())) {
                task.get(player.getName()).cancel();
                task.remove(player.getName());
            }

            task.put(player.getName(), new BukkitRunnable() {
                @Override
                public void run() {
                    if (ArenaUtils.playerInRechargeArea(e, arena) && player.getExp() < 1) {
                        player.setExp(player.getExp() + ((1F / (10 * 20)) * 2));
                        player.getWorld().spigot().playEffect(player.getLocation(), Effect.FLAME, 0, 0, (float) 1.0, (float) 1.0, (float) 1.0, (float) 0.01, 10, 10);
                        player.getWorld().spigot().playEffect(player.getLocation(), Effect.PARTICLE_SMOKE, 0, 0, (float) 1.0, (float) 1.0, (float) 1.0, (float) 0.01, 5, 5);
                    } else {
                        task.get(player.getName()).cancel();
                        task.remove(player.getName());
                    }
                }
            });
            task.get(player.getName()).runTaskTimer(HyteriaLaserStrike.getInstance(), 1L, 1L);
        }
    }
}
