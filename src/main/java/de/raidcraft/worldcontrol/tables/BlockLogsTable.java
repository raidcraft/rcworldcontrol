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

import java.sql.PreparedStatement;
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

    private String selectNewestQuery = "SELECT player, before_material, before_data, after_material, after_data, world, x, y, z, time FROM " +
            "(SELECT *, UNIX_TIMESTAMP(STR_TO_DATE(time,'%d-%m-%Y %H:%i:%S')) AS timestamp FROM `" + getTableName() + "` " +
            "ORDER BY timestamp) AS t1 GROUP BY world, x, y, z";

    private String selectNewestFromWorldQuery = "SELECT id, player, before_material, before_data, after_material, after_data, world, x, y, z, time FROM " +
            "(SELECT *, UNIX_TIMESTAMP(STR_TO_DATE(time,'%d-%m-%Y %H:%i:%S')) AS timestamp FROM `" + getTableName() + "` " +
            "WHERE world = ? ORDER BY timestamp) AS t1 GROUP BY world, x, y, z";

    public BlockLogsTable() {

        super("block_logs", "worldcontrol_");
    }

    @Override
    public void createTable() {

        try {
            executeQuery(
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
                            ")");
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
                resultSet.close();
                return;
            }
            resultSet.close();
            executeUpdate(
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
            );
        } catch (SQLException e) {
            RaidCraft.LOGGER.warning("[WC] SQL exception: " + e.getMessage());
        }
    }

    public List<BlockLog> getAllLogs(String world) {

        List<BlockLog> blockLogs = new ArrayList<>();
        try {
            PreparedStatement statement = getConnection().prepareStatement(selectNewestFromWorldQuery);
            statement.setString(1, world);
            ResultSet resultSet = statement.executeQuery();

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
            statement.close();
            resultSet.close();
        } catch (SQLException e) {
            RaidCraft.LOGGER.warning("[WC] SQL exception: " + e.getMessage());
        }
        return blockLogs;
    }

    public boolean isNearBlockPlaced(Block block, AllowedItem item) {

        try {
            ResultSet resultSet = executeQuery(
                    "SELECT * FROM " + getTableName()
                            + " WHERE after_material = '" + item.getMaterial().name() + "'"
                            + " AND x > '" + (block.getLocation().getBlockX() - item.getLocalPlaceDistance()) + "'"
                            + " AND x < '" + (block.getLocation().getBlockX() + item.getLocalPlaceDistance()) + "'"
                            + " AND y > '" + (block.getLocation().getBlockY() - item.getLocalPlaceDistance()) + "'"
                            + " AND y < '" + (block.getLocation().getBlockY() + item.getLocalPlaceDistance()) + "'"
                            + " AND z > '" + (block.getLocation().getBlockZ() - item.getLocalPlaceDistance()) + "'"
                            + " AND z < '" + (block.getLocation().getBlockZ() + item.getLocalPlaceDistance()) + "';");

            while (resultSet.next()) {
                resultSet.close();
                return true;
            }
            resultSet.close();
        } catch (SQLException e) {
            RaidCraft.LOGGER.warning("[WC] SQL exception: " + e.getMessage());
        }
        return false;
    }

    public void deleteLog(int id) {

        try {
            executeUpdate(
                    "DELETE FROM " + getTableName() + " WHERE id =  '" + id + "'"
            );
        } catch (SQLException e) {
            RaidCraft.LOGGER.warning("[WC] SQL exception: " + e.getMessage());
        }
    }

    public boolean deleteLog(Location location, AllowedItem item) {

        try {
            PreparedStatement statement = getConnection().prepareStatement("DELETE FROM " + getTableName() + " WHERE " +
                    "world = '" + location.getWorld().getName() + "' " +
                    "AND x = '" + location.getBlockX() + "' " +
                    "AND y = '" + location.getBlockY() + "' " +
                    "AND z = '" + location.getBlockZ()+ "' " +
                    "AND before_material = 'AIR' " +
                    "AND after_material = '" + item.getMaterial().name() + "'");
            statement.execute();

            int updateCount = statement.getUpdateCount();
            statement.close();
            return (updateCount == 0) ? false : true;

        } catch (SQLException e) {
            RaidCraft.LOGGER.warning("[WC] SQL exception: " + e.getMessage());
        }
        return false;
    }

    public void deleteAll() {

        try {
            executeUpdate(
                    "DELETE FROM " + getTableName()
            );
        } catch (SQLException e) {
            RaidCraft.LOGGER.warning("[WC] SQL exception: " + e.getMessage());
        }
    }

    public void otimizeTable() {

        LogSaver.INST.setBlocked(true);

        try {
            executeUpdate("DROP TABLE IF EXISTS `" + getTableName() + "_temp`");
            executeUpdate("CREATE TABLE `" + getTableName() + "_temp` LIKE " + getTableName());
            executeUpdate("INSERT INTO `" + getTableName() + "_temp` " + selectNewestQuery);
            executeUpdate("TRUNCATE TABLE `" + getTableName() + "`");
            executeUpdate("INSERT INTO `" + getTableName() + "` SELECT player, before_material, before_data, after_material, after_data, world, x, y, z, time FROM `" + getTableName() + "_temp`");
            executeUpdate("DROP TABLE `" + getTableName() + "_temp`");
        } catch (SQLException e) {
            e.printStackTrace();
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
