package me.ollie_2411.hyterialaserstrike.game;

import me.ollie_2411.hyterialaserstrike.HyteriaLaserStrike;
import me.ollie_2411.hyterialaserstrike.arena.Arena;
import me.ollie_2411.hyterialaserstrike.arena.ArenaManager;
import me.ollie_2411.hyterialaserstrike.utils.ArenaUtils;
import me.ollie_2411.hyterialaserstrike.utils.PlayerFile;
import me.ollie_2411.hyterialaserstrike.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.Configuration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ollie on 22/02/2016.
 */
public class Game {

    public static List<Mine> mines = new ArrayList<Mine>();

    public void startGame(Player player) {
        Arena arena = ArenaManager.getManager().getArena(player);
        arena.setState(GameState.GAME);
        giveEquipment(player);

        ArenaUtils.openArenas(player, false);
        ArenaUtils.updateSign(arena);

        for (String s : arena.getPlayers()) {
            arena.arenaConfig.set("players." + s + ".kills", 0);
            arena.arenaConfig.set("players." + s + ".killstreak", 0);
            arena.save();
        }
        Board board = new Board();
        board.setGameBoard(player);
    }

    public void endGame(Arena arena) {
        List<Player> players = new ArrayList<Player>();
        for (String s : arena.getPlayers()) {
            Player player = Bukkit.getPlayer(s);
            players.add(player);
        }

        for (Player player : players) {
            ArenaManager.getManager().removePlayer(player);
            player.teleport(player.getWorld().getSpawnLocation());
        }
        arena.arenaConfig.set("players", null);
        arena.save();
    }

    public void restartGame(final Arena arena, final Player player) {
        arena.setState(GameState.RESTARTING);
        ArenaUtils.openArenas(player, false);
        ArenaUtils.updateSign(arena);

        arena.setBlueLives(60);
        arena.arenaConfig.set("blueLives", 60);
        arena.setGreenLives(60);
        arena.arenaConfig.set("greenLives", 60);
        arena.save();

        Bukkit.getScheduler().scheduleSyncDelayedTask(HyteriaLaserStrike.getInstance(), new Runnable() {
            @Override
            public void run() {
                arena.setState(GameState.LOBBY);
                ArenaUtils.openArenas(player, false);
                ArenaUtils.updateSign(arena);
            }
        }, 100L);
    }

    public void setLives(Player player) {
        TeamManager tm = TeamManager.getManager();
        Arena arena = ArenaManager.getManager().getArena(player);
        Configuration config = HyteriaLaserStrike.getPluginConfig();

        if (tm.getTeam(player) == tm.getBlue()) {
            int blueLives = arena.getBlueLives();
            blueLives--;
            arena.setBlueLives(blueLives);
            arena.arenaConfig.set("blueLives", blueLives);
            arena.save();
        } else if (tm.getTeam(player) == tm.getGreen()) {
            int greenLives = arena.getGreenLives();
            greenLives--;
            arena.setGreenLives(greenLives);
            arena.arenaConfig.set("greenLives", greenLives);
            arena.save();
        }

        if (arena.getBlueLives() == 0) {
            for (String s : arena.getPlayers()) {
                Player player2 = Bukkit.getPlayer(s);
                PlayerFile pf = new PlayerFile(player2);
                if (tm.getTeam(player2) == tm.getBlue()) {
                    int losses = pf.config.getInt("losses");
                    losses++;
                    pf.config.set("losses", losses);
                } else {
                    int wins = pf.config.getInt("wins");
                    wins++;
                    pf.config.set("wins", wins);
                }
                pf.save();
            }
            for (String s : config.getString("messages.winGame").split("\n")) {
                Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', s).replaceAll("<team>", "green").replaceAll("<arena>", arena.getName()).replaceAll("<mvp>", ArenaUtils.getArenaMvp(arena)));
            }
            endGame(arena);

        } else if (arena.getGreenLives() == 0) {
            for (String s : arena.getPlayers()) {
                Player player2 = Bukkit.getPlayer(s);
                PlayerFile pf = new PlayerFile(player2);
                if (tm.getTeam(player2) == tm.getGreen()) {
                    int losses = pf.config.getInt("losses");
                    losses++;
                    pf.config.set("losses", losses);
                } else {
                    int wins = pf.config.getInt("wins");
                    wins++;
                    pf.config.set("wins", wins);
                }
                pf.save();
            }
            for (String s : config.getString("messages.winGame").split("\n")) {
                Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', s).replaceAll("<team>", "green").replaceAll("<arena>", arena.getName()).replaceAll("<mvp>", ArenaUtils.getArenaMvp(arena)));
            }
            endGame(arena);
        }
    }

