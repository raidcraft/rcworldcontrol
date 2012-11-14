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
import org.bukkit.Bukkit;
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
    private List<BlockLog> logs = new ArrayList<> ();
    private boolean regenerationRunning = false;

    @Override
    public void enable() {
        allowedItems.clear();
        ComponentDatabase.INSTANCE.registerTable(AllowedItemsTable.class, new AllowedItemsTable());
        ComponentDatabase.INSTANCE.registerTable(BlockLogsTable.class, new BlockLogsTable());

        loadConfig();
        CommandBook.registerEvents(new BlockListener());
        CommandBook.registerEvents(new PlayerListener());
        registerCommands(Commands.class);
        loadAllowedItems();
        INSTANCE = this;
        
        regenerateBlocks();
        
        CommandBook.inst().getServer().getScheduler().scheduleAsyncRepeatingTask(CommandBook.inst(), new Runnable() {
            public void run() {
                saveLogs();
            }
        }, 5 * 20, 10 * 20);
    }

    public void loadConfig() {
        config = configure(new LocalConfiguration());
    }

    public void loadAllowedItems() {
        for(AllowedItem item : ComponentDatabase.INSTANCE.getTable(AllowedItemsTable.class).getAllowedItems()) {
            allowedItems.put(item.getMaterial(), item);
        }
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

    public AllowedItem getAllowedItem(Block block) throws NotAllowedItemException {
        if(allowedItems.containsKey(block.getType())) {
            return allowedItems.get(block.getType());
        }
        throw new NotAllowedItemException();
    }
    
    public boolean isNearBlockPlaced(Block block, AllowedItem item) {
        for(BlockLog log : logs) {
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

    public void addBlockLog(BlockLog log) {
        logs.add(log);
    }
    
    public void regenerateBlocks() {
        regenerateBlocks(false);
    }
    
    public void regenerateBlocks(boolean all) {
        regenerationRunning = true;
        //TODO implement
        if(all) {
            CommandBook.inst().getServer().getScheduler().scheduleAsyncDelayedTask(CommandBook.inst(), new Runnable() {
                public void run() {
                    int i = 0;
                    int informCnt = 1;
                    for(BlockLog log : ComponentDatabase.INSTANCE.getTable(BlockLogsTable.class).getAllLogs()) {
                        regenerateBlockLog(log);
                        i++;
                        if(i >= informCnt * 100) {
                            informCnt++;
                            CommandBook.logger().info("[WC] " + i + " Blöcke wurden bereits regeneriert.");
                        }
                    }
                    ComponentDatabase.INSTANCE.getTable(BlockLogsTable.class).deleteAll();
                    CommandBook.logger().info("[WC] Regenerierung fertig. Es wurden insgesamt " + i + " Blöcke regeneriert!");
                    regenerationRunning = false;    
                }
            }, 0);
        }
        else {

            regenerationRunning = false;
        }
    }
    
    public void regenerateBlockLog(BlockLog log) {
        log.getLocation().getBlock().setType(log.getBlockBeforeMaterial());
        log.getLocation().getBlock().setData((byte)log.getBlockBeforeData());
    }

    public boolean isRegenerationRunning() {

        return regenerationRunning;
    }

    public static class LocalConfiguration extends ConfigurationBase {
        @Setting("world") public String world = "world";
        @Setting("farm-region-prefix") public String farmPrefix = "itemfarm_";
    }
}
