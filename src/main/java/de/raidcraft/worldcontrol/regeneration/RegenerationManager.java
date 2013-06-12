package de.raidcraft.worldcontrol.regeneration;

import de.raidcraft.worldcontrol.LogSaver;
import de.raidcraft.worldcontrol.WorldControlPlugin;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.scheduler.BukkitTask;

/**
 * @author Philip Urban
 */
public class RegenerationManager {

    private WorldControlPlugin plugin;
    private BukkitTask task;

    public RegenerationManager(WorldControlPlugin plugin) {

        this.plugin = plugin;
    }

    public boolean regenerate(String world, Location start, int radius, boolean force) {

        if(isRegenerationRunning()) {
            return false;
        }

        if(Bukkit.getWorld(world) == null) {
            return false;
        }

        LogSaver.INST.setBlocked(true);
        task = Bukkit.getScheduler().runTaskTimer(plugin, new RegenerationTask(world, radius, start, force), 0, 4*20);
        return true;
    }

    public void regenerationFinished() {

        task.cancel();
        task = null;
        LogSaver.INST.setBlocked(false);
    }

    public boolean isRegenerationRunning() {

        if(task != null) {
            return true;
        }
        return false;
    }
}
