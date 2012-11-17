package de.raidcraft.worldcontrol;

import com.silthus.raidcraft.util.component.database.ComponentDatabase;
import com.sk89q.commandbook.CommandBook;
import com.zachsthings.libcomponents.ComponentInformation;
import com.zachsthings.libcomponents.bukkit.BukkitComponent;
import com.zachsthings.libcomponents.config.ConfigurationBase;
import com.zachsthings.libcomponents.config.Setting;
import de.raidcraft.worldcontrol.exceptions.NotAllowedItemException;
import de.raidcraft.worldcontrol.listener.BlockListener;
import de.raidcraft.worldcontrol.listener.PlayerListener;
import de.raidcraft.worldcontrol.tables.AllowedItemsTable;
import de.raidcraft.worldcontrol.tables.BlockLogsTable;
import org.bukkit.Material;
import org.bukkit.block.Block;

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

    @Override
    public void enable() {
        INSTANCE = this;
        allowedItems.clear();
        ComponentDatabase.INSTANCE.registerTable(AllowedItemsTable.class, new AllowedItemsTable());
        ComponentDatabase.INSTANCE.registerTable(BlockLogsTable.class, new BlockLogsTable());

        loadConfig();
        CommandBook.registerEvents(new BlockListener());
        CommandBook.registerEvents(new PlayerListener());
        registerCommands(Commands.class);
        loadAllowedItems();
        //TODO ACTIVATE
        Regeneration.INSTANCE.regenerateBlocks();
        
        CommandBook.inst().getServer().getScheduler().scheduleAsyncRepeatingTask(CommandBook.inst(), new Runnable() {
            public void run() {
                LogSaver.INSTANCE.save();
            }
        }, 5 * 20, 10 * 20);
    }

    @Override
    public void disable() {
        CommandBook.logger().info("[WC] Saving block changes...");
        LogSaver.INSTANCE.save();
        super.disable();
    }


    public void loadConfig() {
        config = configure(new LocalConfiguration());
    }

    public void loadAllowedItems() {
        for(AllowedItem item : ComponentDatabase.INSTANCE.getTable(AllowedItemsTable.class).getAllowedItems()) {
            allowedItems.put(item.getMaterial(), item);
        }
    }



    public AllowedItem getAllowedItem(Block block) throws NotAllowedItemException {
        if(allowedItems.containsKey(block.getType())) {
            return allowedItems.get(block.getType());
        }
        throw new NotAllowedItemException();
    }
    
    public Map<Material, AllowedItem> getAllowedItems() {
        return allowedItems;
    }
    
    public boolean isNearBlockPlaced(Block block, AllowedItem item) {
        for(BlockLog log : LogSaver.INSTANCE.getLogs()) {
            if(log.getBlockAfterMaterial() == item.getMaterial()) {
                if(log.getLocation().distance(block.getLocation()) < item.getLocalPlaceDistance()) {
                    return true;
                }
            }
        }
        if(ComponentDatabase.INSTANCE.getTable(BlockLogsTable.class).isNearBlockPlaced(block, item)) {
            return true;
        }
        return false;
    }

    public static class LocalConfiguration extends ConfigurationBase {
        @Setting("world") public String world = "world";
        @Setting("farm-region-prefix") public String farmPrefix = "itemfarm_";
        @Setting("regeneration-max-add-time") public int timeFactor = 50;
    }
}
