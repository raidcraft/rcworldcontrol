package de.raidcraft.worldcontrol;

import com.sk89q.commandbook.CommandBook;
import de.raidcraft.RaidCraft;
import de.raidcraft.api.BasePlugin;
import de.raidcraft.api.config.ConfigurationBase;
import de.raidcraft.api.config.Setting;
import de.raidcraft.worldcontrol.exceptions.NotAllowedItemException;
import de.raidcraft.worldcontrol.listener.BlockListener;
import de.raidcraft.worldcontrol.listener.PlayerListener;
import de.raidcraft.worldcontrol.tables.AllowedItemsTable;
import de.raidcraft.worldcontrol.tables.BlockLogsTable;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;

import java.util.HashMap;
import java.util.Map;

/**
 * Author: Philip
 * Date: 13.11.12 - 06:22
 * Description:
 */

public class WorldControlPlugin extends BasePlugin {

    public static WorldControlPlugin INST; // not so sweet solution

    public LocalConfiguration config;
    
    private Map<Material, AllowedItem> allowedItems = new HashMap<>();
    private int reloadTaskId;
    public boolean allowPhysics = true;

    @Override
    public void enable() {

        INST = this;
        allowedItems.clear();
        registerTable(AllowedItemsTable.class, new AllowedItemsTable());
        registerTable(BlockLogsTable.class, new BlockLogsTable());

        loadConfig();
        CommandBook.registerEvents(new BlockListener());
        CommandBook.registerEvents(new PlayerListener());
        registerCommands(Commands.class);
        loadAllowedItems();
        //TODO ACTIVATE
//      Regeneration.INST.regenerateBlocks();

        Bukkit.getScheduler().scheduleAsyncRepeatingTask(CommandBook.inst(), new Runnable() {
            public void run() {
                LogSaver.INSTANCE.save();
            }
        }, 5 * 20, 10 * 20);

        RaidCraft.LOGGER.info("[WC] Found DB connection, init worldcontrol module...");
        Bukkit.getScheduler().cancelTask(reloadTaskId);

    }

    @Override
    public void disable() {
        RaidCraft.LOGGER.info("[WC] Saving block changes...");
        LogSaver.INSTANCE.save();
    }


    public void loadConfig() {
        config = configure(new LocalConfiguration(this));
    }

    public void loadAllowedItems() {
        for(AllowedItem item : RaidCraft.getTable(AllowedItemsTable.class).getAllowedItems()) {
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
        if(RaidCraft.getTable(BlockLogsTable.class).isNearBlockPlaced(block, item)) {
            return true;
        }
        return false;
    }

    public static class LocalConfiguration extends ConfigurationBase<WorldControlPlugin> {

        public LocalConfiguration(WorldControlPlugin plugin) {

            super(plugin, "config.yml");
        }

        @Setting("world") public String world = "world";
        @Setting("farm-region-prefix") public String farmPrefix = "itemfarm_";
        @Setting("regeneration-max-add-time") public int timeFactor = 50;
    }
}
