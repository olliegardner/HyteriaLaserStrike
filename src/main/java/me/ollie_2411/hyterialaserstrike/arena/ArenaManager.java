package me.ollie_2411.hyterialaserstrike.arena;

import me.ollie_2411.hyterialaserstrike.HyteriaLaserStrike;
import me.ollie_2411.hyterialaserstrike.game.*;
import me.ollie_2411.hyterialaserstrike.utils.ArenaUtils;
import me.ollie_2411.hyterialaserstrike.utils.Utils;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.configuration.Configuration;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ollie on 22/02/2016.
 */
public class ArenaManager {

    public static ArenaManager am = new ArenaManager();
    private List<Arena> arenas = new ArrayList<Arena>();
    private Configuration config = HyteriaLaserStrike.getPluginConfig();
    private Board board = new Board();

    public static ArenaManager getManager() {
        return am;
    }

    public Arena createArena(String s) {
        Arena arena = new Arena(s);
        arenas.add(arena);
        return arena;
    }

    public boolean deleteArena(String s) {
        Arena arena = getArena(s);
        if (arena == null) {
            return false;
        } else {
            arenas.remove(arena);
            File file = new File(HyteriaLaserStrike.getInstance().getDataFolder().getPath() + "/arenas", s + ".yml");
            if (file.exists()) {
                file.delete();
                return true;
            }
        }
        return false;
    }

    public void loadArenas() {
        File folder = new File(HyteriaLaserStrike.getInstance().getDataFolder().getPath() + "/arenas");
        File[] files = folder.listFiles();
        for (File file : files) {
            Arena arena = new Arena(file.getName().replaceAll(".yml", ""));
            arenas.add(arena);
        }
    }

    public Arena getArena(String s) {
        Arena arena = null;
        for (Arena a : arenas) {
            if (a.getName().equals(s)) {
                arena = a;
                break;
            }
        }
        return arena;
    }

    public Arena getArena(Player player) {
        Arena arena = null;
        for (Arena a : arenas) {
            if (a.getPlayers().contains(player.getName())) {
                arena = a;
            }
        }
        return arena;
    }

    public List<Arena> getArenas() {
        return arenas;
    }

    public void addPlayer(Player player, String s) {
        Arena arena = getArena(s);
        if (arena != null) {
            if (arena.getState() == GameState.LOBBY) {
                if (arena.getPlayers().size() >= arena.getMax()) {
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', config.getString("messages.arenaFull")));
                } else {
                    if (inGame(player)) {
                        player.sendMessage(ChatColor.translateAlternateColorCodes('&', config.getString("messages.inGame")));
                    } else {
                        arena.getPlayers().add(player.getName());
                        ArenaUtils.openArenas(player, false);
                        ArenaUtils.updateSign(arena);

                        player.teleport(arena.getLobby());
                        Utils.sendArenaMessage(arena, config.getString("messages.joinArena").replaceAll("<player>", player.getName())
                                .replaceAll("<players>", String.valueOf(arena.getPlayers().size())).replaceAll("<max>", String.valueOf(arena.getMax())));

                        player.getInventory().clear();
                        player.getInventory().setArmorContents(null);
                        player.setLevel(0);
                        player.setExp(1);
                        player.setGameMode(GameMode.SURVIVAL);
                        board.setGameBoard(player);

                        TeamManager.getManager().removePlayer(player);
                        TeamManager.getManager().addPlayer(player);
                        TeamManager.getManager().equipArmour(player);

                        if (arena.getPlayers().size() >= arena.getMin()) {
                            if (Countdown.running == false) {
                                Countdown.running = true;
                                new Countdown(HyteriaLaserStrike.getInstance(), 20, arena).runTaskTimer(HyteriaLaserStrike.getInstance(), 0, 20);
                            }
                        }
                    }
                }
            } else {
                player.sendMessage(ChatColor.RED + "This arena is currently in-game");
            }
        } else {
            player.sendMessage(ChatColor.RED + "Invalid arena");
        }
    }

    public void removePlayer(final Player player) {
        Arena arena = getArena(player);
        if (arena != null) {
            player.getInventory().clear();
            player.getInventory().setArmorContents(null);
            player.setExp(0);

            arena.getPlayers().remove(player.getName());
            board.setLobbyBoard(player);
            ArenaUtils.openArenas(player, false);
            ArenaUtils.updateSign(arena);

            if (arena.getPlayers().size() <= 1) {
                Game game = new Game();
                game.endGame(arena);
                game.restartGame(arena, player);
            }
        }
    }

    public boolean inGame(Player player) {
        for (Arena arena : arenas) {
            if (arena.getPlayers().contains(player.getName())) {
                return true;
            }
        }
        return false;
    }
}
