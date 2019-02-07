package me.ollie_2411.hyterialaserstrike.listeners;

import me.ollie_2411.hyterialaserstrike.HyteriaLaserStrike;
import me.ollie_2411.hyterialaserstrike.arena.ArenaManager;
import me.ollie_2411.hyterialaserstrike.game.Board;
import me.ollie_2411.hyterialaserstrike.game.Game;
import me.ollie_2411.hyterialaserstrike.game.TeamManager;
import me.ollie_2411.hyterialaserstrike.utils.Utils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.Configuration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;

/**
 * Created by Ollie on 24/02/2016.
 */
public class EntityDamageEntityListener implements Listener {

    public EntityDamageEntityListener(HyteriaLaserStrike instance) {
        instance.getServer().getPluginManager().registerEvents(this, instance);
    }

    @EventHandler
    public void onEntityDamageEntity(EntityDamageByEntityEvent e) {
        if (e.getDamager() instanceof Player) {
            if (e.getEntity() instanceof Player) {
                Player damager = (Player) e.getDamager();
                Player target = (Player) e.getEntity();

                ItemStack item = damager.getItemInHand();
                if (item.hasItemMeta()) {
                    String name = ChatColor.stripColor(item.getItemMeta().getDisplayName());
                    TeamManager tm = TeamManager.getManager();
                    Configuration config = HyteriaLaserStrike.getPluginConfig();
                    Game game = new Game();

                    if (item != null && item.getType() == Material.IRON_SWORD && name.equalsIgnoreCase("Knife")) {
                        if (tm.getTeam(target) != tm.getTeam(damager)) {
                            e.setDamage(0);
                            if (!tm.getInvincible().contains(target.getName())) {
                                game.setKills(damager);
                                game.setLives(target);
                                game.setKillstreak(damager);
                                game.die(target);
                                target.setHealth(20);

                                ItemStack newSword = item;
                                newSword.setDurability((short) 0);
                                damager.setItemInHand(newSword);
                                Utils.sendArenaActionBarMessage(ArenaManager.getManager().getArena(damager), config.getString("messages.kill").replaceAll("<killer>", damager.getName()).replaceAll("<target>", target.getName()));
                                new Board().updateArenaBoard(ArenaManager.getManager().getArena(damager));
                            }
                        }
                    }
                }
            }
        }
    }
}
