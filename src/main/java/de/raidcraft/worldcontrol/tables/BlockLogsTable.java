package de.raidcraft.worldcontrol.tables;

import com.silthus.raidcraft.util.component.database.Table;
import com.sk89q.commandbook.CommandBook;
import de.raidcraft.worldcontrol.BlockLog;

import java.sql.SQLException;

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
                            ");"
            ).execute();
        } catch (SQLException e) {
            CommandBook.logger().warning(e.getMessage());
        }
    }
}
