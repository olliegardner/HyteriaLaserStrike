package me.ollie_2411.hyterialaserstrike.commands;

import me.ollie_2411.hyterialaserstrike.HyteriaLaserStrike;
import me.ollie_2411.hyterialaserstrike.arena.Arena;
import me.ollie_2411.hyterialaserstrike.arena.ArenaManager;
import me.ollie_2411.hyterialaserstrike.game.GameState;
import me.ollie_2411.hyterialaserstrike.utils.ArenaUtils;
import me.ollie_2411.hyterialaserstrike.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.Configuration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

/**
 * Created by Ollie on 22/02/2016.
 */
public class LaserCommand implements CommandExecutor {

    private HyteriaLaserStrike plugin;

    public LaserCommand(HyteriaLaserStrike instance) {
        this.plugin = instance;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String s, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            Configuration config = HyteriaLaserStrike.getPluginConfig();

            if (args.length == 0) {
                ArenaUtils.openArenas(player, true);
            } else {
                if (args.length == 1) {
                    if (args[0].equalsIgnoreCase("setup")) {
                        if (player.hasPermission(config.getString("permissions.laser"))) {
                            Inventory setup = Bukkit.createInventory(null, ((ArenaManager.getManager().getArenas().size() + 8) / 9) * 9, ChatColor.DARK_PURPLE + "Arena Setup");
                            for (Arena arena : ArenaManager.getManager().getArenas()) {
                                Utils.createItem(new ItemStack(Material.PAPER), arena.getName(), null, setup);
                            }
                            player.openInventory(setup);
                        } else {
                            player.sendMessage(ChatColor.RED + "You don't have permission");
                        }
                    }

                    if (args[0].equalsIgnoreCase("help")) {
                        player.sendMessage(ChatColor.AQUA + "/laser");
                        player.sendMessage(ChatColor.AQUA + "/laser setup");
                        player.sendMessage(ChatColor.AQUA + "/laser join [arena]");
                        player.sendMessage(ChatColor.AQUA + "/laser create [name]");
                        player.sendMessage(ChatColor.AQUA + "/laser delete [name]");
                    }

                } else if (args.length == 2) {
                    if (args[0].equalsIgnoreCase("create")) {
                        if (player.hasPermission(config.getString("permissions.laser"))) {
                            Arena arena = ArenaManager.getManager().createArena(args[1]);
                            arena.setState(GameState.LOBBY);
                            player.sendMessage(ChatColor.GREEN + "The arena " + arena.getName() + " has been created");
                        } else {
                            player.sendMessage(ChatColor.RED + "You don't have permission");
                        }
                    }

                    if (args[0].equalsIgnoreCase("delete")) {
                        if (player.hasPermission(config.getString("permissions.laser"))) {
                            if (ArenaManager.getManager().deleteArena(args[1]) == false) {
                                player.sendMessage(ChatColor.RED + "Invalid arena name");
                            } else {
                                player.sendMessage(ChatColor.GREEN + "The arena " + args[1] + " has been deleted");
                            }
                        } else {
                            player.sendMessage(ChatColor.RED + "You don't have permission");
                        }
                    }

                    if (args[0].equalsIgnoreCase("join")) {
                        if (!ArenaManager.getManager().inGame(player)) {
                            Arena arena = ArenaManager.getManager().getArena(args[1]);
                            if (arena != null) {
                                if (arena.getState() == GameState.LOBBY) {
                                    ArenaManager.getManager().addPlayer(player, args[1]);
                                } else {
                                    player.sendMessage(ChatColor.RED + "This arena is currently in game");
                                }
                            } else {
                                player.sendMessage(ChatColor.RED + "Incorrect arena name");
                            }
                        } else {
                            player.sendMessage(ChatColor.RED + "You are already in a game");
                        }
                    }
                } else {
                    player.sendMessage(ChatColor.AQUA + "/laser setup");
                    player.sendMessage(ChatColor.AQUA + "/laser create [name]");
                    player.sendMessage(ChatColor.AQUA + "/laser delete [name]");
                }
            }
        }
        return false;
    }
}
