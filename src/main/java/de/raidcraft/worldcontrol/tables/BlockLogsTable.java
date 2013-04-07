package de.raidcraft.worldcontrol.tables;

import de.raidcraft.RaidCraft;
import de.raidcraft.api.database.Table;
import de.raidcraft.worldcontrol.BlockLog;
import de.raidcraft.worldcontrol.LogSaver;
import de.raidcraft.worldcontrol.alloweditem.AllowedItem;
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

    private String selectNewestQuery = "SELECT id, player, before_material, before_data, after_material, after_data, world, x, y, z, time FROM " +
            "(SELECT *, UNIX_TIMESTAMP(STR_TO_DATE(time,'%d-%m-%Y %H:%i:%S')) AS timestamp FROM `" + getTableName() + "` " +
            "ORDER BY timestamp) AS t1 GROUP BY world, x, y, z";

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
            RaidCraft.LOGGER.warning("[WC] SQL exception: " + e.getMessage());
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
            RaidCraft.LOGGER.warning("[WC] SQL exception: " + e.getMessage());
        }
    }

    public List<BlockLog> getAllOldestLogs() {

        List<BlockLog> blockLogs = new ArrayList<>();
        try {
            ResultSet resultSet = getConnection().prepareStatement(selectNewestQuery).executeQuery();

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
        } catch (SQLException e) {
            RaidCraft.LOGGER.warning("[WC] SQL exception: " + e.getMessage());
        }
        return blockLogs;
    }

    public List<BlockLog> getAllLogs() {

        List<BlockLog> blockLogs = new ArrayList<>();
        try {
            ResultSet resultSet = getConnection().prepareStatement(selectNewestQuery).executeQuery();

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
        } catch (SQLException e) {
            RaidCraft.LOGGER.warning("[WC] SQL exception: " + e.getMessage());
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
        } catch (SQLException e) {
            RaidCraft.LOGGER.warning("[WC] SQL exception: " + e.getMessage());
        }
        return false;
    }

    public void deleteLog(int id) {

        try {
            getConnection().prepareStatement(
                    "DELETE FROM " + getTableName() + " WHERE id =  '" + id + "'"
            ).execute();
        } catch (SQLException e) {
            RaidCraft.LOGGER.warning("[WC] SQL exception: " + e.getMessage());
        }
    }

    public void deleteLog(Location location, AllowedItem item) {

        try {
            getConnection().prepareStatement(
                    "DELETE FROM " + getTableName() + " WHERE " +
                            "world = '" + location.getWorld().getName() + "' " +
                            "AND x = '" + location.getY() + "' " +
                            "AND y = '" + location.getY() + "' " +
                            "AND z = '" + location.getY() + "' " +
                            "AND before_material = 'AIR' " +
                            "AND after_material = '" + item.getMaterial().name() + "'"
            ).execute();
        } catch (SQLException e) {
            RaidCraft.LOGGER.warning("[WC] SQL exception: " + e.getMessage());
        }
    }

    public void deleteAll() {

        try {
            getConnection().prepareStatement(
                    "DELETE FROM " + getTableName()
            ).execute();
        } catch (SQLException e) {
            RaidCraft.LOGGER.warning("[WC] SQL exception: " + e.getMessage());
        }
    }

    public void otimizeTable() {

        LogSaver.INST.setBlocked(true);

        try {
            getConnection().prepareStatement("CREATE TABLE IF NOT EXISTS `" + getTableName() + "_temp` LIKE " + getTableName()).execute();
            getConnection().prepareStatement("INSERT INTO `" + getTableName() + "_temp` " + selectNewestQuery).execute();
            getConnection().prepareStatement("TRUNCATE TABLE `" + getTableName() + "`").execute();
            getConnection().prepareStatement("INSERT INTO `" + getTableName() + "` SELECT * FROM `" + getTableName() + "_temp`").execute();
            getConnection().prepareStatement("DROP TABLE `" + getTableName() + "_temp`").execute();
        } catch (SQLException e) {
            RaidCraft.LOGGER.warning("[WC] SQL exception: " + e.getMessage());
        }

        LogSaver.INST.setBlocked(false);
    }

    // !!!this hardcore query crash the mysql service!!!
    //    public void otimizeTable() {
    //        try {
    //            getConnection().prepareStatement(
    //                    "DELETE FROM `" + getTableName() + "` WHERE id NOT IN (SELECT id FROM " +
    //                            "(SELECT *, UNIX_TIMESTAMP(STR_TO_DATE(time,'%d-%m-%Y %H:%i:%S')) AS timestamp FROM `" + getTableName() + "` " +
    //                            "ORDER BY timestamp DESC) AS t1 GROUP BY world, x, y, z)").executeUpdate();
    //        } catch (SQLException e) {
    //            RaidCraft.LOGGER.warning("[WC] SQL exception: " + e.getMessage());
    //        }
    //    }
}
