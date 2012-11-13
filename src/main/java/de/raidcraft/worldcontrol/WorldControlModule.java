package de.raidcraft.worldcontrol;

import com.sk89q.commandbook.CommandBook;
import com.zachsthings.libcomponents.ComponentInformation;
import com.zachsthings.libcomponents.bukkit.BukkitComponent;
import com.zachsthings.libcomponents.config.ConfigurationBase;
import com.zachsthings.libcomponents.config.Setting;
import de.raidcraft.worldcontrol.listener.BlockListener;
import de.raidcraft.worldcontrol.listener.PlayerListener;
import org.bukkit.Material;

import java.util.HashMap;
import java.util.Map;

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

        config = configure(new LocalConfiguration());
        CommandBook.registerEvents(new BlockListener());
        CommandBook.registerEvents(new PlayerListener());
        loadAllowedItems();
        INSTANCE = this;
    }

    public void loadAllowedItems() {
        //TODO
    }

    public static class LocalConfiguration extends ConfigurationBase {
        @Setting("world") public String world = "world";
    }
}
