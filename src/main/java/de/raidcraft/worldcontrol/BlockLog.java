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
    private int id;
    private String player;
    private Location location;
    private Material blockBeforeMaterial = Material.AIR;
    private int blockBeforeData = 0;
    private Material blockAfterMaterial = Material.AIR;
    private int blockAfterData = 0;
    private String time;
    private boolean restored;

    public BlockLog(String player, Location location, Block blockBefore, Block blockAfter) {

        this.player = player;
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

    public BlockLog(int id
            ,String player
            , Location location 
            , Material blockBeforeMaterial
            , short blockBeforeData
            , Material blockAfterMaterial
            , short blockAfterData
            , String time
            , boolean restored) {

        this.id = id;
        this.player = player;
        this.location = location;
        this.time = time;
        this.restored = restored;

        this.blockBeforeMaterial = blockBeforeMaterial;
        this.blockBeforeData = blockBeforeData;

        this.blockAfterMaterial = blockAfterMaterial;
        this.blockAfterData = blockAfterData;
    }

    public int getId() {

        return id;
    }

    public String getPlayer() {

        return player;
    }

    public Location getLocation() {

        return location;
    }

    public void setBlockBeforeMaterial(Material blockBeforeMaterial) {

        this.blockBeforeMaterial = blockBeforeMaterial;
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

    public boolean isRestored() {

        return restored;
    }
}
