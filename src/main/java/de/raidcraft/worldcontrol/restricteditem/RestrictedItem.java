package de.raidcraft.worldcontrol.restricteditem;

import org.bukkit.Material;

/**
 * Author: Philip
 * Date: 12.11.12 - 22:51
 * Description:
 */
public class RestrictedItem {

    private Material material;
    private boolean blockBreak;
    private boolean blockPlace;
    private boolean dropItem;
    private long regenerationTime;
    private int localPlaceDistance;
    private int maxPlaceHeight;
    private boolean farmOnly;

    public RestrictedItem(String materialName, int placeDistance, boolean blockBreak, boolean blockPlace, boolean dropItem, long regenerationTime, int maxPlaceHeight, boolean farmOnly) {

        this.material = Material.getMaterial(materialName);
        this.localPlaceDistance = placeDistance;
        this.blockBreak = blockBreak;
        this.blockPlace = blockPlace;
        this.dropItem = dropItem;
        this.regenerationTime = regenerationTime;
        this.maxPlaceHeight = maxPlaceHeight;
        this.farmOnly = farmOnly;
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

    public int getLocalPlaceDistance() {

        return localPlaceDistance;
    }

    public int getMaxPlaceHeight() {

        return maxPlaceHeight;
    }

    public boolean isFarmOnly() {

        return farmOnly;
    }
}
