package de.raidcraft.worldcontrol.listener;

import de.raidcraft.worldcontrol.WorldControlPlugin;
import de.raidcraft.worldcontrol.WorldGuardManager;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerBucketEmptyEvent;

/**
 * Author: Philip
 * Date: 13.11.12 - 06:28
 * Description:
 */
public class PlayerListener implements Listener {

    @EventHandler(ignoreCancelled = true)
    public void onPlayerEmptyBucket(PlayerBucketEmptyEvent event) {
        if(event.getPlayer().hasPermission("worldcontrol.build"))
            return;

        //check world
        if(!event.getPlayer().getLocation().getWorld().getName().equalsIgnoreCase(WorldControlPlugin.INST.config.world))
            return;

        //check if location is region
        String region = WorldGuardManager.INSTANCE.getLocatedRegion(event.getPlayer().getLocation());
        if(region != null && !region.startsWith(WorldControlPlugin.INST.config.farmPrefix)) {
            return;
        }

        //prevent lava and water placement
        event.setCancelled(true);
        event.getPlayer().sendMessage(ChatColor.RED + "Du kannst hier nichts ausschütten!");
    }
}
