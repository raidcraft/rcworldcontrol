package de.raidcraft.worldcontrol.listener;

import com.silthus.raidcraft.config.MainConfig;
import com.silthus.raidcraft.util.RCMessaging;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import de.raidcraft.worldcontrol.WorldControlModule;
import de.raidcraft.worldcontrol.WorldGuardManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;

/**
 * Author: Philip
 * Date: 13.11.12 - 06:27
 * Description:
 */
public class BlockListener implements Listener {

    @EventHandler( ignoreCancelled = true )
    public void onBlockPlace(BlockPlaceEvent event)
    {
        if(event.getPlayer().hasPermission("worldcontrol.build"))
            return;

        //check world
        if(!event.getPlayer().getLocation().getWorld().getName().equalsIgnoreCase(WorldControlModule.INSTANCE.config.world))
            return;

        //check if location is region
        if(WorldGuardManager.INSTANCE.inRegion(event.getPlayer())) {
            return;
        }


    }

}
