package de.raidcraft.worldcontrol.restricteditem;

import de.raidcraft.RaidCraft;
import de.raidcraft.worldcontrol.BlockLog;
import de.raidcraft.worldcontrol.LogSaver;
import de.raidcraft.worldcontrol.WorldControlPlugin;
import de.raidcraft.worldcontrol.tables.RestrictedItemsTable;
import de.raidcraft.worldcontrol.tables.BlockLogsTable;
import org.bukkit.Material;
import org.bukkit.block.Block;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Philip
 */
public class RestrictedItemManager {

    private WorldControlPlugin plugin;
    private Map<Material, RestrictedItem> restrictedItems = new HashMap<>();

    public RestrictedItemManager(WorldControlPlugin plugin) {

        this.plugin = plugin;
    }

    public void reload() {

        restrictedItems.clear();
        for (RestrictedItem item : RaidCraft.getTable(RestrictedItemsTable.class).getAllRestrictedItems()) {
            restrictedItems.put(item.getMaterial(), item);
        }
    }


    public RestrictedItem getRestrictedItem(Block block) {

        if (restrictedItems.containsKey(block.getType())) {
            return restrictedItems.get(block.getType());
        }
        return null;
    }

    public Map<Material, RestrictedItem> getRestrictedItems() {

        return restrictedItems;
    }

    public boolean isNearBlockPlaced(Block block, RestrictedItem item) {

        for (BlockLog log : LogSaver.INST.getLogs()) {
            if (log.getBlockAfterMaterial() == item.getMaterial()) {
                if (log.getLocation().distance(block.getLocation()) < item.getLocalPlaceDistance()) {
                    return true;
                }
            }
        }
        return RaidCraft.getTable(BlockLogsTable.class).isNearBlockPlaced(block, item);
    }

}
