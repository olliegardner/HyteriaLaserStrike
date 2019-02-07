package me.ollie_2411.hyterialaserstrike.utils;

import me.ollie_2411.hyterialaserstrike.HyteriaLaserStrike;
import me.ollie_2411.hyterialaserstrike.arena.Arena;
import net.minecraft.server.v1_8_R3.AxisAlignedBB;
import net.minecraft.server.v1_8_R3.IChatBaseComponent;
import net.minecraft.server.v1_8_R3.PacketPlayOutChat;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * Created by Ollie on 22/02/2016.
 */
public class Utils {

    public static void createItem(ItemStack itemStack, String name, String loreText, Inventory inventory) {
        ArrayList<String> lore = new ArrayList<String>();
        ItemStack item = new ItemStack(itemStack);
        ItemMeta itemMeta = item.getItemMeta();
        itemMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', name));
        if (loreText != null) {
            for (String s : loreText.split("\n")) {
                lore.add(ChatColor.translateAlternateColorCodes('&', s));
            }
            itemMeta.setLore(lore);
        }
        item.setItemMeta(itemMeta);

        inventory.addItem(item);
    }

    public static ItemStack createItem(ItemStack itemStack, String name, String loreText) {
        ArrayList<String> lore = new ArrayList<String>();
        ItemStack item = new ItemStack(itemStack);
        ItemMeta itemMeta = item.getItemMeta();
        itemMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', name));
        if (loreText != null) {
            for (String s : loreText.split("\n")) {
                lore.add(ChatColor.translateAlternateColorCodes('&', s));
            }
            itemMeta.setLore(lore);
        }
        item.setItemMeta(itemMeta);

        return item;
    }

    public static ItemStack parseItemStack(String item) {
        String[] parts = item.split(":");
        String material = null;
        String id = null;

        if (parts.length == 2) {
            material = parts[0];
            id = parts[1];
        }
        return new ItemStack(Material.getMaterial(Integer.parseInt(material)), 1, Byte.parseByte(id));
    }

    public static void sendArenaMessage(Arena arena, String message) {
        for (String s : arena.getPlayers()) {
            Player player = Bukkit.getPlayer(s);
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
        }
    }

    public static void sendArenaActionBarMessage(Arena arena, String message) {
        for (String s : arena.getPlayers()) {
            CraftPlayer player = (CraftPlayer) Bukkit.getPlayer(s);
            IChatBaseComponent component = IChatBaseComponent.ChatSerializer.a("{\"text\":\"" + ChatColor.translateAlternateColorCodes('&', message) + "\"}");
            PacketPlayOutChat packet = new PacketPlayOutChat(component, (byte) 2);
            player.getHandle().playerConnection.sendPacket(packet);
        }
    }

    public static Entity getNearestEntityInSight(Player player, int range) { // Credit - http://stackoverflow.com/questions/31258964/how-would-i-detect-if-a-player-is-looking-at-another-entity
        List<Entity> entities = player.getNearbyEntities(range, range, range);
        Iterator<Entity> iterator = entities.iterator();
        while (iterator.hasNext()) {
            Entity next = iterator.next();
            if (!(next instanceof LivingEntity) || next == player) {
                iterator.remove();
            }
        }
        List<Block> sight = player.getLineOfSight((Set) null, range);
        for (Block block : sight) {
            if (block.getType() != Material.AIR) {
                break;
            }
            Location low = block.getLocation();
            Location high = low.clone().add(1, 1, 1);
            AxisAlignedBB blockBoundingBox = AxisAlignedBB.a(low.getX(), low.getY(), low.getZ(), high.getX(), high.getY(), high.getZ());
            for (Entity entity : entities) {
                if (entity.getLocation().distance(player.getEyeLocation()) <= range && ((CraftEntity) entity).getHandle().getBoundingBox().b(blockBoundingBox)) {
                    return entity;
                }
            }
        }
        return null;
    }

    public static void noDeathScreen(final PlayerDeathEvent e) {
        new BukkitRunnable() {
            @Override
            public void run() {
                try {
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
                catch (Exception ex) {

                }
            }
        }.runTaskLater(HyteriaLaserStrike.getInstance() , 2);
    }
}
