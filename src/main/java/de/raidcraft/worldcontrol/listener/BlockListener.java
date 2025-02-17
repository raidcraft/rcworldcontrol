package de.raidcraft.worldcontrol.listener;

import de.raidcraft.RaidCraft;
import de.raidcraft.worldcontrol.BlockLog;
import de.raidcraft.worldcontrol.LogSaver;
import de.raidcraft.worldcontrol.WorldControlPlugin;
import de.raidcraft.worldcontrol.restricteditem.RestrictedItem;
import de.raidcraft.worldcontrol.util.WorldGuardManager;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.LeavesDecayEvent;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityExplodeEvent;

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

        if (event.getPlayer().getGameMode() == GameMode.CREATIVE)
            return;

//        if (event.getPlayer().hasPermission("worldcontrol.block.place." + event.getBlock().getTypeId())) {
//            return;
//        }

        //check world
        if (!event.getPlayer().getLocation().getWorld().getName().equalsIgnoreCase(RaidCraft.getComponent(WorldControlPlugin.class).config.world))
            return;

        if (LogSaver.INST.isBlocked() || !RaidCraft.getComponent(WorldControlPlugin.class).allowPhysics) {
            sendInteractSuppressWarning(event.getPlayer());
            event.setCancelled(true);
            return;
        }

        //check if location is region
        if(WorldGuardManager.INST.isInUnknownRegion(event.getBlock().getLocation())) {
            return;
        }

        RestrictedItem restrictedItem = RaidCraft.getComponent(WorldControlPlugin.class).getRestrictedItemManager().getRestrictedItem(event.getBlock());
        if(restrictedItem == null) {
            event.setCancelled(true);
            return;
        }

        // check if can placed
        if (!restrictedItem.canBlockPlace()) {
            event.setCancelled(true);
            return;
        }

        // check if farm only
        if (!WorldGuardManager.INST.isFarm(event.getBlock().getLocation()) && restrictedItem.isFarmOnly()) {
            event.getPlayer().sendMessage(ChatColor.RED + "Du kannst diesen Block nur in Farmen setzen!");
            event.setCancelled(true);
            return;
        }

        // check if deep enough
        if (restrictedItem.getMaxPlaceHeight() > 0 && event.getBlock().getLocation().getBlockY() > restrictedItem.getMaxPlaceHeight()) {
            event.getPlayer().sendMessage(ChatColor.RED + "Dieser Block kann nur weiter unten gesetzt werden!");
            event.setCancelled(true);
            return;
        }

        // check local place limit
        if (RaidCraft.getComponent(WorldControlPlugin.class).getRestrictedItemManager().isNearBlockPlaced(event.getBlock(), restrictedItem)) {
            event.getPlayer().sendMessage(ChatColor.RED + "Dieser Block wurde hier in der Gegend schon zu oft gesetzt!");
            event.setCancelled(true);
            return;
        }

        // check if farm
        if(!WorldGuardManager.INST.isFarm(event.getBlock().getLocation())) {
            LogSaver.INST.addBlockLog(new BlockLog(event.getPlayer().getName(), event.getBlock().getLocation(), null, event.getBlock()));
        }
    }

    @EventHandler(
            ignoreCancelled = true,
            priority = EventPriority.HIGHEST
    )
    public void onBlockBreak(BlockBreakEvent event) {

        if (event.getPlayer().getGameMode() == GameMode.CREATIVE)
            return;

//        if (event.getPlayer().hasPermission("worldcontrol.block.break." + event.getBlock().getTypeId())) {
//            return;
//        }
        //check world
        if (!event.getPlayer().getLocation().getWorld().getName().equalsIgnoreCase(RaidCraft.getComponent(WorldControlPlugin.class).config.world))
            return;

        if (LogSaver.INST.isBlocked() || !RaidCraft.getComponent(WorldControlPlugin.class).allowPhysics) {
            sendInteractSuppressWarning(event.getPlayer());
            event.setCancelled(true);
            return;
        }

        // check if farm
        if(WorldGuardManager.INST.isFarm(event.getBlock().getLocation())) {
            return;
        }

        //check if location is region
        if(WorldGuardManager.INST.isInUnknownRegion(event.getBlock().getLocation())) {
            return;
        }

        RestrictedItem restrictedItem = RaidCraft.getComponent(WorldControlPlugin.class).getRestrictedItemManager().getRestrictedItem(event.getBlock());
        if(restrictedItem == null) {
            event.setCancelled(true);
            return;
        }

        // check if can break
        if (!restrictedItem.canBlockBreak()) {
            event.setCancelled(true);
            return;
        }

        // check if farm only
        if (!WorldGuardManager.INST.isFarm(event.getBlock().getLocation()) && restrictedItem.isFarmOnly()) {
            event.getPlayer().sendMessage(ChatColor.RED + "Du kannst diesen Block nur in Farmen abbauen!");
            event.setCancelled(true);
            return;
        }

        if(!LogSaver.INST.removeLog(event.getBlock().getLocation(), restrictedItem)) {
            LogSaver.INST.addBlockLog(new BlockLog(event.getPlayer().getName(), event.getBlock().getLocation(), event.getBlock(), null));
        }

        if (!restrictedItem.canDropItem()) {
            event.getBlock().setType(Material.AIR); // remove block but don't spawn an item
            event.setCancelled(true);
        }
    }

