package de.raidcraft.worldcontrol.listener;

import de.raidcraft.worldcontrol.AllowedItem;
import de.raidcraft.worldcontrol.BlockLog;
import de.raidcraft.worldcontrol.WorldControlModule;
import de.raidcraft.worldcontrol.WorldGuardManager;
import de.raidcraft.worldcontrol.exceptions.LocalPlaceLimitReachedException;
import de.raidcraft.worldcontrol.exceptions.NoItemDropException;
import de.raidcraft.worldcontrol.exceptions.UnknownAllowedItemException;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockEvent;
import org.bukkit.event.block.BlockPlaceEvent;

/**
 * Author: Philip
 * Date: 13.11.12 - 06:27
 * Description:
 */
public class BlockListener implements Listener {

    @EventHandler( ignoreCancelled = true )
    public void onBlockPlace(BlockPlaceEvent event) {
        if(event.getPlayer().hasPermission("worldcontrol.build"))
            return;

        //check world
        if(!event.getPlayer().getLocation().getWorld().getName().equalsIgnoreCase(WorldControlModule.INSTANCE.config.world))
            return;

        //check if location is region
        if(WorldGuardManager.INSTANCE.inRegion(event.getPlayer())) {
            return;
        }

        try {
            AllowedItem allowedItem = WorldControlModule.INSTANCE.getAllowedItem(event.getBlock());

            if(!WorldControlModule.INSTANCE.canPlace(event.getPlayer(), allowedItem)) {
                throw new LocalPlaceLimitReachedException();
            }
            WorldControlModule.INSTANCE.addBlockLog(new BlockLog(event.getPlayer(), event.getBlock().getLocation(), null, event.getBlock()));
            return;
        }
        catch (UnknownAllowedItemException e) {
            event.getPlayer().sendMessage(ChatColor.RED + "Du kannst diesen Block hier nicht setzen!");
        }
        catch(LocalPlaceLimitReachedException e) {
            event.getPlayer().sendMessage(ChatColor.RED + e.getMessage());
        }

        event.setCancelled(true);
    }

    @EventHandler( ignoreCancelled = true )
    public void onBlockBreak(BlockBreakEvent event) {
        if(event.getPlayer().hasPermission("worldcontrol.build"))
            return;

        //check world
        if(!event.getPlayer().getLocation().getWorld().getName().equalsIgnoreCase(WorldControlModule.INSTANCE.config.world))
            return;

        //check if location is region
        if(WorldGuardManager.INSTANCE.inRegion(event.getPlayer())) {
            return;
        }

        try {
            AllowedItem allowedItem = WorldControlModule.INSTANCE.getAllowedItem(event.getBlock());

            if(!WorldControlModule.INSTANCE.canBreak(event.getPlayer(), allowedItem)) {
                throw new UnknownAllowedItemException();
            }
            WorldControlModule.INSTANCE.addBlockLog(new BlockLog(event.getPlayer(), event.getBlock().getLocation(), event.getBlock(), null));
            if(!allowedItem.canDropItem()) {
                event.getBlock().setType(Material.AIR); // remove block but don't spawn an item
                throw new NoItemDropException();
            }
            return;
        }
        catch (UnknownAllowedItemException e) {
            event.getPlayer().sendMessage(ChatColor.RED + "Du kannst diesen Block hier nicht abbauen!");
        }
        catch(NoItemDropException e) {
            // no message
        }

        event.setCancelled(true);
    }
}
