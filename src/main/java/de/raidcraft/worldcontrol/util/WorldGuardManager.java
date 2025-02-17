package de.raidcraft.worldcontrol.util;

import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.DefaultFlag;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import de.raidcraft.RaidCraft;
import de.raidcraft.worldcontrol.WorldControlPlugin;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

import java.util.Map;

/**
 * Author: Philip
 * Date: 13.11.12 - 20:00
 * Description:
 */
public class WorldGuardManager {

    public final static WorldGuardManager INST = new WorldGuardManager();
    private WorldGuardPlugin worldGuard;

    public WorldGuardManager() {

        worldGuard = (WorldGuardPlugin) Bukkit.getPluginManager().getPlugin("WorldGuard");
    }

    public String getLocatedRegion(Location location) {

        for (ProtectedRegion region : worldGuard.getRegionManager(location.getWorld()).getApplicableRegions(location)) {
            return region.getId();
        }
        return null;
    }

    public boolean isInUnknownRegion(Location location) {

        ApplicableRegionSet regions = worldGuard.getRegionManager(location.getWorld()).getApplicableRegions(location);
        if(regions.size() == 0) {
            return false;
        }
        for (ProtectedRegion region : regions) {
            if(region.getId().startsWith(RaidCraft.getComponent(WorldControlPlugin.class).config.farmPrefix)) {
                return false;
            }
        }
        return true;
    }

    public boolean isFarm(Location location) {
        ApplicableRegionSet regions = worldGuard.getRegionManager(location.getWorld()).getApplicableRegions(location);
        if(regions.size() == 0) {
            return false;
        }
        for (ProtectedRegion region : regions) {
            if(region.getId().startsWith(RaidCraft.getComponent(WorldControlPlugin.class).config.farmPrefix)) {
                return true;
            }
        }
        return false;
    }

    public void updateItemFarmFlags(World world) {

        for(Map.Entry<String, ProtectedRegion> entry : worldGuard.getRegionManager(world).getRegions().entrySet()) {

            if(!entry.getKey().startsWith(RaidCraft.getComponent(WorldControlPlugin.class).config.farmPrefix)) continue;
            ProtectedRegion region = entry.getValue();
            region.setFlag(DefaultFlag.BUILD, StateFlag.State.ALLOW);
        }

    }

}
