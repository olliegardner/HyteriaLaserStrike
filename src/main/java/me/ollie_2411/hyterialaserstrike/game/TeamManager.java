package me.ollie_2411.hyterialaserstrike.game;

import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by Ollie on 22/02/2016.
 */
public class TeamManager {

    public static TeamManager tm = new TeamManager();
    private List<String> blue = new ArrayList<String>();
    private List<String> green = new ArrayList<String>();
    private List<String> invincible = new ArrayList<String>();

    public static TeamManager getManager() {
        return tm;
    }

    public List<String> getTeam(Player player) {
        List<String> team = null;
        if (blue.contains(player.getName())) {
            team = blue;
        } else if (green.contains(player.getName())) {
            team = green;
        }
        return team;
    }

    public void addPlayer(Player player) {
        Random random = new Random();
        if (blue.contains(player.getName())) {
            blue.remove(player.getName());
        }
        if (green.contains(player.getName())) {
            green.remove(player.getName());
        }

        if (blue.size() == green.size()) {
            if (random.nextBoolean()) {
                blue.add(player.getName());
            } else {
                green.add(player.getName());
            }
        } else {
            if (blue.size() < green.size()) {
                blue.add(player.getName());
            } else {
                green.add(player.getName());
            }
        }
    }

    public void removePlayer(Player player) {
        if (getTeam(player) != null) {
            getTeam(player).remove(player.getName());
        }
    }

    public void equipArmour(Player player) {
        Color color = null;
        if (getTeam(player) == blue) {
            color = Color.fromRGB(30, 144, 255);
            player.getInventory().setHelmet(new ItemStack(Material.WOOL, 1, (short) 9));
        } else if (getTeam(player) == green) {
            color = Color.LIME;
            player.getInventory().setHelmet(new ItemStack(Material.WOOL, 1, (short) 5));
        }
        ItemStack chestplate = new ItemStack(Material.LEATHER_CHESTPLATE);
        LeatherArmorMeta chestplateMeta = (LeatherArmorMeta) chestplate.getItemMeta();
        chestplateMeta.setColor(color);
        chestplate.setItemMeta(chestplateMeta);
        player.getInventory().setChestplate(chestplate);

        ItemStack leggings = new ItemStack(Material.LEATHER_LEGGINGS);
        LeatherArmorMeta leaggingsMeta = (LeatherArmorMeta) leggings.getItemMeta();
        leaggingsMeta.setColor(color);
        leggings.setItemMeta(leaggingsMeta);
        player.getInventory().setLeggings(leggings);

        ItemStack boots = new ItemStack(Material.LEATHER_BOOTS);
        LeatherArmorMeta bootsMeta = (LeatherArmorMeta) boots.getItemMeta();
        bootsMeta.setColor(color);
        boots.setItemMeta(bootsMeta);
        player.getInventory().setBoots(boots);
    }

    public List<String> getGreen() {
        return green;
    }

    public List<String> getBlue() {
        return blue;
    }

    public List<String> getInvincible() {
        return invincible;
    }
}
