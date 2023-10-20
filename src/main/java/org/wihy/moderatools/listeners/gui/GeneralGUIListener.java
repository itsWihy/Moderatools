package org.wihy.moderatools.listeners.gui;

import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
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
import org.wihy.moderatools.functions.PunishFunctions;

import java.util.Objects;

import static org.wihy.moderatools.Utilities.*;
import static org.wihy.moderatools.functions.PunishGUIFunctions.*;

public class GeneralGUIListener implements Listener {
    private final Plugin plugin;

    public GeneralGUIListener(Plugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        final Player player = (Player) event.getWhoClicked();
        final String title = textFromComponent(event.getWhoClicked().getOpenInventory().title());
        final ItemStack item = event.getCurrentItem();
        final int slot = event.getRawSlot();
        final Inventory inventory = event.getInventory();

        if(item == null)
            return;

        final Player toPunish = Bukkit.getPlayer(title.split(" ")[1].trim());
        final String type = (title.split(" ")[0].trim());

        if(toPunish == null || !toPunish.isOnline()) return;

        if(hasMetadata(event, "any") || hasMetadata(event, "player")) {
            playerGUIExecution(event, type, slot, item, player, toPunish, inventory);
        }
    }

    private boolean playerGUIExecution(InventoryClickEvent event, Player player, int slot, Player toPunish, ItemStack item) {
        if(!hasMetadata(event, "player")) return false;

        String name = (PlainTextComponentSerializer.plainText().serialize(item.displayName()))
                .replace(" Player", "")
                .replace("[", "")
                .replace("]", "");

        if (slot < 26 && slot > 18 && slot != 22)
            openAnyGUI(player, toPunish, name, plugin, 0, "");

        if(slot == 49)
            openPunishGUI(player, 1, plugin);

        return true;
    }

    private void executePunishment(Player player, Player toPunish, Inventory inventory, String type) {
        execute(player, toPunish, type, inventory);
        player.closeInventory();

        sendMessage(player, "Punishment &a" + type + "&f successfully implemented against &b" + toPunish.getName());
    }

    private void enterCustomReason(Player player, Player toPunish, String type) {
        sendMessage(player, "Enter a reason for the punishment &ain chat");
        player.setMetadata("setReason", new FixedMetadataValue(plugin, toPunish.getName() + "~" + type));

        player.closeInventory();
    }

    private void toggleIPBan(String type, ItemStack item) {
        if("ban".compareToIgnoreCase(type.trim()) != 0) return;

        shinifyAccordingly(item);
        setLore(item, 3, "&b" + properCase(String.valueOf(isShiny(item))));
    }

    private void toggleBroadcast(ItemStack item) {
        shinifyAccordingly(item);
        setLore(item, 6, "&b" + properCase(String.valueOf((isShiny(item)))));
    }

    private void goBack(Player player, Player toPunish, Inventory inventory) {
        ItemStack item = inventory.getItem(49);

        if(item == null)
            return;

        String text = textFromComponent(getLore(item).get(1));

        if(!text.contains(("viewing")))
            return;

        openPlayerGUI(player, toPunish, plugin);
    }

    private void selectReasonItem(Inventory inventory, int slot, ItemStack item) {
        int shinyItemIndex = getShinyItem(inventory);

        ItemStack selectedShiny = new ItemStack(Material.DIRT);
        ItemStack selected = inventory.getItem(slot);

        if(shinyItemIndex != -1) {
            selectedShiny = inventory.getItem(shinyItemIndex);
        }

        if(selected != null) shinify(item);

        if (selectedShiny != null && shinyItemIndex != -1) {
            unshinify(selectedShiny);
        }
    }

    private void playerGUIExecution(InventoryClickEvent event, String type, int slot, ItemStack item, Player player, Player toPunish, Inventory inventory) {
        event.setCancelled(true);

        if(playerGUIExecution(event, player, slot, toPunish, item))
            return;

        switch (slot) {
            case 19 -> executePunishment(player, toPunish, inventory, type);
            case 22, 23, 21-> selectReasonItem(inventory, slot, item);
            case 25 -> enterCustomReason(player, toPunish, type);
            case 45 -> toggleBroadcast(item);
            case 49 -> goBack(player, toPunish, inventory);
            case 53 -> toggleIPBan(type, item);

            case 31 -> openClockGUI(player, toPunish, type, plugin, 0);

            default -> b("");
        }
    }

    private void execute(Player player, Player toPunish, String type, Inventory inventory) {
        PunishFunctions punishFunctions = new PunishFunctions(plugin);

        boolean isIPBan = isShiny(Objects.requireNonNull(inventory.getItem(53)));
        boolean shouldBroadcast = isShiny(Objects.requireNonNull(inventory.getItem(45)));

        double duration = 0;
        ItemStack clockItem = inventory.getItem(31);

        if(clockItem != null) {
            duration = Double.parseDouble(plainFromComponent(getLore(clockItem).get(2)));
        }

        String reason = "";

        ItemStack iteratedItem;

        for(int i = 0; i < 6; i++) {
            iteratedItem = inventory.getItem(21+i);

            if(iteratedItem == null || !isShiny(iteratedItem))
                continue;

            reason = plainFromComponent(getLore(iteratedItem).get(1));
        }

        switch (type) {
            case "ban" -> punishFunctions.banPlayer(player, toPunish, reason, (duration), isIPBan, shouldBroadcast);
            case "mute" -> punishFunctions.mutePlayer(player, toPunish, reason, (duration), shouldBroadcast);
            case "warn" -> punishFunctions.warnPlayer(player, toPunish, reason, shouldBroadcast);
            case "kick" -> punishFunctions.kickPlayer(player, toPunish, reason, shouldBroadcast);
            default -> sendMessage(player, "Invalid Punishment Type!");
        }
    }
}
