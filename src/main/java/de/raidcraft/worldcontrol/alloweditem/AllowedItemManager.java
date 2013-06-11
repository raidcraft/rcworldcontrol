package de.raidcraft.worldcontrol.alloweditem;

import de.raidcraft.RaidCraft;
import de.raidcraft.worldcontrol.BlockLog;
import de.raidcraft.worldcontrol.LogSaver;
import de.raidcraft.worldcontrol.tables.AllowedItemsTable;
import de.raidcraft.worldcontrol.tables.BlockLogsTable;
import org.bukkit.Material;
import org.bukkit.block.Block;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Philip
 */
public class AllowedItemManager {

    public final static AllowedItemManager INST = new AllowedItemManager();

    private Map<Material, AllowedItem> allowedItems = new HashMap<>();

    public void reload() {

        allowedItems.clear();
        for (AllowedItem item : RaidCraft.getTable(AllowedItemsTable.class).getAllowedItems()) {
            allowedItems.put(item.getMaterial(), item);
        }
    }


    public AllowedItem getAllowedItem(Block block) {

        if (allowedItems.containsKey(block.getType())) {
            return allowedItems.get(block.getType());
        }
        return null;
    }

    public Map<Material, AllowedItem> getAllowedItems() {

        return allowedItems;
    }

    public boolean isNearBlockPlaced(Block block, AllowedItem item) {

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
