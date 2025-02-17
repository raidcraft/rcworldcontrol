package de.raidcraft.worldcontrol.listener;

import de.raidcraft.RaidCraft;
import de.raidcraft.worldcontrol.WorldControlPlugin;
import de.raidcraft.worldcontrol.util.WorldGuardManager;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;
import org.bukkit.inventory.ItemStack;

/**
 * Author: Philip
 * Date: 13.11.12 - 06:28
 * Description:
 */
public class PlayerListener implements Listener {

    @EventHandler(ignoreCancelled = true)
    public void onPlayerEmptyBucket(PlayerBucketEmptyEvent event) {

        Player player = event.getPlayer();

        if (player.getGameMode() == GameMode.CREATIVE)
            return;

        //check world
        if (!player.getLocation().getWorld().getName().equalsIgnoreCase(RaidCraft.getComponent(WorldControlPlugin.class).config.world))
            return;

        //check if location is region
        if(WorldGuardManager.INST.isInUnknownRegion(player.getLocation())) {
            return;
        }

        //prevent lava and water placement
        event.setCancelled(true);

        if(player.getFireTicks() > 0 && (player.getItemInHand().getType() == Material.WATER_BUCKET)) {
            player.setFireTicks(0);
            player.setItemInHand(new ItemStack(Material.BUCKET));
            player.sendMessage(ChatColor.GREEN + "Du hast dich gelöscht!");
            return;
        }
        player.sendMessage(ChatColor.RED + "Du kannst hier nichts ausschütten!");
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerFillBucket(PlayerBucketFillEvent event) {

        if (event.getPlayer().getGameMode() == GameMode.CREATIVE)
            return;

        //check world
        if (!event.getPlayer().getLocation().getWorld().getName().equalsIgnoreCase(RaidCraft.getComponent(WorldControlPlugin.class).config.world))
            return;

        //check if location is region
        if(WorldGuardManager.INST.isInUnknownRegion(event.getPlayer().getLocation())) {
            return;
        }

        event.setCancelled(true);
        if(event.getBlockClicked().getType() == Material.STATIONARY_WATER || event.getBlockClicked().getType() == Material.WATER) {
            if(event.getItemStack().getAmount() > 1) {
                int firstEmpty = event.getPlayer().getInventory().firstEmpty();
                if(firstEmpty == -1) return;
                event.getPlayer().getInventory().setItem(firstEmpty, new ItemStack(Material.BUCKET, event.getItemStack().getAmount() - 1));
            }
            event.getPlayer().setItemInHand(new ItemStack(Material.WATER_BUCKET));
        }
        if(event.getBlockClicked().getType() == Material.STATIONARY_LAVA || event.getBlockClicked().getType() == Material.LAVA) {
            if(event.getItemStack().getAmount() > 1) {
                int firstEmpty = event.getPlayer().getInventory().firstEmpty();
                if(firstEmpty == -1) return;
                event.getPlayer().getInventory().setItem(firstEmpty, new ItemStack(Material.BUCKET, event.getItemStack().getAmount() - 1));
            }
            event.getPlayer().setItemInHand(new ItemStack(Material.LAVA_BUCKET));
        }
        event.getPlayer().updateInventory();
    }
}
