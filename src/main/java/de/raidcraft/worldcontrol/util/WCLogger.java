package de.raidcraft.worldcontrol.util;

import de.raidcraft.RaidCraft;
import org.bukkit.ChatColor;

/**
 * Author: Philip
 * Date: 17.11.12 - 18:43
 * Description:
 */
public class WCLogger {
    public final static String PREFIX = ChatColor.DARK_GRAY + "[" + ChatColor.GOLD + "WC" + ChatColor.DARK_GRAY + "] " + ChatColor.WHITE;
    
    public static void info(String msg) {
        RaidCraft.LOGGER.info(ChatColor.stripColor(PREFIX + msg));
//        for(Player player : Bukkit.getOnlinePlayers()) {
//            if(player.hasPermission("worldcontrol.infos")) {
//                player.sendMessage(PREFIX + ChatColor.YELLOW + msg);
//            }
//        }
    }

}
