package de.raidcraft.worldcontrol;

import com.sk89q.commandbook.CommandBook;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sun.org.apache.bcel.internal.generic.INSTANCEOF;
import org.bukkit.Bukkit;
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

    public boolean inRegion(Player player) {
        if(worldGuard.getRegionManager(player.getWorld()).getApplicableRegions(player.getLocation()).size() > 0) {
            return true;
        }
        return false;
    }

}
