package de.raidcraft.worldcontrol;

import de.raidcraft.RaidCraft;
import de.raidcraft.api.BasePlugin;
import de.raidcraft.api.config.ConfigurationBase;
import de.raidcraft.api.config.Setting;
import de.raidcraft.worldcontrol.restricteditem.RestrictedItemManager;
import de.raidcraft.worldcontrol.commands.Commands;
import de.raidcraft.worldcontrol.listener.BlockListener;
import de.raidcraft.worldcontrol.listener.PlayerListener;
import de.raidcraft.worldcontrol.regeneration.RegenerationManager;
import de.raidcraft.worldcontrol.tables.RestrictedItemsTable;
import de.raidcraft.worldcontrol.tables.BlockLogsTable;
import de.raidcraft.worldcontrol.util.WorldGuardManager;
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
    private RestrictedItemManager restrictedItemManager;

    @Override
    public void enable() {

        registerTable(RestrictedItemsTable.class, new RestrictedItemsTable());
        registerTable(BlockLogsTable.class, new BlockLogsTable());

        registerEvents(new BlockListener());
        registerEvents(new PlayerListener());

        registerCommands(Commands.class);

        restrictedItemManager = new RestrictedItemManager(this); // must loaded before reload is called!!!

        reload();

        regenerationManager = new RegenerationManager(this);
        regenerationManager.regenerate(Bukkit.getWorlds().get(0).getName(), new Location(Bukkit.getWorlds().get(0) , 0, 0, 0), 50000, false);

        Bukkit.getScheduler().runTaskTimerAsynchronously(this, new Runnable() {
            public void run() {

                LogSaver.INST.save();
            }
        }, 5 * 20, 10 * 20);
    }

    @Override
    public void reload() {

        config = configure(new LocalConfiguration(this));
        restrictedItemManager.reload();
        WorldGuardManager.INST.updateItemFarmFlags(Bukkit.getWorld(config.world));
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

    public RestrictedItemManager getRestrictedItemManager() {

        return restrictedItemManager;
    }
}
