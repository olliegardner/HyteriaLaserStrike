package me.ollie_2411.hyterialaserstrike.utils;

import me.ollie_2411.hyterialaserstrike.HyteriaLaserStrike;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;

/**
 * Created by Ollie on 28/02/2016.
 */
public class PlayerFile {

    private Plugin plugin = HyteriaLaserStrike.getInstance();
    private File playerFolder = new File(plugin.getDataFolder() + File.separator + "players");
    private File playerFile;
    public YamlConfiguration config;

    public PlayerFile(Player player) {
        if (!playerFolder.exists()) {
            playerFolder.mkdirs();
        }
        playerFile = new File(playerFolder, player.getUniqueId().toString() + ".yml");

        if (!playerFile.exists()) {
            try {
                playerFile.createNewFile();
                config = YamlConfiguration.loadConfiguration(playerFile);
                config.set("name", player.getName());
                config.set("wins", 0);
                config.set("losses", 0);
                config.set("kills", 0);
                config.set("deaths", 0);
                config.save(playerFile);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        } else {
            config = YamlConfiguration.loadConfiguration(playerFile);
        }
    }

    public void save() {
        try {
            config.save(playerFile);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
