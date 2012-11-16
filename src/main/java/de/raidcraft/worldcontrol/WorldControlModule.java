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
    private List<BlockLog> logs = new ArrayList<> ();
    private List<BlockLog> savingLogs = new ArrayList<>();
    private int savingProcessed = 0;
    private boolean saving = false;

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
        Regeneration.INSTANCE.regenerateBlocks();
        
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
        if(saving) {
            CommandBook.logger().info("[WC] Saving queue full! Left: " + (savingLogs.size() - savingProcessed));
            return;
        }
        saving = true;

        savingLogs = logs;
        logs = new ArrayList<>();

        for(BlockLog log : savingLogs) {
            ComponentDatabase.INSTANCE.getTable(BlockLogsTable.class).addLog(log);
            savingProcessed++;
        }
        savingProcessed = 0;
        savingLogs.clear();
        saving = false;
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
        for(BlockLog currLog : logs) {
            if(log.getLocation().getBlockX() == currLog.getLocation().getBlockX()
                    && log.getLocation().getBlockY() == currLog.getLocation().getBlockY()
                    && log.getLocation().getBlockZ() == currLog.getLocation().getBlockZ()) {
                return;
            }
        }
        logs.add(log);
    }

    public static class LocalConfiguration extends ConfigurationBase {
        @Setting("world") public String world = "world";
        @Setting("farm-region-prefix") public String farmPrefix = "itemfarm_";
        @Setting("regeneration-max-add-time") public int timeFactor = 50;
    }
}
