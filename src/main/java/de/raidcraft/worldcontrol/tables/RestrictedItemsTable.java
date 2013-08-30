package de.raidcraft.worldcontrol.tables;

import de.raidcraft.RaidCraft;
import de.raidcraft.api.database.Table;
import de.raidcraft.worldcontrol.restricteditem.RestrictedItem;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Author: Philip
 * Date: 14.11.12 - 06:13
 * Description:
 */
public class RestrictedItemsTable extends Table {

    public RestrictedItemsTable() {

        super("allowed_items", "worldcontrol_");
    }

    @Override
    public void createTable() {

        try {
            executeUpdate(
                    "CREATE TABLE `" + getTableName() + "` (" +
                            "`id` INT NOT NULL AUTO_INCREMENT, " +
                            "`material` VARCHAR( 32 ) NOT NULL, " +
                            "`break` TINYINT( 1 ) NOT NULL, " +
                            "`place` TINYINT( 1 ) NOT NULL, " +
                            "`drops` TINYINT( 1 ) NOT NULL, " +
                            "`farm_only` TINYINT( 1 ) NOT NULL, " +
                            "`regeneration_time` BIGINT( 20 ) NOT NULL, " +
                            "`place_distance` INT( 11 ) NOT NULL, " +
                            "`place_max_height` INT( 11 ) NOT NULL, " +
                            "PRIMARY KEY ( `id` )" +
                            ")");
        } catch (SQLException e) {
            RaidCraft.LOGGER.warning(e.getMessage());
            e.printStackTrace();
        }
    }

    public List<RestrictedItem> getAllRestrictedItems() {

        List<RestrictedItem> restrictedItems = new ArrayList<>();
        try {
            ResultSet resultSet = executeQuery(
                    "SELECT * FROM " + getTableName() + ";");

            while (resultSet.next()) {
                restrictedItems.add(new RestrictedItem(
                        resultSet.getString("material"),
                        resultSet.getInt("place_distance"),
                        resultSet.getBoolean("break"),
                        resultSet.getBoolean("place"),
                        resultSet.getBoolean("drops"),
                        resultSet.getLong("regeneration_time"),
                        resultSet.getInt("place_max_height"),
                        resultSet.getBoolean("farm_only")
                ));
            }
            resultSet.close();
        } catch (SQLException e) {
            RaidCraft.LOGGER.warning(e.getMessage());
            e.printStackTrace();
        }
        return restrictedItems;
    }
}
