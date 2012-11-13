package de.raidcraft.worldcontrol;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;

/**
 * Author: Philip
 * Date: 13.11.12 - 19:44
 * Description:
 */
public class BlockLog {
    private String player;
    private Location location;
    private Material material;
    private int blockData;

    public BlockLog(String player, Location location, Block block) {

        this.player = player;
        this.location = location;
        this.material = block.getType();
        this.blockData = block.getData();
    }

    public String getPlayer() {

        return player;
    }

    public Location getLocation() {

        return location;
    }

    public Material getMaterial() {

        return material;
    }

    public int getBlockData() {

        return blockData;
    }
}
