package de.raidcraft.worldcontrol;

import com.silthus.raidcraft.util.component.DateUtil;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

/**
 * Author: Philip
 * Date: 13.11.12 - 19:44
 * Description:
 */
public class BlockLog {
    private String player;
    private Location location;
    private Material blockBeforeMaterial = Material.AIR;
    private int blockBeforeData = 0;
    private Material blockAfterMaterial = Material.AIR;
    private int blockAfterData = 0;
    private String time;

    public BlockLog(Player player, Location location, Block blockBefore, Block blockAfter) {

        this.player = player.getName();
        this.location = location;
        this.time = DateUtil.getCurrentDateString();

        if(blockBefore != null) {
            this.blockBeforeMaterial = blockBefore.getType();
            this.blockBeforeData = blockBefore.getData();
        }

        if(blockAfter != null) {
            this.blockAfterMaterial = blockAfter.getType();
            this.blockAfterData = blockAfter.getData();
        }
    }

    public String getPlayer() {

        return player;
    }

    public Location getLocation() {

        return location;
    }

    public Material getBlockBeforeMaterial() {

        return blockBeforeMaterial;
    }

    public int getBlockBeforeData() {

        return blockBeforeData;
    }

    public Material getBlockAfterMaterial() {

        return blockAfterMaterial;
    }

    public int getBlockAfterData() {

        return blockAfterData;
    }

    public String getTime() {

        return time;
    }
}
