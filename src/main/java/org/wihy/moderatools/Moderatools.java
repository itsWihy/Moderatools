package org.wihy.moderatools;

import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.wihy.moderatools.commands.PunishCommand;
import org.wihy.moderatools.listeners.ChatListener;
import org.wihy.moderatools.listeners.PunishListener;
import org.wihy.moderatools.listeners.gui.ClockGUIListener;
import org.wihy.moderatools.listeners.gui.GeneralGUIListener;
import org.wihy.moderatools.listeners.gui.PunishGUIListener;

import java.util.List;
import java.util.Objects;

public final class Moderatools extends JavaPlugin {
    @Override
    public void onEnable() {
        List<Listener> listeners = List.of(new ClockGUIListener(this), new PunishGUIListener(this), new PunishListener(this), new GeneralGUIListener(this), new ChatListener(this));

        for(Listener listener : listeners) {
            getServer().getPluginManager().registerEvents(listener, this);
        }

        Objects.requireNonNull(getCommand("punish")).setExecutor(new PunishCommand(this));
    }
}

//todo: Update Checker. History. Command for every action. Unban.
//todo: Config. Permissions.