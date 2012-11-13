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
    private int localPlaceDistance;
    private int actionDeepness;

    public AllowedItem(String materialName, int placeDistance, boolean blockBreak, boolean blockPlace, boolean dropItem, long regenerationTime, int actionDeepness) {
        this.material = Material.getMaterial(materialName);
        this.localPlaceDistance = placeDistance;
        this.blockBreak = blockBreak;
        this.blockPlace = blockPlace;
        this.dropItem = dropItem;
        this.regenerationTime = regenerationTime;
        this.actionDeepness = actionDeepness;
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

    public int getActionDeepness() {

        return actionDeepness;
    }
}
