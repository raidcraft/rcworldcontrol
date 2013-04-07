package de.raidcraft.worldcontrol;

import de.raidcraft.RaidCraft;
import de.raidcraft.api.BasePlugin;
import de.raidcraft.api.config.ConfigurationBase;
import de.raidcraft.api.config.Setting;
import de.raidcraft.worldcontrol.commands.Commands;
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

    public LocalConfiguration config;

    private Map<Material, AllowedItem> allowedItems = new HashMap<>();
    public boolean allowPhysics = true;

    @Override
    public void enable() {

        allowedItems.clear();

        registerTable(AllowedItemsTable.class, new AllowedItemsTable());
        registerTable(BlockLogsTable.class, new BlockLogsTable());

        registerEvents(new BlockListener());
        registerEvents(new PlayerListener());

        registerCommands(Commands.class);
        reload();
        //TODO ACTIVATE
        //Regeneration.INSTANCE.regenerateBlocks();

        Bukkit.getScheduler().runTaskTimerAsynchronously(this, new Runnable() {
            public void run() {

                LogSaver.INSTANCE.save();
            }
        }, 5 * 20, 10 * 20);
    }

    @Override
    public void reload() {

        config = configure(new LocalConfiguration(this));
        loadAllowedItems();
    }

    @Override
    public void disable() {

        getLogger().info("[WC] Saving block changes...");
        LogSaver.INSTANCE.save();
        RaidCraft.getTable(BlockLogsTable.class).cleanTable();
    }

    public void loadAllowedItems() {

        for (AllowedItem item : RaidCraft.getTable(AllowedItemsTable.class).getAllowedItems()) {
            allowedItems.put(item.getMaterial(), item);
        }
    }


    public AllowedItem getAllowedItem(Block block) throws NotAllowedItemException {

        if (allowedItems.containsKey(block.getType())) {
            return allowedItems.get(block.getType());
        }
        throw new NotAllowedItemException();
    }

    public Map<Material, AllowedItem> getAllowedItems() {

        return allowedItems;
    }

    public boolean isNearBlockPlaced(Block block, AllowedItem item) {

        for (BlockLog log : LogSaver.INSTANCE.getLogs()) {
            if (log.getBlockAfterMaterial() == item.getMaterial()) {
                if (log.getLocation().distance(block.getLocation()) < item.getLocalPlaceDistance()) {
                    return true;
                }
            }
        }
        return RaidCraft.getTable(BlockLogsTable.class).isNearBlockPlaced(block, item);
    }

    public static class LocalConfiguration extends ConfigurationBase<WorldControlPlugin> {

        @Setting("world")
        public String world = "world";
        @Setting("farm-region-prefix")
        public String farmPrefix = "itemfarm_";
        @Setting("regeneration-max-add-time")
        public int timeFactor = 50;

        public LocalConfiguration(WorldControlPlugin plugin) {

            super(plugin, "config.yml");
        }
    }
}
