package de.raidcraft.worldcontrol.listener;

import com.silthus.raidcraft.util.RCMessaging;
import de.raidcraft.worldcontrol.WorldControlModule;
import de.raidcraft.worldcontrol.WorldGuardManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import sun.invoke.empty.Empty;

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
        if(!event.getPlayer().getLocation().getWorld().getName().equalsIgnoreCase(WorldControlModule.INSTANCE.config.world))
            return;

        //check if location is region
        if(WorldGuardManager.INSTANCE.inRegion(event.getPlayer())) {
            return;
        }

        //prevent lava and water placement
        event.setCancelled(true);
        event.getPlayer().sendMessage(ChatColor.RED + "Du kannst hier nichts ausschütten!");
    }
}
