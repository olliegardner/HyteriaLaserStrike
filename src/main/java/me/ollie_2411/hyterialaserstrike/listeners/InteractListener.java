package me.ollie_2411.hyterialaserstrike.listeners;

import com.nametagedit.plugin.NametagEdit;
import me.ollie_2411.hyterialaserstrike.HyteriaLaserStrike;
import me.ollie_2411.hyterialaserstrike.arena.Arena;
import me.ollie_2411.hyterialaserstrike.arena.ArenaManager;
import me.ollie_2411.hyterialaserstrike.game.*;
import me.ollie_2411.hyterialaserstrike.utils.PlayerFile;
import me.ollie_2411.hyterialaserstrike.utils.Utils;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.configuration.Configuration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.List;

/**
 * Created by Ollie on 24/02/2016.
 */
public class InteractListener implements Listener {

    public InteractListener(HyteriaLaserStrike instance) {
        instance.getServer().getPluginManager().registerEvents(this, instance);
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent e) {
        final Player player = e.getPlayer();
        ItemStack item = player.getItemInHand();
        final Configuration config = HyteriaLaserStrike.getPluginConfig();
        final TeamManager tm = TeamManager.getManager();
        final Board board = new Board();
        final Game game = new Game();
        final Arena arena = ArenaManager.getManager().getArena(player);

        if (e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK) {
            if (item != null && item.hasItemMeta()) {
                String itemName = ChatColor.stripColor(item.getItemMeta().getDisplayName());
                if (item.getType() == Material.IRON_BARDING && itemName.equalsIgnoreCase("Laser Gun")) {
                    boolean hit = false;

                    if (player.getExp() > 0) {
                        player.setExp(player.getExp() - ((1F / (10 * 20)) * 18));

                        for (int i = 1; i <= 75 && !hit; ++i) {
                            final Location loc = player.getLocation().add(0, 0.5, 0);
                            loc.setY(loc.getY() + 1);
                            loc.setX(loc.getX() + Math.cos(Math.toRadians(loc.getYaw() + 90.0f)) * i);
                            loc.setZ(loc.getZ() + Math.sin(Math.toRadians(loc.getYaw() + 90.0f)) * i);
                            loc.setY(loc.getY() + i / Math.tan(Math.toRadians(loc.getPitch() - 90.0f)));

                            if (loc.getBlock().getType().isSolid()) hit = true;

                            if (tm.getTeam(player) == tm.getBlue()) {
                                player.getWorld().spigot().playEffect(loc, Effect.TILE_BREAK, 35, 9, 0, 0, 0, 0, 1, 500);
                            } else if (tm.getTeam(player) == tm.getGreen()) {
                                player.getWorld().spigot().playEffect(loc, Effect.TILE_BREAK, 35, 5, 0, 0, 0, 0, 1, 500);
                            }
                        }

                        if (Utils.getNearestEntityInSight(player, 75) instanceof Player) {
                            Player target = (Player) Utils.getNearestEntityInSight(player, 75);
                            if (tm.getTeam(target) != tm.getTeam(player)) {
                                if (!tm.getInvincible().contains(target.getName())) {
                                    game.die(target);
                                    target.setHealth(20);
                                    Utils.sendArenaActionBarMessage(ArenaManager.getManager().getArena(player), config.getString("messages.kill").replaceAll("<killer>", player.getName()).replaceAll("<target>", target.getName()));

                                    game.setKills(player);
                                    game.setLives(target);
                                    game.setKillstreak(player);
                                    board.updateArenaBoard(ArenaManager.getManager().getArena(player));
                                }
                            }
                        }
                    } else {
                        player.sendMessage(ChatColor.translateAlternateColorCodes('&', config.getString("messages.noAmmo")));
                    }
                }

                if (item.getType() == Material.SLIME_BALL && itemName.equalsIgnoreCase("Grenade")) {
                    final Item slime = player.getWorld().dropItem(player.getEyeLocation(), new ItemStack(Material.SLIME_BALL));
                    slime.setVelocity(player.getEyeLocation().getDirection().multiply(1.5));
                    slime.setPickupDelay(Integer.MAX_VALUE);

                    player.setItemInHand(null);

                    Bukkit.getScheduler().scheduleSyncDelayedTask(HyteriaLaserStrike.getInstance(), new Runnable() {
                        @Override
                        public void run() {
                            Location location = slime.getLocation();
                            effects(player, location);
                            int enemies = 0;

                            for (Entity entity : slime.getNearbyEntities(4, 4, 4)) {
                                if (entity instanceof Player) {
                                    enemies++;
                                    Player target = (Player) entity;
                                    if (tm.getTeam(target) != tm.getTeam(player)) {
                                        if (!tm.getInvincible().contains(target.getName())) {
                                            game.die(target);
                                            target.setHealth(20);
                                            Utils.sendArenaActionBarMessage(ArenaManager.getManager().getArena(player), config.getString("messages.kill").replaceAll("<killer>", player.getName()).replaceAll("<target>", target.getName()));

                                            game.setKills(player);
                                            game.setLives(target);
                                            game.setKillstreak(player);
                                            board.updateArenaBoard(arena);
                                        }
                                    }
                                }
                            }
                            slime.remove();

                            if (enemies == 2) {
                                Utils.sendArenaMessage(arena, HyteriaLaserStrike.getPluginConfig().getString("messages.doubleKill").replaceAll("<player>", player.getName()));
                            } else if (enemies == 3) {
                                Utils.sendArenaMessage(arena, HyteriaLaserStrike.getPluginConfig().getString("messages.tripleKill").replaceAll("<player>", player.getName()));
                            }
                        }
                    }, 40L);
                }

                if (item.getType() == Material.IRON_INGOT && itemName.equalsIgnoreCase("Invincibility")) {
                    if (!player.hasPotionEffect(PotionEffectType.INVISIBILITY)) {
                        if (tm.getInvincible().contains(player.getName())) {
                            tm.getInvincible().remove(player.getName());
                        }
                        tm.getInvincible().add(player.getName());
                        player.setItemInHand(null);
                        NametagEdit.getApi().setPrefix(player.getName(), ChatColor.translateAlternateColorCodes('&', config.getString("perks.invincibilityTag")));

                        ItemStack chestplate = new ItemStack(Material.LEATHER_CHESTPLATE);
                        LeatherArmorMeta chestplateMeta = (LeatherArmorMeta) chestplate.getItemMeta();
                        chestplateMeta.setColor(Color.RED);
                        chestplate.setItemMeta(chestplateMeta);
                        player.getInventory().setChestplate(chestplate);

                        ItemStack leggings = new ItemStack(Material.LEATHER_LEGGINGS);
                        LeatherArmorMeta leaggingsMeta = (LeatherArmorMeta) leggings.getItemMeta();
                        leaggingsMeta.setColor(Color.RED);
                        leggings.setItemMeta(leaggingsMeta);
                        player.getInventory().setLeggings(leggings);

                        ItemStack boots = new ItemStack(Material.LEATHER_BOOTS);
                        LeatherArmorMeta bootsMeta = (LeatherArmorMeta) boots.getItemMeta();
                        bootsMeta.setColor(Color.RED);
                        boots.setItemMeta(bootsMeta);
                        player.getInventory().setBoots(boots);

                        Bukkit.getScheduler().scheduleSyncDelayedTask(HyteriaLaserStrike.getInstance(), new Runnable() {
                            @Override
                            public void run() {
                                tm.getInvincible().remove(player.getName());
                                NametagEdit.getApi().setPrefix(player.getName(), ChatColor.WHITE + "");
                                player.getInventory().setArmorContents(null);
                                tm.equipArmour(player);
                            }
                        }, 20 * config.getInt("perks.invincibilityTime"));
                    } else {
                        player.sendMessage(ChatColor.RED + "You currently have invisibility enabled");
                    }
                }

                if (item.getType() == Material.POTION && itemName.equalsIgnoreCase("Invisibility")) {
                    e.setCancelled(true);
                    if (!tm.getInvincible().contains(player.getName())) {
                        player.setItemInHand(null);
                        player.getInventory().setArmorContents(null);
                        player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 20 * config.getInt("perks.invisibilityTime"), 0));

                        Bukkit.getScheduler().scheduleSyncDelayedTask(HyteriaLaserStrike.getInstance(), new Runnable() {
                            @Override
                            public void run() {
                                TeamManager.getManager().equipArmour(player);
                            }
                        }, 20 * config.getInt("perks.invisibilityTime"));
                    } else {
                        player.sendMessage(ChatColor.RED + "You currently have invincibility enabled");
                    }
                }
            }
        }

        if (e.getClickedBlock() != null && (e.getClickedBlock().getType() == Material.SIGN || e.getClickedBlock().getType() == Material.SIGN_POST || e.getClickedBlock().getType() == Material.WALL_SIGN) && e.getAction() == Action.RIGHT_CLICK_BLOCK) {
            Sign sign = (Sign) e.getClickedBlock().getState();
            if (sign.getLine(0).equalsIgnoreCase(ChatColor.translateAlternateColorCodes('&', config.getString("sign.1")))) {
                ArenaManager.getManager().addPlayer(player, ChatColor.stripColor(sign.getLine(1)));
            }
        }

        if (e.getAction() == Action.PHYSICAL) {
            if (e.getClickedBlock().getType() == Material.IRON_PLATE) {
                final Block block = e.getClickedBlock();
                Location location = block.getLocation();
                List<Entity> entities = location.getWorld().getEntities();
                int enemies = 0;

                for (Entity entity : entities) {
                    if (entity instanceof Player) {
                        Player target = (Player) entity;
                        if (target.getLocation().distance(location) <= 4) {
                            Player owner = null;
                            Mine mine = null;
                            for (Mine m : Game.mines) {
                                if (m.getLocation().equals(location)) {
                                    owner = m.getPlayer();
                                    mine = m;
                                }
                            }

                            if (owner != null) {
                                if (tm.getTeam(target) != tm.getTeam(owner)) {
                                    if (!tm.getInvincible().contains(target.getName())) {
                                        enemies++;
                                        effects(player, location);
                                        game.die(target);
                                        game.setKills(owner);
                                        game.setLives(target);
                                        game.setKillstreak(owner);
                                        target.setHealth(20);
                                        Utils.sendArenaActionBarMessage(ArenaManager.getManager().getArena(player), config.getString("messages.kill").replaceAll("<killer>", owner.getName()).replaceAll("<target>", target.getName()));

                                        board.updateArenaBoard(arena);
                                        if (mine != null) Game.mines.remove(mine);

                                        Bukkit.getScheduler().scheduleSyncDelayedTask(HyteriaLaserStrike.getInstance(), new Runnable() {
                                            @Override
                                            public void run() {
                                                block.setType(Material.AIR);
                                                block.getState().update();
                                            }
                                        }, 1L);
                                    }
                                }
                            }
                        }
                    }
                }

                if (enemies == 2) {
                    Utils.sendArenaMessage(arena, HyteriaLaserStrike.getPluginConfig().getString("messages.doubleKill").replaceAll("<player>", player.getName()));
                } else if (enemies == 3) {
                    Utils.sendArenaMessage(arena, HyteriaLaserStrike.getPluginConfig().getString("messages.tripleKill").replaceAll("<player>", player.getName()));
                }
            }
        }
    }

    private void effects(Player player, Location location) {
        player.getWorld().spigot().playEffect(location, Effect.EXPLOSION_LARGE, 0, 0, (float) 1, (float) 1, (float) 1, (float) 0.01, 1, 1);
        player.getWorld().spigot().playEffect(location, Effect.PORTAL, 0, 0, (float) 1, (float) 1, (float) 1, (float) 0.01, 50, 50);
        player.getWorld().spigot().playEffect(location, Effect.FLAME, 0, 0, (float) 1, (float) 1, (float) 1, (float) 0.05, 50, 50);
        player.getWorld().spigot().playEffect(location, Effect.CLOUD, 0, 0, (float) 1, (float) 1, (float) 1, (float) 0.01, 50, 50);
    }
}
