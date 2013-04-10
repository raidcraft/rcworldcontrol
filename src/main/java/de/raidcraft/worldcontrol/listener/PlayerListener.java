package de.raidcraft.worldcontrol.listener;

import de.raidcraft.RaidCraft;
import de.raidcraft.worldcontrol.WorldControlPlugin;
import de.raidcraft.worldcontrol.util.WorldGuardManager;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerInteractEvent;

/**
 * Author: Philip
 * Date: 13.11.12 - 06:28
 * Description:
 */
public class PlayerListener implements Listener {

    @EventHandler(ignoreCancelled = true)
    public void onPlayerEmptyBucket(PlayerBucketEmptyEvent event) {

        if (event.getPlayer().getGameMode() == GameMode.CREATIVE)
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

    @EventHandler(ignoreCancelled = true)
    public void onPlayerInteract(PlayerInteractEvent event) {

        if(event.getAction() != Action.LEFT_CLICK_BLOCK) {
            return;
        }

        if(event.getPlayer().getItemInHand().getType() != Material.BUCKET) {
            return;
        }

        if(event.getClickedBlock() == null || (event.getClickedBlock().getType() != Material.WATER && event.getClickedBlock().getType() != Material.LAVA)) {
            return;
        }

        event.getPlayer().sendMessage(ChatColor.RED + "Du kannst hier dein Eimer nicht füllen!");
        event.setCancelled(true);
    }
}
