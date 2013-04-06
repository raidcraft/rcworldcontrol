package de.raidcraft.worldcontrol.commands;

import com.sk89q.minecraft.util.commands.Command;
import com.sk89q.minecraft.util.commands.CommandContext;
import com.sk89q.minecraft.util.commands.CommandPermissions;
import com.sk89q.minecraft.util.commands.NestedCommand;
import de.raidcraft.RaidCraft;
import de.raidcraft.worldcontrol.Regeneration;
import de.raidcraft.worldcontrol.WorldControlPlugin;
import de.raidcraft.worldcontrol.util.WCLogger;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;

/**
 * Author: Philip
 * Date: 04.11.12 - 15:31
 * Description:
 */
public class Commands {

    public Commands(WorldControlPlugin module) {

    }

    @Command(
            aliases = {"worldcontrol", "wc"},
            desc = "Manage world control module."
    )
    @NestedCommand(NestedCommands.class)
    public void worldControl(CommandContext context, CommandSender sender) {
        //TODO implement help
    }

    public static class NestedCommands {

        private List<String> completeRegeneration = new ArrayList<>();
        private final WorldControlPlugin module;

        public NestedCommands(WorldControlPlugin module) {

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
                aliases = {"regenerate", "regen", "reg"},
                desc = "Force a regeneration.",
                min = 1
        )
        @CommandPermissions("worldcontrol.regenerate")
        public void regenerate(CommandContext context, CommandSender sender) {

            if (context.getString(0).equalsIgnoreCase("default")) {
                sender.sendMessage(ChatColor.DARK_GREEN + "Standardregenerierung wurde gestartet! " + ChatColor.DARK_RED + "(Lags möglich)");
                WCLogger.info("Standardregenerierung wird durchgeführt! " + ChatColor.DARK_RED + "(Lags möglich)");
                Regeneration.INSTANCE.regenerateBlocks();
                return;
            }

            if (context.getString(0).equalsIgnoreCase("all")) {
                sender.sendMessage(ChatColor.GOLD + "Komplettregenerierung bestätigen mit: " + ChatColor.DARK_RED + "/wc regenerate confirm");
                if (!completeRegeneration.contains(sender.getName())) {
                    completeRegeneration.add(sender.getName());
                }
                return;
            }

            if (context.getString(0).equalsIgnoreCase("confirm")) {
                if (!completeRegeneration.contains(sender.getName())) {
                    sender.sendMessage(ChatColor.DARK_RED + "Keine Aktion zum bestätigen gefunden!");
                    return;
                }
                completeRegeneration.remove(sender.getName());

                if (!Regeneration.INSTANCE.canRegenerate()) {
                    sender.sendMessage(ChatColor.GOLD + "Es läuft derzeit bereits eine Regenerierung!");
                    return;
                }

                sender.sendMessage(ChatColor.DARK_GREEN + "Komplettregenerierung wird durchgeführt!");
                Regeneration.INSTANCE.regenerateBlocks(true);
                return;
            }

            if (context.getString(0).equalsIgnoreCase("info")) {
                if (!Regeneration.INSTANCE.canRegenerate()) {
                    sender.sendMessage(ChatColor.DARK_RED + "Es wird zurzeit eine Regenierung durchgeführt!");
                } else {
                    sender.sendMessage(ChatColor.DARK_GREEN + "Es findet derzeit keine Regenerierung statt!");
                }
                return;
            }

            int radius = context.getInteger(0);
            //TODO radius regeneration
        }

        @Command(
                aliases = {"physics", "phy"},
                desc = "Toggle world physics."
        )
        @CommandPermissions("worldcontrol.regenerate")
        public void physics(CommandContext context, CommandSender sender) {

            if (context.getString(0).equalsIgnoreCase("on")) {
                RaidCraft.getComponent(WorldControlPlugin.class).allowPhysics = true;
                sender.sendMessage(ChatColor.DARK_GREEN + "Blockphysik eingeschaltet!");
                return;
            }

            if (context.getString(0).equalsIgnoreCase("off")) {
                RaidCraft.getComponent(WorldControlPlugin.class).allowPhysics = false;
                sender.sendMessage(ChatColor.DARK_GREEN + "Blockphysik ausgeschaltet!");
                return;
            }

            sender.sendMessage(ChatColor.DARK_RED + "Als Parameter entweder 'on' oder 'off'!");
        }
    }
}
