package de.raidcraft.worldcontrol.regeneration;

import de.raidcraft.RaidCraft;
import de.raidcraft.worldcontrol.BlockLog;
import de.raidcraft.worldcontrol.WorldControlPlugin;
import de.raidcraft.worldcontrol.tables.BlockLogsTable;
import de.raidcraft.worldcontrol.util.WCLogger;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Philip Urban
 */
public class RegenerationTask implements Runnable {

    private WorldControlPlugin plugin;
    private String world;
    private int radius;
    private Location start;
    private boolean force;
    private List<BlockLog> blocksToRestore = new ArrayList<>();
    private int stage = 0;
    private CollectingState collectingState = CollectingState.STOPPED;

    public RegenerationTask(String world, int radius, Location start, boolean force) {

        this.plugin = RaidCraft.getComponent(WorldControlPlugin.class);
        this.world = world;
        this.radius = radius;
        this.start = start;
        this.force = force;
    }

    @Override
    public void run() {

        switch(stage) {

            // collect blocks
            case 0:
                if(collectingState == CollectingState.STOPPED) {
                    WCLogger.info("Collect blocks for regeneration...");
                    collectingState = CollectingState.STARTED;
                    Bukkit.getScheduler().runTaskAsynchronously(plugin, new CollectBlocksTask(this));
                }
                else if(collectingState == CollectingState.FINISHED) {
                    WCLogger.info("Found " + blocksToRestore.size() + " blocks to regenerate!");
                    stage++;
                }
                break;

            // regenerate 200 blocks every round
            case 1:
                regenerateBlocks(200);
                break;

            // set regeneration as finished
            case 2:
                plugin.getRegenerationManager().regenerationFinished();
                break;
        }
    }

    private void regenerateBlocks(int number) {

        if (blocksToRestore.size() == 0) {
            stage++;
            return;
        }

        List<BlockLog> subList = new ArrayList<>(number);
        for(int i = 0; i < number; i++) {

            BlockLog log = blocksToRestore.remove(0);
            if(log == null) continue;
            subList.add(log);
            regenerateBlockLog(log);
        }

        cleanDatabase(subList);
    }

    private void regenerateBlockLog(BlockLog log) {

        log.getLocation().getBlock().setType(log.getBlockBeforeMaterial());
        // remove mark
        RaidCraft.removePlayerPlacedBlock(log.getBlock());

        byte dataByte = (byte) log.getBlockBeforeData();

        // make leaves permanent
        //        if(log.getBlockAfterMaterial() == Material.LEAVES) {
        //            dataByte = 1<<4;
        //        }
        log.getLocation().getBlock().setData(dataByte, true);
    }

    private void cleanDatabase(List<BlockLog> logs) {

        BlockLogsTable table = RaidCraft.getTable(BlockLogsTable.class);
        final Connection connection = table.getConnection();
        PreparedStatement statement = null;

        String updateQuery = "DELETE FROM `" + table.getTableName() +
                "` WHERE id = ?";

        try {
            connection.setAutoCommit(false);
            statement = connection.prepareStatement(updateQuery);

            int i = 1;
            //                            WCLogger.info("Try to delete " + blocksToRestore.size() + " rows...");
            for (BlockLog log : logs) {
                statement.setInt(1, log.getId());
                statement.executeUpdate();
                i++;

                if (i % 100 == 0) {
                    //                                    WCLogger.info("Already deleted " + i + " rows!");
                    connection.commit();
                }
            }
            connection.commit();
        } catch (final SQLException ex) {
            RaidCraft.LOGGER.warning("[WC] SQL exception: " + ex.getMessage());
        } finally {
            try {
                if (statement != null)
                    statement.close();
            } catch (final SQLException ex) {
                RaidCraft.LOGGER.warning("[WC] SQL exception on close: " + ex.getMessage());
            }
        }
    }

    public String getWorld() {

        return world;
    }

    public int getRadius() {

        return radius;
    }

    public Location getStart() {

        return start;
    }

    public boolean isForced() {

        return force;
    }

    public List<BlockLog> getBlocksToRestore() {

        return blocksToRestore;
    }

    public void setCollectingState(CollectingState collectingState) {

        this.collectingState = collectingState;
    }
}
