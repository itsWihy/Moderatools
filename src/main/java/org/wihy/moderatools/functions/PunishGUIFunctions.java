package org.wihy.moderatools.functions;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.Plugin;

import java.util.List;
import java.util.Objects;

import static org.wihy.moderatools.Constants.*;
import static org.wihy.moderatools.Utilities.*;

public final class PunishGUIFunctions {
    private PunishGUIFunctions() {}
    public static void openPunishGUI(Player player, int page, Plugin plugin) {
        List<? extends Player> list = Bukkit.getOnlinePlayers().stream().toList();

        Inventory inventory = baseGUI("Punish GUI " +page);

        int slot = 0;
        int index;

        for(Player online : list) {
            index = list.indexOf(online);

            if(index >= 45*(page-1) && index < 45*(page+1)) {
                setSlot(inventory, slot, skull(online), "&b"+online.getName(), "~&7Click to open Punish GUI~~&eClick to execute");
                slot++;
            }
        }

        if(page > 1)
            setSlot(inventory, 48, ARROW_STRING, "&bPrevious Page", "~&7Click to go to the~&7Previous Page~~&eClick to execute");
        if(list.size() > 45*page)
            setSlot(inventory, 50, ARROW_STRING, "&bNext Page", "~&7Click to go to the~&7Next Page~~&eClick to execute");

        player.openInventory(inventory);
        player.setMetadata(INVENTORY_STRING, new FixedMetadataValue(plugin, "punishGUI"));
    }

    public static void openPlayerGUI(Player openTo, Player player, Plugin plugin) {
        Inventory inventory = baseGUI("Punish " + player.getName());

        setSlot(inventory, 13, skull(player), "&b"+player.getName(), "&r&7This is the player you're punishing");

        setSlot(inventory, 19, "diamond sword", "&bBan Player", EXECUTE_LORE);
        setSlot(inventory, 20, "writable book", "&bMute Player", EXECUTE_LORE);

        setSlot(inventory, 22, "ancient debris", "&bPunishment History", "~&c&lCOMING SOON");
        setSlot(inventory, 24, "diamond boots", "&bKick Player", EXECUTE_LORE);
        setSlot(inventory, 25, "flint and steel", "&bWarn Player", EXECUTE_LORE);

        setSlot(inventory, 49, ARROW_STRING, BACK_LORE, "~&7Click to go back to viewing all players");

        openTo.openInventory(inventory);
        openTo.setMetadata(INVENTORY_STRING, new FixedMetadataValue(plugin, "playerGUI"));
    }

    public static void openAnyGUI(Player sender, Player player, String type, Plugin plugin, double duration, String reason) {
        String cleanType = type.toLowerCase().trim();

        Inventory inventory = openAnyGUI(sender, player, cleanType, plugin, reason);

        if("ban".compareTo(cleanType) == 0 || "mute".compareTo(cleanType) == 0) {
            setSlot(inventory, 31, "clock", "&bDuration", "~&7Click to enter a duration~&e" + duration + "~&bIn seconds");
        }

        if("ban".compareTo(cleanType) == 0)
            setSlot(inventory, 53, "ender eye", "&bIP Ban", "~&7Click to change! Currently set to: ~&bfalse");

        if(!reason.isEmpty()) shinify(Objects.requireNonNull(inventory.getItem(25)));
    }

    public static Inventory openAnyGUI(Player sender, Player player, String type, Plugin plugin, String reason) {
        Inventory inventory = baseGUI(type + " " + player.getName());

        setSlot(inventory, 13, skull(player), "&b"+player.getName(), "&r&7This is the player you're punishing");

        setSlot(inventory, 19, "emerald", "Execute", "~&7Click to " + type + " the player");

        setSlot(inventory, 21, "red wool", "&bReason Preset 1", "~&7Cheating is forbidden~");
        setSlot(inventory, 22, "lime wool", "&bReason Preset 2", "~&7You broke the rules~");
        setSlot(inventory, 23, "blue wool", "&bReason Preset 3", "~&7Harmfulness or hate is discouraged.");
        setSlot(inventory, 25, "oak sign", "&bCustom Reason", "&7Current reason~&b"+ reason + "~~&7Click to enter a custom reason");

        setSlot(inventory, 45, "observer", "&bBroadcast", "~&7Click to choose whether the message~&7should be broadcasted to all players~~&7Currently set to~&bFalse");

        setSlot(inventory, 49, ARROW_STRING, BACK_LORE, "~&7Click to go back to viewing all players");

        sender.openInventory(inventory);
        sender.setMetadata(INVENTORY_STRING, new FixedMetadataValue(plugin, "anyGUI"));

        return inventory;
    }

    public static void openClockGUI(Player sender, Player player, String type, Plugin plugin, double seconds) {
        Inventory inventory = baseGUI( type + " " + player.getName());

        setSlot(inventory, 13, "clock", "&bDuration", "~&7Click the buttons below to enter a duration");

        List<String> colouredItems = List.of("red", "orange", "yellow", "pink", "magenta", "purple", "black");
        List<String> correspondingTime = List.of("5M", "15M", "1H", "3H", "1D", "1W", "FOREVER");

        for(int i = 0; i < 7; i++) {
            setSlot(inventory, 19+i, colouredItems.get(i) + " concrete powder", "&b" + type + " for " + correspondingTime.get(i), "&7Click to set the duration of the &b" + type + " to~&7(In seconds)~~&6" + correspondingTime.get(i) + "~~&bClick to select");
        }

        setSlot(inventory, 31, "oak sign", "&bCustom Duration", "~&7Click to enter a custom duration~&7Currently set to: ~&b" + seconds + "~~&bClick to change");

        setSlot(inventory, 49, ARROW_STRING, BACK_LORE, "~&7Click to go back to viewing all punishments for this player");

        sender.openInventory(inventory);
        sender.setMetadata(INVENTORY_STRING, new FixedMetadataValue(plugin, "clockGUI"));

        if(seconds != 0) {
            shinify(Objects.requireNonNull(inventory.getItem(31)));
        }
    }

    public static Inventory baseGUI(String name) {
        Inventory inventory = Bukkit.createInventory(null, 54, componentFromText(name));
        setSlots(inventory, new int[]{45, 46, 47, 48, 49, 50, 51, 52, 53}, "black stained glass pane", "&b");

        return inventory;
    }
}
