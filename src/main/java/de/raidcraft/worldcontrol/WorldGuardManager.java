package de.raidcraft.worldcontrol;

import com.sk89q.commandbook.CommandBook;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

/**
 * Author: Philip
 * Date: 13.11.12 - 20:00
 * Description:
 */
public class WorldGuardManager {
    public final static WorldGuardManager INSTANCE = new WorldGuardManager();
    private WorldGuardPlugin worldGuard;

    public WorldGuardManager() {
        worldGuard = (WorldGuardPlugin) Bukkit.getPluginManager().getPlugin("WorldGuard");
    }

    public String getLocatedRegion(Location location) {
        for(ProtectedRegion region : worldGuard.getRegionManager(location.getWorld()).getApplicableRegions(location)) {
            return region.getId();
        }
        return null;
    }

}
