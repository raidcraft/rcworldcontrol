package de.raidcraft.worldcontrol.listener;

import de.raidcraft.RaidCraft;
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

        if (event.getPlayer().hasPermission("worldcontrol.build"))
            return;

        //check world
        if (!event.getPlayer().getLocation().getWorld().getName().equalsIgnoreCase(RaidCraft.getComponent(WorldControlPlugin.class).config.world))
            return;

        //check if location is region
        if(WorldGuardManager.INST.isInUnknownRegion(event.getPlayer().getLocation())) {
            return;
        }

        //prevent lava and water placement
        event.setCancelled(true);
        event.getPlayer().sendMessage(ChatColor.RED + "Du kannst hier nichts ausschütten!");
    }
}
