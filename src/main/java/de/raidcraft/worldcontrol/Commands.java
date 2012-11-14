package de.raidcraft.worldcontrol;

import com.sk89q.minecraft.util.commands.Command;
import com.sk89q.minecraft.util.commands.CommandContext;
import com.sk89q.minecraft.util.commands.CommandPermissions;
import com.sk89q.minecraft.util.commands.NestedCommand;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

/**
 * Author: Philip
 * Date: 04.11.12 - 15:31
 * Description:
 */
public class Commands {

    public Commands(WorldControlModule module) {
    }

    @Command(
            aliases = {"worldcontrol", "wc"},
            desc = "Manage world control module."
    )
    @NestedCommand(NestedCommands.class)
    public void hungerGames(CommandContext context, CommandSender sender) {
        //TODO implement help
    }

    public static class NestedCommands {
        private List<String> completeRegeneration = new ArrayList<>();
        private final WorldControlModule module;

        public NestedCommands(WorldControlModule module) {
            this.module = module;
        }

        @Command(
                aliases = {"reload"},
                desc = "Reloads the module."
        )
        @CommandPermissions("worldcontrol.reload")
        public void reload(CommandContext context, CommandSender sender) {

            module.reload();
            module.loadAllowedItems();
            module.loadConfig();
            sender.sendMessage(ChatColor.DARK_GREEN + "WorldControl wurde neugeladen.");
        }

        @Command(
                aliases = {"regenerate"},
                desc = "Force a regeneration."
        )
        @CommandPermissions("worldcontrol.regenerate")
        public void regenerate(CommandContext context, CommandSender sender) {

            if(context.argsLength() == 0) {
                sender.sendMessage(ChatColor.DARK_GREEN + "Standardregenerierung wird durchgeführt! " + ChatColor.DARK_RED + "(Laggs möglich)");
                WorldControlModule.INSTANCE.regenerateBlocks();
                return;
            }

            if(context.getString(0).equalsIgnoreCase("all")) {
                sender.sendMessage(ChatColor.GOLD + "Komplettregenerierung bestätigen mit: " + ChatColor.DARK_RED + "/wc regenerate confirm");
                if(!completeRegeneration.contains(sender.getName())) {
                    completeRegeneration.add(sender.getName());
                }
                return;
            }

            if(context.getString(0).equalsIgnoreCase("confirm")) {
                if(!completeRegeneration.contains(sender.getName())) {
                    sender.sendMessage(ChatColor.DARK_RED + "Keine Aktion zum bestätigen gefunden!");
                    return;
                }
                completeRegeneration.remove(sender.getName());

                if(WorldControlModule.INSTANCE.isRegenerationRunning()) {
                    sender.sendMessage(ChatColor.GOLD + "Es läuft derzeit bereits eine Regenerierung!");
                    return;
                }

                sender.sendMessage(ChatColor.DARK_GREEN + "Komplettregenerierung wird durchgeführt!");
                WorldControlModule.INSTANCE.regenerateBlocks(true);
            }

            if(context.getString(0).equalsIgnoreCase("info")) {
                if(WorldControlModule.INSTANCE.isRegenerationRunning()) {
                    sender.sendMessage(ChatColor.DARK_RED + "Es wird zurzeit eine Regenierung durchgeführt!");
                }
                else {
                    sender.sendMessage(ChatColor.DARK_GREEN + "Es findet derzeit keine Regenerierung statt!");
                }
            }
        }
    }
}
