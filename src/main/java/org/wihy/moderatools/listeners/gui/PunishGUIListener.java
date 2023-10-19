package org.wihy.moderatools.listeners.gui;

import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.util.Objects;

import static org.wihy.moderatools.Constants.INVENTORY_STRING;
import static org.wihy.moderatools.Utilities.hasMetadata;
import static org.wihy.moderatools.Utilities.textFromComponent;
import static org.wihy.moderatools.functions.PunishGUIFunctions.openPlayerGUI;
import static org.wihy.moderatools.functions.PunishGUIFunctions.openPunishGUI;

public class PunishGUIListener implements Listener {
    private final Plugin plugin;

    public PunishGUIListener(Plugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        final Player player = (Player) event.getPlayer();

        if(player.hasMetadata(INVENTORY_STRING)) {
            player.removeMetadata(INVENTORY_STRING, plugin);
        }
    }
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        final Player player = (Player) event.getWhoClicked();
        final String title = textFromComponent(event.getWhoClicked().getOpenInventory().title());
        final ItemStack item = event.getCurrentItem();
        final int slot = event.getRawSlot();

        if(item == null)
            return;

        String name = (PlainTextComponentSerializer.plainText().serialize(item.displayName()));
        name = name.replace("[", "").replace("]", "");

        if(hasMetadata(event, "punish")) {
            punishGUIExecution(event, player, title, name, slot, item);
        }
    }

    private void punishGUIExecution(InventoryClickEvent event, Player player, String title, String name, int slot, ItemStack item) {
        event.setCancelled(true);

        int pageToOpen = Integer.parseInt(title.split(" ")[2]);

        if(slot == 48) pageToOpen--;
        if(slot == 50) pageToOpen++;

        if(item.getType() == Material.ARROW) {
            openPunishGUI(player, pageToOpen, plugin);
        }

        if(item.getType() == Material.PLAYER_HEAD) {
            openPlayerGUI(player, Objects.requireNonNull(Bukkit.getPlayer(name)), plugin);
        }
    }
}
