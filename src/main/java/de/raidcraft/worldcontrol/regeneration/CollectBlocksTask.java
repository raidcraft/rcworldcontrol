package de.raidcraft.worldcontrol.regeneration;

import de.raidcraft.RaidCraft;
import de.raidcraft.util.DateUtil;
import de.raidcraft.worldcontrol.BlockLog;
import de.raidcraft.worldcontrol.WorldControlPlugin;
import de.raidcraft.worldcontrol.alloweditem.AllowedItem;
import de.raidcraft.worldcontrol.alloweditem.AllowedItemManager;
import de.raidcraft.worldcontrol.tables.BlockLogsTable;
import org.bukkit.Location;
import org.bukkit.Material;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Philip Urban
 */
public class CollectBlocksTask implements Runnable {

    private RegenerationTask regenerationTask;
    private Map<Location, BlockLog> allSavedLogs = new HashMap<>();

    public CollectBlocksTask(RegenerationTask regenerationTask) {

        this.regenerationTask = regenerationTask;
    }

    @Override
    public void run() {

        RaidCraft.getTable(BlockLogsTable.class).otimizeTable();

        List<BlockLog> allLogs = RaidCraft.getTable(BlockLogsTable.class).getAllLogs(regenerationTask.getWorld());
        for (BlockLog log : allLogs) {
            allSavedLogs.put(log.getLocation(), log);
        }

        for (Map.Entry<Location, BlockLog> entry : allSavedLogs.entrySet()) {
            BlockLog log = entry.getValue();
            // skips already processed blocks
            if (log == null) {
                continue;
            }

            AllowedItem allowedItem;
            double rnd = Math.random() * (RaidCraft.getComponent(WorldControlPlugin.class).config.timeFactor / 100);

            // check breaked blocks
            allowedItem = AllowedItemManager.INST.getAllowedItems().get(log.getBlockBeforeMaterial());
            if (allowedItem != null || regenerationTask.isForced()) {
                if (regenerationTask.isForced() || DateUtil.getTimeStamp(log.getTime()) / 1000 + allowedItem.getRegenerationTime() + (allowedItem.getRegenerationTime() * rnd) < System.currentTimeMillis() / 1000) {
                    regenerateRecursive(log.getLocation());
                    continue; // skip because log is already added
                }
            }

            // check placed blocks
            if(log.getBlockBeforeMaterial() == Material.AIR) {
                allowedItem = AllowedItemManager.INST.getAllowedItems().get(log.getBlockAfterMaterial());
                if (allowedItem != null || regenerationTask.isForced()) {
                    if (regenerationTask.isForced() || DateUtil.getTimeStamp(log.getTime()) / 1000 + allowedItem.getRegenerationTime() + (allowedItem.getRegenerationTime() * rnd) < System.currentTimeMillis() / 1000) {
                        regenerateRecursive(log.getLocation());
                        continue; // skip because log is already added
                    }
                }
            }
        }

        regenerationTask.setCollectingState(CollectingState.FINISHED);
    }

    private void regenerateRecursive(Location blockLocation) {

        if (!allSavedLogs.containsKey(blockLocation)
                || allSavedLogs.get(blockLocation) == null) {
            return;
        }

        regenerationTask.getBlocksToRestore().add(allSavedLogs.get(blockLocation));
        //        regenerateBlockLog(allSavedLogs.get(blockLocation));
        allSavedLogs.put(blockLocation, null);

        //        if(restored >= informCnt * 100) {
        //            informCnt++;
        //            RaidCraft.LOGGER.info("[WC] " + restored + " Bloecke wurden bereits regeneriert.");
        //        }

        regenerateRecursive(blockLocation.add(0, 1, 0));
        regenerateRecursive(blockLocation.add(1, 0, 0));
        regenerateRecursive(blockLocation.add(0, 0, 1));
        regenerateRecursive(blockLocation.add(-1, 0, 0));
        regenerateRecursive(blockLocation.add(0, 0, -1));
        regenerateRecursive(blockLocation.add(0, -1, 0));
    }
}
