package de.raidcraft.worldcontrol.commands;

import com.sk89q.minecraft.util.commands.*;
import de.raidcraft.RaidCraft;
import de.raidcraft.worldcontrol.WorldControlPlugin;
import de.raidcraft.worldcontrol.regeneration.RegenerationManager;
import de.raidcraft.worldcontrol.tables.BlockLogsTable;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;

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

            RaidCraft.getComponent(WorldControlPlugin.class).reload();
            sender.sendMessage(ChatColor.DARK_GREEN + "WorldControl wurde neugeladen.");
        }

        @Command(
                aliases = {"regenerate", "regen", "reg"},
                desc = "Force a regeneration.",
                min = 2
        )
        @CommandPermissions("worldcontrol.regenerate")
        @NestedCommand(NestedRegenerateCommands.class)
        public void regenerate(CommandContext context, CommandSender sender) throws CommandException {
        }

        @Command(
                aliases = {"optimize", "opti"},
                desc = "Database optimization."
        )
        @CommandPermissions("worldcontrol.regenerate")
        public void optimize(CommandContext context, CommandSender sender) {

            RaidCraft.getTable(BlockLogsTable.class).otimizeTable();
            sender.sendMessage(ChatColor.GREEN + " Die Datenbank wurde optimiert!");
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

    public static class NestedRegenerateCommands {

        private final WorldControlPlugin module;

        public NestedRegenerateCommands(WorldControlPlugin module) {

            this.module = module;
        }

        @Command(
                aliases = {"all"},
                desc = "Regenerate all",
                min = 1
        )
        public void all(CommandContext context, CommandSender sender) throws CommandException {

            WorldControlPlugin plugin = RaidCraft.getComponent(WorldControlPlugin.class);
            RegenerationManager regenerationManager = plugin.getRegenerationManager();
            String world = context.getString(0);

            if (regenerationManager.isRegenerationRunning()) {
                sender.sendMessage(ChatColor.GOLD + "Es läuft derzeit bereits eine Regenerierung!");
                return;
            }

            if(!plugin.getRegenerationManager().regenerate(world, new Location(null, 0,0,0), 20000, true)) {
                throw new CommandException("Die Regenerierung konnt nicht gestaret werden! Falsche Welt?");
            }
            sender.sendMessage(ChatColor.DARK_GREEN + "Komplettregenerierung wird durchgeführt!");
        }

        @Command(
                aliases = {"default", "normal"},
                desc = "Regenerate normal",
                min = 1
        )
        public void normal(CommandContext context, CommandSender sender) throws CommandException {

            WorldControlPlugin plugin = RaidCraft.getComponent(WorldControlPlugin.class);
            RegenerationManager regenerationManager = plugin.getRegenerationManager();
            String world = context.getString(0);

            if (regenerationManager.isRegenerationRunning()) {
                sender.sendMessage(ChatColor.GOLD + "Es läuft derzeit bereits eine Regenerierung!");
                return;
            }

            if(!plugin.getRegenerationManager().regenerate(world, new Location(null, 0,0,0), 20000, false)) {
                throw new CommandException("Die Regenerierung konnt nicht gestaret werden! Falsche Welt?");
            }
            sender.sendMessage(ChatColor.DARK_GREEN + "Default-Regenerierung wird durchgeführt!");
        }
    }
}