//    @EventHandler(
//            ignoreCancelled = true,
//            priority = EventPriority.HIGHEST
//    )
//    public void onGravelSandMove(BlockPhysicsEvent event) {
//        //check world
//        if (!event.getBlock().getLocation().getWorld().getName().equalsIgnoreCase(RaidCraft.getComponent(WorldControlPlugin.class).config.world))
//            return;
//
//        if (LogSaver.INST.isBlocked() || !RaidCraft.getComponent(WorldControlPlugin.class).allowPhysics) {
//            event.setCancelled(true);
//            return;
//        }
//
//        if (event.getBlock().getType() != Material.SAND && event.getBlock().getType() != Material.GRAVEL) {
//            return;
//        }
//
//        //check if location is region
//        if(WorldGuardManager.INST.isInUnknownRegion(event.getBlock().getLocation())) {
//            return;
//        }
//
//        LogSaver.INST.addBlockLog(new BlockLog("Physics", event.getBlock().getLocation(), event.getBlock(), null));
//    }

    @EventHandler(
            ignoreCancelled = true,
            priority = EventPriority.HIGHEST
    )
    public void onExplosion(EntityExplodeEvent event) {

        //check world
        if (!event.getLocation().getWorld().getName().equalsIgnoreCase(RaidCraft.getComponent(WorldControlPlugin.class).config.world))
            return;

        if (LogSaver.INST.isBlocked() || !RaidCraft.getComponent(WorldControlPlugin.class).allowPhysics) {
            event.setCancelled(true);
            return;
        }

        // check if farm
        if(WorldGuardManager.INST.isFarm(event.getLocation())) {
            return;
        }

        //check if location is region
        if(WorldGuardManager.INST.isInUnknownRegion(event.getLocation())) {
            return;
        }

        for (Block block : event.blockList()) {
            LogSaver.INST.addBlockLog(new BlockLog(event.getEntityType().name(), block.getLocation(), block, null));
        }
    }

    @EventHandler(
            ignoreCancelled = true,
            priority = EventPriority.HIGHEST
    )
    public void onEntityChangeBlock(EntityChangeBlockEvent event) {

        if(event.getEntityType() == EntityType.FALLING_BLOCK) {
            return;
        }

        //check world
        if (!event.getBlock().getLocation().getWorld().getName().equalsIgnoreCase(RaidCraft.getComponent(WorldControlPlugin.class).config.world))
            return;

        if (LogSaver.INST.isBlocked() || !RaidCraft.getComponent(WorldControlPlugin.class).allowPhysics) {
            event.setCancelled(true);
            return;
        }

        // check if farm
        if(WorldGuardManager.INST.isFarm(event.getBlock().getLocation())) {
            return;
        }

        //check if location is region
        if(WorldGuardManager.INST.isInUnknownRegion(event.getBlock().getLocation())) {
            return;
        }

        LogSaver.INST.addBlockLog(new BlockLog(event.getEntity().getType().name(), event.getBlock().getLocation(), event.getBlock(), null));
    }

    @EventHandler(
            ignoreCancelled = true,
            priority = EventPriority.HIGHEST
    )
    public void onBlockDecay(LeavesDecayEvent event) {

        //check world
        if (!event.getBlock().getLocation().getWorld().getName().equalsIgnoreCase(RaidCraft.getComponent(WorldControlPlugin.class).config.world))
            return;

        if (LogSaver.INST.isBlocked() || !RaidCraft.getComponent(WorldControlPlugin.class).allowPhysics) {
            event.setCancelled(true);
            return;
        }

        // check if farm
        if(WorldGuardManager.INST.isFarm(event.getBlock().getLocation())) {
            return;
        }

        //check if location is region
        if(WorldGuardManager.INST.isInUnknownRegion(event.getBlock().getLocation())) {
            return;
        }

        LogSaver.INST.addBlockLog(new BlockLog("Leaves", event.getBlock().getLocation(), event.getBlock(), null));
    }

    private void sendInteractSuppressWarning(Player player) {

        player.sendMessage(ChatColor.RED + "Dieses Gebiet regeneriert sich gerade!");
        player.sendMessage(ChatColor.RED + "Interaktionen sind für wenige Sekunden unterbunden.");
    }
}
