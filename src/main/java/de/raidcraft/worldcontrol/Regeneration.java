package de.raidcraft.worldcontrol;

import com.sk89q.commandbook.CommandBook;
import de.raidcraft.RaidCraft;
import de.raidcraft.util.DateUtil;
import de.raidcraft.worldcontrol.tables.BlockLogsTable;
import de.raidcraft.worldcontrol.util.WCLogger;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
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
    private int restored = 0;
    private Map<Location, BlockLog> allSavedLogs = new HashMap<>();
    private List<BlockLog> blocksToRestore = new ArrayList<>();
    private int restoreTaskId = 0;

    public void startRestoreTask() {
        restoreTaskId = Bukkit.getScheduler().scheduleSyncRepeatingTask(CommandBook.inst(), new Runnable() {
            public void run() {
                if(blocksToRestore.size() == 0) {
                    WCLogger.info("No blocks to regenerate!");
                    stopRestoreTask();
                    return;
                }

                WCLogger.info("Regenerate " + blocksToRestore.size() + " blocks...");
                LogSaver.INSTANCE.setBlocked(true);
                for (BlockLog log : blocksToRestore) {
                    regenerateBlockLog(log);
                }
                // activate physics and logs after 5 seconds
                Bukkit.getScheduler().scheduleSyncDelayedTask(CommandBook.inst(), new Runnable() {
                    public void run() {

                        LogSaver.INSTANCE.setBlocked(false);
                    }
                }, 5 * 20);
                WCLogger.info("Regeneration finished!");
                WCLogger.info("Start database cleanup...");

                Bukkit.getScheduler().scheduleAsyncDelayedTask(CommandBook.inst(), new Runnable() {
                    public void run() {

                        final Connection connection = RaidCraft.getTable(BlockLogsTable.class).getConnection();
                        PreparedStatement statement = null;

                        String updateQuery = "DELETE FROM `" + RaidCraft.getTable(BlockLogsTable.class).getTableName() +
                                "` WHERE id = ?";

                        try {
                            connection.setAutoCommit(false);
                            statement = connection.prepareStatement(updateQuery);

                            int i = 1;
//                            WCLogger.info("Try to delete " + blocksToRestore.size() + " rows...");
                            for (BlockLog log : blocksToRestore) {
                                statement.setInt(1, log.getId());
                                statement.executeUpdate();
                                i++;

                                if(i % 100 == 0) {
//                                    WCLogger.info("Already deleted " + i + " rows!");
                                    connection.commit();
                                }
                            }

                            connection.commit();
                            connection.setAutoCommit(true);
                        } catch (final SQLException ex) {
                            RaidCraft.LOGGER.warning("[WC] SQL exception: " + ex.getMessage());
                        } finally {
                            try {
                                connection.setAutoCommit(true);
                            } catch (SQLException e) {
                                e.printStackTrace();
                            }
                            WCLogger.info("Finished database cleanup!");
                            blocksToRestore.clear();
                        }

                    }
                }, 0);

                stopRestoreTask();
            }
        }, 0, 10*20);
    }

    public void stopRestoreTask() {
        Bukkit.getScheduler().cancelTask(restoreTaskId);
    }

    public void regenerateBlocks(boolean all) {
        if(all) {
            regenerateAll = true;
        }
        regenerateBlocks();
    }

    public void regenerateBlocks() {
        if(!canRegenerate()) {
            return;
        }

        regenerationRunning = true;
        allSavedLogs.clear();
        stopRestoreTask();

//        WCLogger.info("Clean log table...");
        RaidCraft.getTable(BlockLogsTable.class).cleanTable();
//        WCLogger.info("Finished table cleanup!");

        WCLogger.info("Collect blocks for regeneration...");

        Bukkit.getScheduler().scheduleAsyncDelayedTask(CommandBook.inst(), new Runnable() {
            public void run() {
                
                restored = 0;

                List<BlockLog> allLogs = RaidCraft.getTable(BlockLogsTable.class).getAllLogs();
                for(BlockLog log : allLogs) {
                    allSavedLogs.put(log.getLocation(), log);
                }

                for(Map.Entry<Location, BlockLog> entry : allSavedLogs.entrySet()) {
                    BlockLog log = entry.getValue();
                    // skips already processed blocks
                    if(log == null) {
                        continue;
                    }
                    AllowedItem allowedItem = WorldControlPlugin.INST.getAllowedItems().get(log.getBlockBeforeMaterial());
                    if(allowedItem == null) {
                        continue;
                    }
                    double rnd = Math.random() * (WorldControlPlugin.INST.config.timeFactor/100);
                    if(regenerateAll || DateUtil.getTimeStamp(log.getTime()) / 1000 + allowedItem.getRegenerationTime() + (allowedItem.getRegenerationTime() * rnd) < System.currentTimeMillis() / 1000) {
                        regenerateRecursive(log.getLocation());
                    }
                }
                
                startRestoreTask();
                WCLogger.info(restored + " blocks found!");
                regenerationRunning = false;
                regenerateAll = false;
            }
        }, 0);
    }

    private void regenerateRecursive(Location blockLocation) {
        if(!allSavedLogs.containsKey(blockLocation)
                || allSavedLogs.get(blockLocation) == null) {
            return;
        }
        restored++;
        blocksToRestore.add(allSavedLogs.get(blockLocation));
//        regenerateBlockLog(allSavedLogs.get(blockLocation));
        allSavedLogs.put(blockLocation, null);

//        if(restored >= informCnt * 100) {
//            informCnt++;
//            RaidCraft.LOGGER.info("[WC] " + restored + " Bloecke wurden bereits regeneriert.");
//        }

        regenerateRecursive(blockLocation.add( 0,  1,  0));
        regenerateRecursive(blockLocation.add( 1,  0,  0));
        regenerateRecursive(blockLocation.add( 0,  0,  1));
        regenerateRecursive(blockLocation.add(-1,  0,  0));
        regenerateRecursive(blockLocation.add( 0,  0, -1));
        regenerateRecursive(blockLocation.add( 0, -1,  0));
    }

    public void regenerateBlockLog(BlockLog log) {
        log.getLocation().getBlock().setType(log.getBlockBeforeMaterial());
        // remove mark
        RaidCraft.removePlayerPlacedBlock(log.getBlock());

        byte dataByte = (byte)log.getBlockBeforeData();

        // make leaves permanent
//        if(log.getBlockAfterMaterial() == Material.LEAVES) {
//            dataByte = 1<<4;
//        }
        log.getLocation().getBlock().setData(dataByte, true);
    }

    public boolean canRegenerate() {

        return !regenerationRunning && blocksToRestore.size() == 0;
    }
}