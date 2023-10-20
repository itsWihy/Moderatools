package org.wihy.moderatools.listeners.gui;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.Plugin;

import static org.wihy.moderatools.Utilities.*;
import static org.wihy.moderatools.functions.PunishGUIFunctions.openAnyGUI;

public class ClockGUIListener implements Listener {
    private final Plugin plugin;

    public ClockGUIListener(Plugin plugin) {
        this.plugin = plugin;
    }
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        final Player player = (Player) event.getWhoClicked();
        final String title = textFromComponent(event.getWhoClicked().getOpenInventory().title());
        final ItemStack item = event.getCurrentItem();

        if(item == null)
            return;

        final Player toPunish = Bukkit.getPlayer(title.split(" ")[1].trim());
        final String type = title.split(" ")[0].trim();

        if(toPunish == null || !toPunish.isOnline()) return;

        if(hasMetadata(event, "clock")) {
            clockGUIExecution(event, player, event.getRawSlot(), item, event.getInventory(), toPunish, type);
        }
    }

    private void handleSignClick(int slot, ItemStack item, Player player, Player toPunish, String type) {
        if(slot != 31 || item.getType() != Material.OAK_SIGN) return;

        sendMessage(player, "Enter a duration in chat. &6Format &bNumber(>0)Unit(S/M/H/D/W/MO/Y) &fExample -> 5D");
        player.setMetadata("setDuration", new FixedMetadataValue(plugin, toPunish.getName() + "~" + type));

        player.closeInventory();
    }

    private void handleArrowClick(int slot, ItemStack item, Player player, Player toPunish, String type, double duration) {
        if(slot == 49 && item.getType() == Material.ARROW) {
            String reason = "";

            if(player.hasMetadata("reason"))
                reason = player.getMetadata("reason").get(0).asString();

            player.setMetadata("duration", new FixedMetadataValue(plugin, duration));

            final String finalReason = reason;
            final double finalDuration = duration;

            Bukkit.getScheduler().runTaskLater(plugin, () -> openAnyGUI(player, toPunish, type, plugin, finalDuration, finalReason), 2L);
        }
    }

    private void clockGUIExecution(InventoryClickEvent event, Player player, int slot, ItemStack item, Inventory inventory, Player toPunish, String type) {
        event.setCancelled(true);

        handleSignClick(slot, item, player, toPunish, type);

        double duration = 0;
        int shinyItemIndex = getShinyItem(inventory);

        ItemStack selectedShiny = new ItemStack(Material.DIRT);
        ItemStack selected = inventory.getItem(slot);

        if(shinyItemIndex != -1)
            selectedShiny = inventory.getItem(shinyItemIndex);

        if(selected != null && slot > 18 && slot < 26)
            shinify(item);

        if (selectedShiny != null && shinyItemIndex != -1 && shinyItemIndex != 25) {
            unshinify(selectedShiny);
            duration = parseMessage(plainFromComponent(getLore(selectedShiny).get(3)));
        }

        if(shinyItemIndex == 25) {
            duration = 999999999;
        }

        handleArrowClick(slot, item, player, toPunish, type, duration);
    }
}
