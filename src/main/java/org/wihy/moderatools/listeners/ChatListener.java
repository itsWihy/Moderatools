package org.wihy.moderatools.listeners;

import io.papermc.paper.event.player.AsyncChatEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.Plugin;

import static org.wihy.moderatools.Constants.DURATION_STRING;
import static org.wihy.moderatools.Constants.REASON_STRING;
import static org.wihy.moderatools.Utilities.*;
import static org.wihy.moderatools.functions.PunishGUIFunctions.*;

public class ChatListener implements Listener {

    private final Plugin plugin;

    public ChatListener(Plugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onChat(AsyncChatEvent event) {
        Player player = event.getPlayer();

        if(!player.hasMetadata(DURATION_STRING) && !player.hasMetadata(REASON_STRING))
            return;

        event.setCancelled(true);

        String metadataType = DURATION_STRING;

        if(player.hasMetadata(REASON_STRING)) metadataType = REASON_STRING;

        final String value = player.getMetadata(metadataType).get(0).asString();

        final String type = value.split("~")[1];
        final Player toPunish = Bukkit.getPlayer(value.split("~")[0]);
        final String message = textFromComponent(event.message());

        if(metadataType.equals(REASON_STRING))   setReason(message, player, toPunish, type);
        if(metadataType.equals(DURATION_STRING)) setDuration(message, player, toPunish, type);

        player.removeMetadata(metadataType, plugin);
    }

    private void setDuration(String message, Player player, Player toPunish, String type) {
        final double seconds = parseMessage(message);

        if (toPunish != null) {
            Bukkit.getScheduler().runTask(plugin, () -> openClockGUI(player, toPunish, type, plugin, seconds));
        }
    }

    private void setReason(String message, Player player, Player toPunish, String type) {
        String newMessage = message.replace("[", "").replace("]", "");

        if (toPunish != null) {
            double seconds = 0;

            if(player.hasMetadata("duration"))
                seconds = player.getMetadata("duration").get(0).asDouble();

            final double finalSeconds = seconds;
            final String finalMessage = newMessage;

            Bukkit.getScheduler().runTask(plugin, () -> {
                openAnyGUI(player, toPunish, type, plugin, finalSeconds, finalMessage);
                player.setMetadata("reason", new FixedMetadataValue(plugin, finalMessage));
            });
        }
    }
}
