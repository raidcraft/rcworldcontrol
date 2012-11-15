package de.raidcraft.worldcontrol.listener;

import com.silthus.raidcraft.util.component.database.ComponentDatabase;
import de.raidcraft.worldcontrol.AllowedItem;
import de.raidcraft.worldcontrol.BlockLog;
import de.raidcraft.worldcontrol.WorldControlModule;
import de.raidcraft.worldcontrol.WorldGuardManager;
import de.raidcraft.worldcontrol.exceptions.*;
import de.raidcraft.worldcontrol.tables.BlockLogsTable;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.ExplosionPrimeEvent;

/**
 * Author: Philip
 * Date: 13.11.12 - 06:27
 * Description:
 */
public class BlockListener implements Listener {
    

    @EventHandler(
            ignoreCancelled = true,
            priority = EventPriority.HIGHEST
    )
    public void onBlockPlace(BlockPlaceEvent event) {
        if(event.getPlayer().getGameMode() == GameMode.CREATIVE && event.getPlayer().hasPermission("worldcontrol.build"))
            return;

        //check world
        if(!event.getPlayer().getLocation().getWorld().getName().equalsIgnoreCase(WorldControlModule.INSTANCE.config.world))
            return;

        //check if location is region
        String region = WorldGuardManager.INSTANCE.getLocatedRegion(event.getBlock().getLocation());
        if(region != null && !region.startsWith(WorldControlModule.INSTANCE.config.farmPrefix)) {
            return;
        }

        try {
            AllowedItem allowedItem = WorldControlModule.INSTANCE.getAllowedItem(event.getBlock());

            // check if can placed
            if(!allowedItem.canBlockPlace()) {
                throw new NotAllowedItemException();
            }

            // check if farm only
            if(region == null && allowedItem.isFarmOnly()) {
                throw new FarmOnlyException();
            }

            // check if deep enough
            if(allowedItem.getMaxPlaceHeight() > 0 && event.getBlock().getLocation().getBlockY() > allowedItem.getMaxPlaceHeight()) {
                throw new NotDeepEnoughException();
            }

            // check local place limit
            if(WorldControlModule.INSTANCE.isNearBlockPlaced(event.getBlock(), allowedItem)) {
                throw new LocalPlaceLimitReachedException();
            }

            WorldControlModule.INSTANCE.addBlockLog(new BlockLog(event.getPlayer().getName(), event.getBlock().getLocation(), null, event.getBlock()));
            return;
        }
        catch (NotAllowedItemException e) {
            event.getPlayer().sendMessage(ChatColor.RED + "Du kannst diesen Block hier nicht setzen!");
        }
        catch(NotDeepEnoughException e) {
            event.getPlayer().sendMessage(ChatColor.RED + e.getMessage());
        }
        catch(LocalPlaceLimitReachedException e) {
            event.getPlayer().sendMessage(ChatColor.RED + e.getMessage());
        }
        catch (FarmOnlyException e) {
            event.getPlayer().sendMessage(ChatColor.RED + "Du kannst diesen Block nur in Farmen setzen!");
        }

        event.setCancelled(true);
    }

    @EventHandler(
            ignoreCancelled = true,
            priority = EventPriority.HIGHEST
    )
    public void onBlockBreak(BlockBreakEvent event) {
        if(event.getPlayer().getGameMode() == GameMode.CREATIVE && event.getPlayer().hasPermission("worldcontrol.build"))
            return;

        //check world
        if(!event.getPlayer().getLocation().getWorld().getName().equalsIgnoreCase(WorldControlModule.INSTANCE.config.world))
            return;

        //check if location is region
        String region = WorldGuardManager.INSTANCE.getLocatedRegion(event.getBlock().getLocation());
        if(region != null && !region.startsWith(WorldControlModule.INSTANCE.config.farmPrefix)) {
            return;
        }

        try {
            AllowedItem allowedItem = WorldControlModule.INSTANCE.getAllowedItem(event.getBlock());

            // check if can placed
            if(!allowedItem.canBlockBreak()) {
                throw new NotAllowedItemException();
            }

            // check if farm only
            if(region == null && allowedItem.isFarmOnly()) {
                throw new FarmOnlyException();
            }

            WorldControlModule.INSTANCE.addBlockLog(new BlockLog(event.getPlayer().getName(), event.getBlock().getLocation(), event.getBlock(), null));
            if(!allowedItem.canDropItem()) {
                event.getBlock().setType(Material.AIR); // remove block but don't spawn an item
                throw new NoItemDropException();
            }
            return;
        }
        catch (NotAllowedItemException e) {
            event.getPlayer().sendMessage(ChatColor.RED + "Du kannst diesen Block hier nicht abbauen!");
        }
        catch (FarmOnlyException e) {
            event.getPlayer().sendMessage(ChatColor.RED + "Du kannst diesen Block nur in Farmen abbauen!");
        }
        catch(NoItemDropException e) {
            // no message
        }

        event.setCancelled(true);
    }

    @EventHandler(
            ignoreCancelled = true,
            priority = EventPriority.HIGHEST
    )
    public void onGravelSandMove(BlockPhysicsEvent event) {
        if(event.getBlock().getType() != Material.SAND && event.getBlock().getType() != Material.GRAVEL) {
            return;
        }

        //check if location is region
        String region = WorldGuardManager.INSTANCE.getLocatedRegion(event.getBlock().getLocation());
        if(region == null || !region.startsWith(WorldControlModule.INSTANCE.config.farmPrefix)) {
            return;
        }
        WorldControlModule.INSTANCE.addBlockLog(new BlockLog("PHYSIC", event.getBlock().getLocation(), event.getBlock(), null));
    }

    @EventHandler(
            ignoreCancelled = true,
            priority = EventPriority.HIGHEST
    )
    public void onExplosion(EntityExplodeEvent event) {

        //check if location is region
        String region = WorldGuardManager.INSTANCE.getLocatedRegion(event.getLocation());
        if(region != null && !region.startsWith(WorldControlModule.INSTANCE.config.farmPrefix)) {
            return;
        }

        for(Block block : event.blockList()) {
            WorldControlModule.INSTANCE.addBlockLog(new BlockLog("EXPLOSION", block.getLocation(), block, null));
        }
    }

    @EventHandler(
            ignoreCancelled = true,
            priority = EventPriority.HIGHEST
    )
    public void onEntityChangeBlock(EntityChangeBlockEvent event) {

        //check if location is region
        String region = WorldGuardManager.INSTANCE.getLocatedRegion(event.getBlock().getLocation());
        if(region != null && !region.startsWith(WorldControlModule.INSTANCE.config.farmPrefix)) {
            return;
        }

        WorldControlModule.INSTANCE.addBlockLog(new BlockLog("ENTITY", event.getBlock().getLocation(), event.getBlock(), null));
    }

    @EventHandler(
            ignoreCancelled = true,
            priority = EventPriority.HIGHEST
    )
    public void onBlockDecay(LeavesDecayEvent event) {

        //check if location is region
        String region = WorldGuardManager.INSTANCE.getLocatedRegion(event.getBlock().getLocation());
        if(region != null && !region.startsWith(WorldControlModule.INSTANCE.config.farmPrefix)) {
            return;
        }

        WorldControlModule.INSTANCE.addBlockLog(new BlockLog("LEAVES", event.getBlock().getLocation(), event.getBlock(), null));
    }
}
