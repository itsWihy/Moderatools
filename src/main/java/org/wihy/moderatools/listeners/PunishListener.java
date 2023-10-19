package org.wihy.moderatools.listeners;

import io.papermc.paper.event.player.AsyncChatEvent;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;

public class PunishListener implements Listener {
    private final Plugin plugin;

    public PunishListener(Plugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onChat(AsyncChatEvent event) {
        Player player = event.getPlayer();
        PersistentDataContainer container = player.getPersistentDataContainer();

        if(!container.has(new NamespacedKey(plugin, "muted"))) return;

        String value = container.get(new NamespacedKey(plugin, "muted"), PersistentDataType.STRING);

        if(value == null || value.isEmpty()) return;

        String reason = value.split("~")[0];
        double duration = Double.parseDouble(value.split("~")[1]);

        event.setCancelled(true);
        player.sendMessage("You have been muted for " + reason + " until " + duration + "!");
    }
}
