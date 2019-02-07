package me.ollie_2411.hyterialaserstrike.listeners;

import me.ollie_2411.hyterialaserstrike.HyteriaLaserStrike;
import me.ollie_2411.hyterialaserstrike.arena.Arena;
import me.ollie_2411.hyterialaserstrike.arena.ArenaManager;
import me.ollie_2411.hyterialaserstrike.game.Board;
import me.ollie_2411.hyterialaserstrike.game.TeamManager;
import me.ollie_2411.hyterialaserstrike.utils.PlayerFile;
import me.ollie_2411.hyterialaserstrike.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * Created by Ollie on 24/02/2016.
 */
public class DeathListener implements Listener {

    public DeathListener(HyteriaLaserStrike instance) {
        instance.getServer().getPluginManager().registerEvents(this, instance);
    }

    @EventHandler
    public void onDeath(final PlayerDeathEvent e) {
        new BukkitRunnable()
        {
            public void run()
            {
                try
                {
                    Object nmsPlayer = e.getEntity().getClass().getMethod("getHandle").invoke(e.getEntity());
                    Object con = nmsPlayer.getClass().getDeclaredField("playerConnection").get(nmsPlayer);

                    Class< ? > EntityPlayer = Class.forName(nmsPlayer.getClass().getPackage().getName() + ".EntityPlayer");

                    Field minecraftServer = con.getClass().getDeclaredField("minecraftServer");
                    minecraftServer.setAccessible(true);
                    Object mcserver = minecraftServer.get(con);

                    Object playerlist = mcserver.getClass().getDeclaredMethod("getPlayerList").invoke(mcserver);
                    Method moveToWorld = playerlist.getClass().getMethod("moveToWorld" , EntityPlayer , int.class , boolean.class);
                    moveToWorld.invoke(playerlist , nmsPlayer , 0 , false);
                }
                catch (Exception ex)
                {
                    ex.printStackTrace();
                }
            }
        }.runTaskLater(HyteriaLaserStrike.getInstance() , 2);



        final Player player = e.getEntity();
        if (ArenaManager.getManager().inGame(player)) {
            final Arena arena = ArenaManager.getManager().getArena(player);
            final TeamManager tm = TeamManager.getManager();
            PlayerFile pf = new PlayerFile(player);

            player.setHealth(20);
            e.getDrops().clear();
            e.setDroppedExp(0);
            e.setDeathMessage(null);

            int deaths = pf.config.getInt("deaths");
            deaths++;
            pf.config.set("deaths", deaths);
            pf.save();

            arena.arenaConfig.set("players." + player.getName() + ".killstreak", 0);
            arena.save();

            Board board = new Board();
            board.setGameBoard(player);

            Bukkit.getScheduler().scheduleSyncDelayedTask(HyteriaLaserStrike.getInstance(), new Runnable() {
                @Override
                public void run() {
                    if (tm.getTeam(player) == tm.getBlue()) {
                        player.teleport(arena.randomSpawn("blueSpawn"));
                        player.getInventory().setItem(0, Utils.createItem(new ItemStack(Material.IRON_BARDING), "&b&lLaser Gun", null));
                    } else if (tm.getTeam(player) == tm.getGreen()) {
                        player.teleport(arena.randomSpawn("greenSpawn"));
                        player.getInventory().setItem(0, Utils.createItem(new ItemStack(Material.IRON_BARDING), "&a&lLaser Gun", null));
                    }
                    tm.equipArmour(player);
                    player.getInventory().setItem(1, Utils.createItem(new ItemStack(Material.IRON_SWORD), "&c&lKnife", null));
                    player.getInventory().setHeldItemSlot(0);
                }
            }, 1L);
        }
    }
}
