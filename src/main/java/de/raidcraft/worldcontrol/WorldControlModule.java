package de.raidcraft.worldcontrol;

import com.silthus.raidcraft.util.component.database.ComponentDatabase;
import com.sk89q.commandbook.CommandBook;
import com.sk89q.worldedit.LocalConfiguration;
import com.zachsthings.libcomponents.ComponentInformation;
import com.zachsthings.libcomponents.bukkit.BukkitComponent;
import com.zachsthings.libcomponents.config.ConfigurationBase;
import com.zachsthings.libcomponents.config.Setting;
import de.raidcraft.worldcontrol.exceptions.UnknownAllowedItemException;
import de.raidcraft.worldcontrol.listener.BlockListener;
import de.raidcraft.worldcontrol.listener.PlayerListener;
import de.raidcraft.worldcontrol.tables.AllowedItemsTable;
import de.raidcraft.worldcontrol.tables.BlockLogsTable;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.util.*;

/**
 * Author: Philip
 * Date: 13.11.12 - 06:22
 * Description:
 */
@ComponentInformation(
        friendlyName = "World Control",
        desc = "Check block actions and regenerates the world"
)
public class WorldControlModule extends BukkitComponent {
    public static WorldControlModule INSTANCE; // not so sweet solution

    public LocalConfiguration config;

    private Map<Material, AllowedItem> allowedItems = new HashMap<>();
    private List<BlockLog> logs = new ArrayList<> ();

    @Override
    public void enable() {

        ComponentDatabase.INSTANCE.registerTable(AllowedItemsTable.class, new AllowedItemsTable());
        ComponentDatabase.INSTANCE.registerTable(BlockLogsTable.class, new BlockLogsTable());

        config = configure(new LocalConfiguration());
        CommandBook.registerEvents(new BlockListener());
        CommandBook.registerEvents(new PlayerListener());
        loadAllowedItems();
        INSTANCE = this;

        CommandBook.inst().getServer().getScheduler().scheduleAsyncRepeatingTask(CommandBook.inst(), new Runnable() {
            public void run() {
                saveLogs();
            }
        }, 5 * 20, 10 * 20);
    }

    public void loadAllowedItems() {
        //TODO
    }

    private void saveLogs() {
        if(logs.size() <= 0) {
            return;
        }

        List<BlockLog> logsCopy = logs;
        logs = new ArrayList<>();

        for(BlockLog log : logsCopy) {
            ComponentDatabase.INSTANCE.getTable(BlockLogsTable.class).addLog(log);
        }
    }

    public AllowedItem getAllowedItem(Block block) throws UnknownAllowedItemException {
        if(allowedItems.containsKey(block.getType())) {
            return allowedItems.get(block.getType());
        }
        throw new UnknownAllowedItemException();
    }

    public boolean canPlace(Player player, AllowedItem item) {
        if(!item.canBlockPlace()) {
            return false;
        }

        //TODO check deepness, place radius
        //TODO check in database if item is already placed in configured radius
        return false;
    }
    
    public boolean canBreak(Player player, AllowedItem item) {
        if(!item.canBlockBreak()) {
            return false;
        }
        return true;
    }

    public void addBlockLog(BlockLog log) {
        logs.add(log);
    }

    public static class LocalConfiguration extends ConfigurationBase {
        @Setting("world") public String world = "world";
    }
}
