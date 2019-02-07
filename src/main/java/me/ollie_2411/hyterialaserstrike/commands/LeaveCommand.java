package me.ollie_2411.hyterialaserstrike.commands;

import me.ollie_2411.hyterialaserstrike.HyteriaLaserStrike;
import me.ollie_2411.hyterialaserstrike.arena.Arena;
import me.ollie_2411.hyterialaserstrike.arena.ArenaManager;
import me.ollie_2411.hyterialaserstrike.utils.Utils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.Configuration;
import org.bukkit.entity.Player;

/**
 * Created by Ollie on 22/02/2016.
 */
public class LeaveCommand implements CommandExecutor {

    private HyteriaLaserStrike plugin;

    public LeaveCommand(HyteriaLaserStrike instance) {
        this.plugin = instance;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String s, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            Configuration config = HyteriaLaserStrike.getPluginConfig();

            if (ArenaManager.getManager().inGame(player)) {
                Arena arena = ArenaManager.getManager().getArena(player);
                ArenaManager.getManager().removePlayer(player);
                player.teleport(player.getWorld().getSpawnLocation());
                player.getInventory().clear();
                player.getInventory().setArmorContents(null);
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', HyteriaLaserStrike.getPluginConfig().getString("messages.leftGame")));
                Utils.sendArenaMessage(arena, config.getString("messages.leftArena").replaceAll("<player>", player.getName()).replaceAll("<players>", String.valueOf(arena.getPlayers().size())).replaceAll("<max>", String.valueOf(arena.getMax())));
            } else {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', config.getString("messages.notInGame")));
            }
        }
        return false;
    }
}
