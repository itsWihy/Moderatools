package org.wihy.moderatools.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import static org.wihy.moderatools.functions.PunishGUIFunctions.openPlayerGUI;
import static org.wihy.moderatools.functions.PunishGUIFunctions.openPunishGUI;

public class PunishCommand implements CommandExecutor {
    private final Plugin plugin;

    public PunishCommand(Plugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if(!(sender instanceof Player)) return false;
        if("punish".compareTo(command.getName()) != 0) return false;

        if(strings.length < 1) {
            openPunishGUI(((Player) sender).getPlayer(), 2, plugin);
            return true;
        }

        final Player player = Bukkit.getPlayer(strings[0]);

        if(player == null) return false;

        openPlayerGUI(((Player) sender), player, plugin);

        return true;
    }
}
