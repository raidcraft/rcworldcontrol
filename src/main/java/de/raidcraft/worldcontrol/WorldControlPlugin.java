package de.raidcraft.worldcontrol;

import de.raidcraft.RaidCraft;
import de.raidcraft.api.BasePlugin;
import de.raidcraft.api.config.ConfigurationBase;
import de.raidcraft.api.config.Setting;
import de.raidcraft.worldcontrol.alloweditem.AllowedItemManager;
import de.raidcraft.worldcontrol.commands.Commands;
import de.raidcraft.worldcontrol.listener.BlockListener;
import de.raidcraft.worldcontrol.listener.PlayerListener;
import de.raidcraft.worldcontrol.regeneration.RegenerationManager;
import de.raidcraft.worldcontrol.tables.AllowedItemsTable;
import de.raidcraft.worldcontrol.tables.BlockLogsTable;
import org.bukkit.Bukkit;
import org.bukkit.Location;

/**
 * Author: Philip
 * Date: 13.11.12 - 06:22
 * Description:
 */
public class WorldControlPlugin extends BasePlugin {

    public LocalConfiguration config;

    public boolean allowPhysics = true;

    private RegenerationManager regenerationManager;

    @Override
    public void enable() {

        registerTable(AllowedItemsTable.class, new AllowedItemsTable());
        registerTable(BlockLogsTable.class, new BlockLogsTable());

        registerEvents(new BlockListener());
        registerEvents(new PlayerListener());

        registerCommands(Commands.class);
        reload();

        regenerationManager = new RegenerationManager(this);
        regenerationManager.regenerate("world", new Location(null , 0, 0, 0), 50000, false);

        Bukkit.getScheduler().runTaskTimerAsynchronously(this, new Runnable() {
            public void run() {

                LogSaver.INST.save();
            }
        }, 5 * 20, 10 * 20);
    }

    @Override
    public void reload() {

        config = configure(new LocalConfiguration(this));
        AllowedItemManager.INST.reload();
    }

    @Override
    public void disable() {

        getLogger().info("[WC] Saving block changes...");
        LogSaver.INST.save();
        RaidCraft.getTable(BlockLogsTable.class).otimizeTable();
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

    public RegenerationManager getRegenerationManager() {

        return regenerationManager;
    }
}
