package me.ollie_2411.hyterialaserstrike.listeners;

import me.ollie_2411.hyterialaserstrike.HyteriaLaserStrike;
import me.ollie_2411.hyterialaserstrike.arena.Arena;
import me.ollie_2411.hyterialaserstrike.arena.ArenaManager;
import me.ollie_2411.hyterialaserstrike.utils.ArenaUtils;
import me.ollie_2411.hyterialaserstrike.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.Configuration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ollie on 22/02/2016.
 */
public class ClickListener implements Listener {
    
    public ClickListener(HyteriaLaserStrike instance) {
        instance.getServer().getPluginManager().registerEvents(this, instance);
    }

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        if (e.getCurrentItem() == null || e.getCurrentItem().getItemMeta() == null) {
            return;
        } else {
            Player player = (Player) e.getWhoClicked();
            ItemStack item = e.getCurrentItem();
            String itemName = ChatColor.stripColor(item.getItemMeta().getDisplayName());
            String openInv = ChatColor.stripColor(player.getOpenInventory().getTitle());
            Configuration config = HyteriaLaserStrike.getPluginConfig();

            if (openInv.equalsIgnoreCase("Arena Setup")) {
                if (item.getType() == Material.PAPER) {
                    e.setCancelled(true);

                    Inventory settings = Bukkit.createInventory(null, 9, ChatColor.DARK_PURPLE + itemName + " Settings");
                    Utils.createItem(new ItemStack(Material.BOOK_AND_QUILL), "&e" + itemName, "&7The name of the arena", settings);
                    Utils.createItem(new ItemStack(Material.COMPASS), "&eLobby Spawn", "&7Click to set the lobby spawn for \n&7this arena", settings);
                    Utils.createItem(new ItemStack(Material.WOOL, 1, (short) 9), "&eBlue Spawn", "&7Click to add a &3blue &7spawn for \n&7this arena", settings);
                    Utils.createItem(new ItemStack(Material.WOOL, 1, (short) 5), "&eGreen Spawn", "&7Click to add a &agreen &7spawn for \n&7this arena", settings);
                    Utils.createItem(new ItemStack(Material.EYE_OF_ENDER), "&eMinimum Players", "&7Click to select the minimum amount of players \n&7needed for the game to start", settings);
                    Utils.createItem(new ItemStack(Material.ENDER_PEARL), "&eMaximum Players", "&7Click to select the maximum amount of players \n&7that can join the arena", settings);
                    Utils.createItem(new ItemStack(Material.STAINED_CLAY, 1, (short) 3), "&eBlue Recharge", "&7Click to set the &3blue &7recharge area for \n&7this arena", settings);
                    Utils.createItem(new ItemStack(Material.STAINED_CLAY, 1, (short) 5), "&eGreen Recharge", "&7Click to set the &agreen &7recharge area for \n&7this arena", settings);
                    player.openInventory(settings);
                }
            }


            if (openInv.contains("Settings")) {
                e.setCancelled(true);
                Arena arena = ArenaManager.getManager().getArena(openInv.replaceAll(" Settings", ""));

                if (itemName.equalsIgnoreCase("Lobby Spawn")) {
                    arena.setLobby(player.getLocation());
                    arena.arenaConfig.set("lobby", player.getLocation());
                    arena.save();
                    player.sendMessage(ChatColor.GREEN + "Lobby spawn set to your location");
                }

                if (itemName.equalsIgnoreCase("Blue Spawn")) {
                    List<Location> spawns = new ArrayList<Location>();
                    if (arena.arenaConfig.getList("blueSpawn") != null) spawns = (List<Location>) arena.arenaConfig.getList("blueSpawn");
                    spawns.add(player.getLocation());
                    arena.setGreenSpawn(spawns);
                    arena.arenaConfig.set("blueSpawn", spawns);
                    arena.save();
                    player.sendMessage(ChatColor.GREEN + "Blue spawn added at your location");
                }

                if (itemName.equalsIgnoreCase("Green Spawn")) {
                    List<Location> spawns = new ArrayList<Location>();
                    if (arena.arenaConfig.getList("greenSpawn") != null) spawns = (List<Location>) arena.arenaConfig.getList("greenSpawn");
                    spawns.add(player.getLocation());
                    arena.setGreenSpawn(spawns);
                    arena.arenaConfig.set("greenSpawn", spawns);
                    arena.save();
                    player.sendMessage(ChatColor.GREEN + "Green spawn added at your location");
                }

                if (itemName.equalsIgnoreCase("Minimum Players")) {
                    Inventory min = Bukkit.createInventory(null, 36, ChatColor.DARK_PURPLE + "Min players for " + arena.getName());
                    for (int i = 0; i < 36; i++) {
                        Utils.createItem(new ItemStack(Material.EYE_OF_ENDER, i + 1), "&e" + (i + 1) + " Players", null, min);
                    }
                    player.openInventory(min);
                }

                if (itemName.equalsIgnoreCase("Maximum Players")) {
                    Inventory max = Bukkit.createInventory(null, 36, ChatColor.DARK_PURPLE + "Max players for " + arena.getName());
                    for (int i = 0; i < 36; i++) {
                        Utils.createItem(new ItemStack(Material.ENDER_PEARL, i + 1), "&e" + (i + 1) + " Players", null, max);
                    }
                    player.openInventory(max);
                }

                if (itemName.equalsIgnoreCase("Blue Recharge")) {
                    ArenaUtils.saveRegion(player, arena, "blueRecharge");
                }

                if (itemName.equalsIgnoreCase("Green Recharge")) {
                    ArenaUtils.saveRegion(player, arena, "greenRecharge");
                }
            }


            if (openInv.contains("Min players")) {
                e.setCancelled(true);
                if (item.getType() == Material.EYE_OF_ENDER) {
                    Arena arena = ArenaManager.getManager().getArena(openInv.replaceAll("Min players for ", ""));
                    arena.setMin(item.getAmount());
                    arena.arenaConfig.set("min", item.getAmount());
                    arena.save();
                    player.sendMessage(ChatColor.GREEN + "Minimum players set to " + item.getAmount());
                    player.performCommand("laser setup");
                }
            }


            if (openInv.contains("Max players")) {
                e.setCancelled(true);
                if (item.getType() == Material.ENDER_PEARL) {
                    Arena arena = ArenaManager.getManager().getArena(openInv.replaceAll("Max players for ", ""));
                    arena.setMax(item.getAmount());
                    arena.arenaConfig.set("max", item.getAmount());
                    arena.save();
                    player.sendMessage(ChatColor.GREEN + "Maximum players set to " + item.getAmount());
                    player.performCommand("laser setup");
                }
            }


            if (player.getOpenInventory().getTitle().equalsIgnoreCase(ChatColor.translateAlternateColorCodes('&', config.getString("laser.name")))) {
                e.setCancelled(true);
                ItemStack join = new ItemStack(Utils.parseItemStack(config.getString("laser.join.item")));
                if ((item.getType() == join.getType()) && (item.getDurability() == join.getDurability())) {
                    ArenaManager.getManager().addPlayer(player, itemName);
                }
            }

            if (ArenaManager.getManager().inGame(player)) {
                e.setCancelled(true);
            }
        }
    }
}
