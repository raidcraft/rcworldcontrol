package de.raidcraft.worldcontrol;

import com.silthus.raidcraft.util.component.database.ComponentDatabase;
import com.sk89q.commandbook.CommandBook;
import de.raidcraft.worldcontrol.tables.BlockLogsTable;
import de.raidcraft.worldcontrol.util.WCLogger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Author: Philip
 * Date: 16.11.12 - 18:32
 * Description:
 */
public class LogSaver {
    public final static LogSaver INSTANCE = new LogSaver();

    private List<BlockLog> logs = new ArrayList<> ();
    private List<BlockLog> savingLogs = new ArrayList<>();
    private int savingProcessed = 0;
    private boolean saving = false;
    private boolean blocked = false;

    public void addBlockLog(BlockLog log) {
        if(isBlocked()) {
            return;
        }
        for(BlockLog currLog : logs) {
            if(log.getLocation().getBlockX() == currLog.getLocation().getBlockX()
                    && log.getLocation().getBlockY() == currLog.getLocation().getBlockY()
                    && log.getLocation().getBlockZ() == currLog.getLocation().getBlockZ()) {
                return;
            }
        }
        logs.add(log);
    }

    public void save() {
        if(logs.size() <= 0) {
            return;
        }
        if(saving) {
            CommandBook.logger().info("[WC] Saving queue blocked! Left: " + (savingLogs.size() - savingProcessed));
            return;
        }
        saving = true;

        savingLogs = logs;
        logs = new ArrayList<>();

        final Connection connection = ComponentDatabase.INSTANCE.getNewConnection();
        PreparedStatement statement = null;
        
        String insertQuery = "INSERT INTO " + ComponentDatabase.INSTANCE.getTable(BlockLogsTable.class).getTableName() + 
                " (player, before_material, before_data, after_material, after_data, world, x, y, z, time, restored) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, 0)";

        try {
            connection.setAutoCommit(false);
            statement = connection.prepareStatement(insertQuery);
            for(BlockLog log : savingLogs) {

                statement.setString(1, log.getPlayer());
                statement.setString(2, log.getBlockBeforeMaterial().name());
                statement.setInt(3, log.getBlockBeforeData());
                statement.setString(4, log.getBlockAfterMaterial().name());
                statement.setInt(5, log.getBlockAfterData());
                statement.setString(6, log.getLocation().getWorld().getName());
                statement.setInt(7, log.getLocation().getBlockX());
                statement.setInt(8, log.getLocation().getBlockY());
                statement.setInt(9, log.getLocation().getBlockZ());
                statement.setString(10, log.getTime());
                statement.executeUpdate();
                savingProcessed++;

                if((savingProcessed + 1) % 1000 == 0) {
                    connection.commit();
                }
            }
            connection.commit();
        } catch (final SQLException ex) {
            CommandBook.logger().warning("[WC] SQL exception: " + ex.getMessage());
        } finally {
            try {
                if (statement != null)
                    statement.close();
                if(connection != null)
                    connection.close();
            } catch (final SQLException ex) {
                CommandBook.logger().warning("[WC] SQL exception on close: " + ex.getMessage());
            }
            WCLogger.info("Saved " + savingLogs.size() + " logs!");
            savingProcessed = 0;
            savingLogs.clear();
            saving = false;
        }
    }

    public List<BlockLog> getLogs() {

        return logs;
    }

    public boolean isSaving() {

        return saving;
    }

    public void setBlocked(boolean state) {
        blocked = state;
    }

    public boolean isBlocked() {
        return blocked;
    }
}
