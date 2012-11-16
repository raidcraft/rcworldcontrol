package de.raidcraft.worldcontrol;

import com.silthus.raidcraft.util.component.DateUtil;
import com.silthus.raidcraft.util.component.database.ComponentDatabase;
import com.sk89q.commandbook.CommandBook;
import de.raidcraft.worldcontrol.tables.BlockLogsTable;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Author: Philip
 * Date: 15.11.12 - 20:14
 * Description:
 */
public class Regeneration {
    public final static Regeneration INSTANCE = new Regeneration();

    private boolean regenerationRunning = false;
    private boolean regenerateAll = false;
    Map<Location, BlockLog> allSavedLogs = new HashMap<>();

    public void regenerateBlocks(boolean all) {
        if(!regenerationRunning) {
            regenerateAll = true;
            regenerateBlocks();
        }
    }

    public void regenerateBlocks() {
        if(regenerationRunning) {
            return;
        }

        regenerationRunning = true;
        allSavedLogs.clear();

        CommandBook.inst().getServer().getScheduler().scheduleAsyncDelayedTask(CommandBook.inst(), new Runnable() {
            public void run() {
                int i = 0;
                int informCnt = 1;
                for(BlockLog log : ComponentDatabase.INSTANCE.getTable(BlockLogsTable.class).getAllLogs()) {
                    allSavedLogs.put(log.getLocation(), log);
                }

                for(Map.Entry<Location, BlockLog> entry : allSavedLogs.entrySet()) {
                    BlockLog log = entry.getValue();
                    // skips already processed blocks
                    if(log == null) {
                        continue;
                    }
                    AllowedItem allowedItem = WorldControlModule.INSTANCE.getAllowedItems().get(log.getBlockBeforeMaterial());

                    double rnd = Math.random() * (WorldControlModule.INSTANCE.config.timeFactor/100);
                    if(regenerateAll || allowedItem == null
                            || DateUtil.getTimeStamp(log.getTime()) + allowedItem.getRegenerationTime() + (allowedItem.getRegenerationTime() * rnd) < System.currentTimeMillis() / 1000) {
                        regenerateRecursive(log.getLocation());
                        i++;
                    }

                    if(i >= informCnt * 100) {
                        informCnt++;
                        CommandBook.logger().info("[WC] " + i + " Bloecke wurden bereits regeneriert.");
                    }
                }
                ComponentDatabase.INSTANCE.getTable(BlockLogsTable.class).deleteAll();
                CommandBook.logger().info("[WC] Regenerierung fertig. Es wurden insgesamt " + i + " Bloecke regeneriert!");
                regenerationRunning = false;
                regenerateAll = false;
            }
        }, 0);
    }

    private void regenerateRecursive(Location blockLocation) {
        if(!allSavedLogs.containsKey(blockLocation) || allSavedLogs.get(blockLocation) == null) {
            return;
        }

        regenerateBlockLog(allSavedLogs.get(blockLocation));
        allSavedLogs.put(blockLocation, null);

        regenerateRecursive(blockLocation.add( 0,  1,  0));
        regenerateRecursive(blockLocation.add( 1,  0,  0));
        regenerateRecursive(blockLocation.add( 0,  0,  1));
        regenerateRecursive(blockLocation.add(-1,  0,  0));
        regenerateRecursive(blockLocation.add( 0,  0, -1));
        regenerateRecursive(blockLocation.add( 0, -1,  0));
    }

    public void regenerateBlockLog(BlockLog log) {
        log.getLocation().getBlock().setType(log.getBlockBeforeMaterial());
        log.getLocation().getBlock().setData((byte)log.getBlockBeforeData(), true);
    }

    public boolean isRegenerationRunning() {

        return regenerationRunning;
    }
}