    public void setKills(Player player) {
        PlayerFile pf = new PlayerFile(player);
        int kills = pf.config.getInt("kills");
        kills++;
        pf.config.set("kills", kills);
        pf.save();

        Arena arena = ArenaManager.getManager().getArena(player);
        int playerKills = arena.arenaConfig.getInt("players." + player.getName() + ".kills");
        playerKills++;
        arena.arenaConfig.set("players." + player.getName() + ".kills", playerKills);
    }

    public void setKillstreak(Player player) {
        Arena arena = ArenaManager.getManager().getArena(player);
        int playerKillstreak = arena.arenaConfig.getInt("players." + player.getName() + ".killstreak");
        playerKillstreak++;
        arena.arenaConfig.set("players." + player.getName() + ".killstreak", playerKillstreak);
        arena.save();

        if (playerKillstreak % 5 == 0) {
            Utils.sendArenaMessage(arena, HyteriaLaserStrike.getPluginConfig().getString("messages.killstreak").replaceAll("<player>", player.getName()).replaceAll("<kills>", String.valueOf(playerKillstreak)));
        }
    }

    public void giveEquipment(Player player) {
        TeamManager tm = TeamManager.getManager();
        Arena arena = ArenaManager.getManager().getArena(player);
        Configuration config = HyteriaLaserStrike.getPluginConfig();

        if (tm.getTeam(player) == tm.getBlue()) {
            player.teleport(arena.randomSpawn("blueSpawn"));
            player.getInventory().setItem(0, Utils.createItem(new ItemStack(Material.IRON_BARDING), "&b&lLASER GUN", null));
        } else if (tm.getTeam(player) == tm.getGreen()) {
            player.teleport(arena.randomSpawn("greenSpawn"));
            player.getInventory().setItem(0, Utils.createItem(new ItemStack(Material.IRON_BARDING), "&a&lLASER GUN", null));
        }
        player.getInventory().setItem(1, Utils.createItem(new ItemStack(Material.IRON_SWORD), "&c&lKNIFE", null));
        player.getInventory().setHeldItemSlot(0);

        if (player.hasPermission(config.getString("permissions.perks.grenade"))) {
            player.getInventory().addItem(Utils.createItem(new ItemStack(Material.SLIME_BALL), "&e&lGRENADE", null));
        }
        if (player.hasPermission(config.getString("permissions.perks.mine"))) {
            player.getInventory().addItem(Utils.createItem(new ItemStack(Material.IRON_PLATE), "&a&lMINE", null));
        }
        if (player.hasPermission(config.getString("permissions.perks.invisibility"))) {
            player.getInventory().addItem(Utils.createItem(new ItemStack(Material.POTION, 1, (short) 8206), "&d&lINVISIBILITY", null));
        }
        if (player.hasPermission(config.getString("permissions.perks.invincibility"))) {
            player.getInventory().addItem(Utils.createItem(new ItemStack(Material.IRON_INGOT), "&6&lINVINCIBILITY", null));
        }
    }

    public void die(Player player) {
        if (ArenaManager.getManager().inGame(player)) {
            final Arena arena = ArenaManager.getManager().getArena(player);
            PlayerFile pf = new PlayerFile(player);
            player.setExp(1);
            player.getInventory().clear();

            int deaths = pf.config.getInt("deaths");
            deaths++;
            pf.config.set("deaths", deaths);
            pf.save();

            arena.arenaConfig.set("players." + player.getName() + ".killstreak", 0);
            arena.save();
            new Board().setGameBoard(player);
            new Game().giveEquipment(player);
        }
    }
}
