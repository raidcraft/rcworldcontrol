package de.raidcraft.worldcontrol.tables;

import com.silthus.raidcraft.util.component.database.Table;
import com.sk89q.commandbook.CommandBook;
import de.raidcraft.worldcontrol.AllowedItem;
import de.raidcraft.worldcontrol.BlockLog;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Author: Philip
 * Date: 14.11.12 - 06:14
 * Description:
 */
public class BlockLogsTable extends Table {

    public BlockLogsTable() {
        super("block_logs", "worldcontrol_");
    }

    @Override
    public void createTable() {
        try {
            getConnection().prepareStatement(
                    "CREATE TABLE `" + getTableName() + "` (" +
                            "`id` INT NOT NULL AUTO_INCREMENT, " +
                            "`player` VARCHAR( 32 ) NOT NULL, " +
                            "`before_material` VARCHAR( 32 ) NOT NULL, " +
                            "`before_data` INT( 11 ) NOT NULL, " +
                            "`after_material` VARCHAR( 32 ) NOT NULL, " +
                            "`after_data` INT( 11 ) NOT NULL, " +
                            "`world` VARCHAR( 32 ) NOT NULL, " +
                            "`x` INT( 11 ) NOT NULL, " +
                            "`y` INT( 11 ) NOT NULL, " +
                            "`z` INT( 11 ) NOT NULL, " +
                            "`time` VARCHAR( 100 ) NOT NULL, " +
                            "PRIMARY KEY ( `id` )" +
                            ")").execute();
        } catch (SQLException e) {
            CommandBook.logger().warning(e.getMessage());
        }
    }

    public void addLog(BlockLog log) {
        try {
            // insert only if no entry exists for coordinate
            ResultSet resultSet = getConnection().prepareStatement(
                    "SELECT * FROM " + getTableName()
                            + " WHERE world = '" + log.getLocation().getWorld().getName() + "'"
                            + " AND x = '" + log.getLocation().getBlockX() + "'"
                            + " AND y = '" + log.getLocation().getBlockY() + "'"
                            + " AND z = '" + log.getLocation().getBlockZ() + "'").executeQuery();

            while (resultSet.next()) {
                return;
            }

            getConnection().prepareStatement(
                    "INSERT INTO " + getTableName() + " (player, before_material, before_data, after_material, after_data, world, x, y, z, time) " +
                            "VALUES (" +
                            "'" + log.getPlayer() + "'" + "," +
                            "'" + log.getBlockBeforeMaterial().name() + "'" + "," +
                            "'" + log.getBlockBeforeData() + "'" + "," +
                            "'" + log.getBlockAfterMaterial().name() + "'" + "," +
                            "'" + log.getBlockAfterData() + "'" + "," +
                            "'" + log.getLocation().getWorld().getName() + "'" + "," +
                            "'" + log.getLocation().getBlockX() + "'" + "," +
                            "'" + log.getLocation().getBlockY() + "'" + "," +
                            "'" + log.getLocation().getBlockZ() + "'" + "," +
                            "'" + log.getTime() + "'" +
                            ")"
            ).execute();
        } catch (SQLException e) {
            CommandBook.logger().warning(e.getMessage());
        }
    }

    public List<BlockLog> getAllLogs() {
        List<BlockLog> blockLogs = new ArrayList<>();
        try {
            ResultSet resultSet = getConnection().prepareStatement(
                    "SELECT * FROM " + getTableName() + ";").executeQuery();

            while (resultSet.next()) {
                blockLogs.add(new BlockLog(
                        resultSet.getInt("id"),
                        resultSet.getString("player"),
                        new Location(
                            Bukkit.getWorld(resultSet.getString("world")),
                            resultSet.getDouble("x"),
                            resultSet.getDouble("y"),
                            resultSet.getDouble("z")
                        ),
                        Material.getMaterial(resultSet.getString("before_material")),
                        resultSet.getShort("before_data"),
                        Material.getMaterial(resultSet.getString("after_material")),
                        resultSet.getShort("after_data"),
                        resultSet.getString("time")
                ));
            }
        }
        catch (SQLException e) {
        }
        return blockLogs;
    }

    public boolean isNearBlockPlaced(Block block, AllowedItem item) {
        try {
            ResultSet resultSet = getConnection().prepareStatement(
                    "SELECT * FROM " + getTableName()
                            + " WHERE after_material = '" + item.getMaterial().name() + "'"
                            + " AND x > '" + (block.getLocation().getBlockX() - item.getLocalPlaceDistance()) + "'"
                            + " AND x < '" + (block.getLocation().getBlockX() + item.getLocalPlaceDistance()) + "'"
                            + " AND y > '" + (block.getLocation().getBlockY() - item.getLocalPlaceDistance()) + "'"
                            + " AND y < '" + (block.getLocation().getBlockY() + item.getLocalPlaceDistance()) + "'"
                            + " AND z > '" + (block.getLocation().getBlockZ() - item.getLocalPlaceDistance()) + "'"
                            + " AND z < '" + (block.getLocation().getBlockZ() + item.getLocalPlaceDistance()) + "';").executeQuery();

            while (resultSet.next()) {
                return true;
            }
        }
        catch (SQLException e) {
        }
        return false;
    }

    public void deleteLog(int id) {
        try {
            getConnection().prepareStatement(
                    "DELETE FROM " + getTableName() + " WHERE id =  '" + id + "'"
            ).execute();
        } catch (SQLException e) {
            CommandBook.logger().warning(e.getMessage());
        }
    }

    public void deleteAll() {
        try {
            getConnection().prepareStatement(
                    "DELETE FROM " + getTableName()
            ).execute();
        } catch (SQLException e) {
            CommandBook.logger().warning(e.getMessage());
        }
    }
}
