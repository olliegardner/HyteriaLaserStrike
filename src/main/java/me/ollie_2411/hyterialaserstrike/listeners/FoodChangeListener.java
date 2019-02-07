package me.ollie_2411.hyterialaserstrike.listeners;

import me.ollie_2411.hyterialaserstrike.HyteriaLaserStrike;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.FoodLevelChangeEvent;

/**
 * Created by Ollie on 05/03/2016.
 */
public class FoodChangeListener implements Listener {

    public FoodChangeListener(HyteriaLaserStrike instance) {
        instance.getServer().getPluginManager().registerEvents(this, instance);
    }

    @EventHandler
    public void onFoodLevelChange(FoodLevelChangeEvent e) {
        e.setCancelled(true);
    }
}
