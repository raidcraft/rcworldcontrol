package de.raidcraft.worldcontrol.tables;

import com.silthus.raidcraft.util.component.database.Table;
import com.sk89q.commandbook.CommandBook;

import java.sql.SQLException;

/**
 * Author: Philip
 * Date: 14.11.12 - 06:13
 * Description:
 */
public class AllowedItemsTable extends Table {

    public AllowedItemsTable() {
        super("allowed_items", "worldcontrol_");
    }

    @Override
    public void createTable() {
        try {
            getConnection().prepareStatement(
                    "CREATE TABLE `" + getTableName() + "` (" +
                            "`id` INT NOT NULL AUTO_INCREMENT, " +
                            "`material` VARCHAR( 32 ) NOT NULL, " +
                            "`break` TINYINT( 1 ) NOT NULL, " +
                            "`place` TINYINT( 1 ) NOT NULL, " +
                            "`drops` TINYINT( 1 ) NOT NULL, " +
                            "`regeneration_time` INT( 11 ) NOT NULL, " +
                            "`place_distance` INT( 11 ) NOT NULL, " +
                            "`place_deepness` INT( 11 ) NOT NULL, " +
                            "PRIMARY KEY ( `id` )" +
                            ")").execute();
        } catch (SQLException e) {
            CommandBook.logger().warning(e.getMessage());
        }
    }
}
