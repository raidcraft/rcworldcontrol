package de.raidcraft.worldcontrol;

import org.bukkit.Material;

/**
 * Author: Philip
 * Date: 12.11.12 - 22:51
 * Description:
 */
public class AllowedItem {
    private Material material;
    private boolean blockBreak;
    private boolean blockPlace;
    private boolean dropItem;
    private long regenerationTime;
    private int placeDistance;

    public AllowedItem(String materialName, int placeDistance, boolean blockBreak, boolean blockPlace, boolean dropItem, long regenerationTime) {
        this.material = Material.getMaterial(materialName);
        this.placeDistance = placeDistance;
        this.blockBreak = blockBreak;
        this.blockPlace = blockPlace;
        this.dropItem = dropItem;
        this.regenerationTime = regenerationTime;
    }

    public Material getMaterial() {

        return material;
    }

    public boolean canBlockBreak() {

        return blockBreak;
    }

    public boolean canBlockPlace() {

        return blockPlace;
    }

    public boolean canDropItem() {

        return dropItem;
    }

    public long getRegenerationTime() {

        return regenerationTime;
    }

    public int getPlaceDistance() {

        return placeDistance;
    }
}
