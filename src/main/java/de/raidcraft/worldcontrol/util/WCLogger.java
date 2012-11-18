package de.raidcraft.worldcontrol.util;

import com.avaje.ebeaninternal.server.lib.sql.Prefix;
import com.sk89q.commandbook.CommandBook;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.getspout.spoutapi.gui.ChatBar;

/**
 * Author: Philip
 * Date: 17.11.12 - 18:43
 * Description:
 */
public class WCLogger {
    public final static String PREFIX = ChatColor.DARK_GRAY + "[" + ChatColor.GOLD + "WC" + ChatColor.DARK_GRAY + "] " + ChatColor.WHITE;
    
    public static void info(String msg) {
        CommandBook.logger().info(ChatColor.stripColor(PREFIX + msg));
//        for(Player player : Bukkit.getOnlinePlayers()) {
//            if(player.hasPermission("worldcontrol.infos")) {
//                player.sendMessage(PREFIX + ChatColor.YELLOW + msg);
//            }
//        }
    }

}
