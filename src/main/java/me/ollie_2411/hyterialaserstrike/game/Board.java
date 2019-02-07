package me.ollie_2411.hyterialaserstrike.game;

import me.ollie_2411.hyterialaserstrike.HyteriaLaserStrike;
import me.ollie_2411.hyterialaserstrike.arena.Arena;
import me.ollie_2411.hyterialaserstrike.arena.ArenaManager;
import me.ollie_2411.hyterialaserstrike.utils.PlayerFile;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.Configuration;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.*;

/**
 * Created by Ollie on 28/02/2016.
 */
public class Board {

    private ScoreboardManager manager = Bukkit.getScoreboardManager();
    private Configuration config = HyteriaLaserStrike.getPluginConfig();

    public void setLobbyBoard(Player player) {
        Scoreboard scoreboard = manager.getNewScoreboard();
        Objective objective = scoreboard.registerNewObjective("test", "dummy");
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        objective.setDisplayName(ChatColor.translateAlternateColorCodes('&', config.getString("scoreboard.lobby.title")));

        for (int i = 1; i < 16; i++) {
            Score score = objective.getScore(ChatColor.translateAlternateColorCodes('&', findKeyword(config.getString("scoreboard.lobby." + i), player)));
            score.setScore(i);
        }
        player.setScoreboard(scoreboard);
    }

    public void setGameBoard(Player player) {
        Scoreboard scoreboard = manager.getNewScoreboard();
        Objective objective = scoreboard.registerNewObjective("test", "dummy");
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        objective.setDisplayName(ChatColor.translateAlternateColorCodes('&', config.getString("scoreboard.game.title")));

        for (int i = 1; i < 16; i++) {
            Score score = objective.getScore(ChatColor.translateAlternateColorCodes('&', findKeyword(config.getString("scoreboard.game." + i), player)));
            score.setScore(i);
        }
        player.setScoreboard(scoreboard);
    }

    public String findKeyword(String word, Player player) {
        String key = word.toString();
        PlayerFile pf = new PlayerFile(player);
        double kdr;

        if (pf.config.getInt("deaths") == 0) {
            kdr = pf.config.getDouble("kills");
        } else {
            double num = pf.config.getDouble("kills") / pf.config.getDouble("deaths");
            kdr = Math.round(num * 100.0) / 100.0;
        }

        key = key.replaceAll("<playersOnline>", String.valueOf(Bukkit.getOnlinePlayers().size())).replaceAll("<kdr>", String.valueOf(kdr))
                .replaceAll("<wins>", String.valueOf(pf.config.getInt("wins"))).replaceAll("<losses>", String.valueOf(pf.config.getInt("losses"))).replaceAll("<kills>", String.valueOf(pf.config.getInt("kills")))
                .replaceAll("<deaths>", String.valueOf(pf.config.getInt("deaths")));

        if (ArenaManager.getManager().inGame(player)) {
            Arena arena = ArenaManager.getManager().getArena(player);
            key = key.replaceAll("<arena>", arena.getName()).replaceAll("<blueLives>", String.valueOf(arena.arenaConfig.getInt("blueLives"))).replaceAll("<greenLives>", String.valueOf(arena.arenaConfig.getInt("greenLives")))
            .replaceAll("<playerKills>", String.valueOf(arena.arenaConfig.getInt("players." + player.getName() + ".kills"))).replaceAll("<killstreak>", String.valueOf(arena.arenaConfig.getInt("players." + player.getName() + ".killstreak")));
        }
        return key;
    }

    public void updateArenaBoard(Arena arena) {
        for (String s : arena.getPlayers()) {
            Player player = Bukkit.getPlayer(s);
            setGameBoard(player);
        }
    }
}
