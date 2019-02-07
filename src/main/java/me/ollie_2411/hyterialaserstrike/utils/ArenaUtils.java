package me.ollie_2411.hyterialaserstrike.utils;

import com.sk89q.worldedit.BlockVector;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.bukkit.selections.Selection;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.domains.DefaultDomain;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import me.ollie_2411.hyterialaserstrike.HyteriaLaserStrike;
import me.ollie_2411.hyterialaserstrike.arena.Arena;
import me.ollie_2411.hyterialaserstrike.arena.ArenaManager;
import me.ollie_2411.hyterialaserstrike.game.GameState;
import me.ollie_2411.hyterialaserstrike.game.TeamManager;
import org.apache.commons.lang.StringUtils;
import org.bukkit.*;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.configuration.Configuration;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.Plugin;

/**
 * Created by Ollie on 22/02/2016.
 */
public class ArenaUtils {

    public static void openArenas(Player player, boolean forceOpen) {
        Inventory arenas = null;
        Configuration config = HyteriaLaserStrike.getPluginConfig();

        for (Player online : Bukkit.getOnlinePlayers()) {
            arenas = Bukkit.createInventory(null, ((ArenaManager.getManager().getArenas().size() + 8) / 9) * 9, ChatColor.translateAlternateColorCodes('&', config.getString("laser.name")));
            for (Arena arena : ArenaManager.getManager().getArenas()) {
                if (arena.getState() == GameState.LOBBY) {
                    Utils.createItem(Utils.parseItemStack(config.getString("laser.join.item")), config.getString("laser.join.name").replaceAll("<arena>", arena.getName()), config.getString("laser.join.lore").replaceAll("<arena>", arena.getName()).replaceAll("<state>", StringUtils.capitalize(arena.getState().name().toLowerCase())).replaceAll("<players>", String.valueOf(arena.getPlayers().size())).replaceAll("<max>", String.valueOf(arena.getMax())), arenas);
                }

                if (arena.getState() == GameState.GAME) {
                    Utils.createItem(Utils.parseItemStack(config.getString("laser.game.item")), config.getString("laser.game.name").replaceAll("<arena>", arena.getName()), config.getString("laser.game.lore").replaceAll("<arena>", arena.getName()).replaceAll("<state>", StringUtils.capitalize(arena.getState().name().toLowerCase())).replaceAll("<players>", String.valueOf(arena.getPlayers().size())).replaceAll("<max>", String.valueOf(arena.getMax())), arenas);
                }

                if (arena.getState() == GameState.RESTARTING) {
                    Utils.createItem(Utils.parseItemStack(config.getString("laser.restart.item")), config.getString("laser.restart.name").replaceAll("<arena>", arena.getName()), config.getString("laser.restart.lore").replaceAll("<arena>", arena.getName()).replaceAll("<state>", StringUtils.capitalize(arena.getState().name().toLowerCase())).replaceAll("<players>", String.valueOf(arena.getPlayers().size())).replaceAll("<max>", String.valueOf(arena.getMax())), arenas);
                }
            }
            if (online.getOpenInventory().getTitle().equalsIgnoreCase(arenas.getTitle())) {
                online.openInventory(arenas);
            }
        }

        if (forceOpen) player.openInventory(arenas);
    }

    public static void updateSign(Arena arena) {
        Configuration config = HyteriaLaserStrike.getPluginConfig();
        if (arena.getLobby() != null) {
            for (Chunk chunk : arena.getLobby().getWorld().getLoadedChunks()) {
                for (BlockState blockState : chunk.getTileEntities()) {
                    if (blockState.getType() == Material.SIGN || blockState.getType() == Material.WALL_SIGN || blockState.getType() == Material.SIGN_POST) {
                        Sign sign = (Sign) blockState;

                        if (sign.getLine(0).equalsIgnoreCase(ChatColor.translateAlternateColorCodes('&', config.getString("sign.1")))) {
                            if (ArenaManager.getManager().getArena(ChatColor.stripColor(sign.getLine(1))).getName().equalsIgnoreCase(arena.getName())) {
                                for (int i = 1; i < 5; i++) {
                                    sign.setLine(i - 1, ChatColor.translateAlternateColorCodes('&', config.getString("sign." + i).replaceAll("<arena>", arena.getName()).replaceAll("<state>", StringUtils.capitalize(arena.getState().name().toLowerCase()))
                                            .replaceAll("<players>", String.valueOf(arena.getPlayers().size())).replaceAll("<max>", String.valueOf(arena.getMax()))));
                                    sign.update();
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    public static void saveRegion(Player player, Arena arena, String name) {
        Selection selection = getWorldEdit().getSelection(player);
        if (selection == null) {
            player.sendMessage(ChatColor.RED + "Make a selection first using //wand");
            return;
        }

        ProtectedCuboidRegion region = new ProtectedCuboidRegion(arena.getName() + "_" + name, new BlockVector(selection.getNativeMinimumPoint()), new BlockVector(selection.getNativeMaximumPoint()));
        DefaultDomain owners = new DefaultDomain();
        owners.addPlayer(getWorldGuard().wrapPlayer(player));
        region.setOwners(owners);
        getWorldGuard().getRegionManager(player.getWorld()).addRegion(region);
        player.sendMessage(ChatColor.GREEN + "Created " + name + " recharging area");
    }

    public static boolean playerInRechargeArea(PlayerMoveEvent e, Arena arena) {
        Player player = e.getPlayer();
        Location location = player.getLocation();
        Vector v = new Vector(location.getX(), location.getY(), location.getZ());
        TeamManager tm = TeamManager.getManager();
        boolean found = false;

        if (!(e.getTo()).equals(e.getFrom())) {
            if (tm.getTeam(player) == tm.getBlue()) {
                ProtectedRegion region = getWorldGuard().getRegionManager(player.getWorld()).getRegion(arena.getName() + "_blueRecharge");
                if (region.contains(v)) {
                    found = true;
                }
            } else if (tm.getTeam(player) == tm.getGreen()) {
                ProtectedRegion region = getWorldGuard().getRegionManager(player.getWorld()).getRegion(arena.getName() + "_greenRecharge");
                if (region.contains(v)) {
                    found = true;
                }
            }
        }
        return found;
    }

    private static WorldEditPlugin getWorldEdit() {
        Plugin plugin = Bukkit.getServer().getPluginManager().getPlugin("WorldEdit");
        if (plugin == null || !(plugin instanceof WorldEditPlugin)) {
            return null;
        }
        return (WorldEditPlugin) plugin;
    }

    private static WorldGuardPlugin getWorldGuard() {
        Plugin plugin = Bukkit.getServer().getPluginManager().getPlugin("WorldGuard");
        if (plugin == null || !(plugin instanceof WorldGuardPlugin)) {
            return null;
        }
        return (WorldGuardPlugin) plugin;
    }

    public static String getArenaMvp(Arena arena) {
        String mvp = null;
        int kills = 0;
        for (String s : arena.getPlayers()) {
            if (arena.arenaConfig.getInt("players." + s + ".kills") > kills) {
                kills = arena.arenaConfig.getInt("players." + s + ".kills");
                mvp = s;
            }
        }
        return mvp;
    }
}
