package org.wihy.moderatools;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.ArrayList;
import java.util.List;

import static org.wihy.moderatools.Constants.INVENTORY_STRING;
import static org.wihy.moderatools.Constants.PREFIX;

public class Utilities {

    private Utilities() {}

    public static void sendMessage(final Player player, final String message) {
        player.sendMessage(componentFromText(PREFIX + " &r" +  message));
    }

    public static TextComponent componentFromText(String text) {
        return (TextComponent) LegacyComponentSerializer.legacyAmpersand().deserialize(text).decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE);
    }

    public static String plainFromComponent(Component component) {
        return PlainTextComponentSerializer.plainText().serialize(component);
    }
    public static void b(Object object) {
        if(object.toString().isEmpty()) {
            return;
        }

        Bukkit.broadcast(componentFromText(object.toString()));
    }

    public static ItemStack hideInformation(ItemStack item) {
        ItemMeta meta = item.getItemMeta();

        if(meta == null) {
            return item;
        }

        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        item.setItemMeta(meta);

        return item;
    }

    public static String textFromComponent(Component component) {
        return LegacyComponentSerializer.legacyAmpersand().serialize(component);
    }

    public static ItemStack skull(OfflinePlayer player) {
        ItemStack head = new ItemStack(Material.PLAYER_HEAD);

        SkullMeta meta = (SkullMeta) head.getItemMeta();
        meta.setOwningPlayer(player);
        head.setItemMeta(meta);

        return head;
    }


    public static void setSlot(Inventory inventory, int slot, String itemType, String name, String lore) {
        inventory.setItem(slot, hideInformation(addLore(rename(typeFromName(itemType), name), lore)));
    }

    public static void setSlot(Inventory inventory, int slot, ItemStack item, String name, String lore) {
        inventory.setItem(slot, hideInformation(addLore(rename(item, name), lore)));
    }

    public static void setSlots(Inventory inventory, int[] slots, String itemType, String name) {
        for (int i : slots) {
            inventory.setItem(i, rename(typeFromName(itemType), name));
        }
    }

    public static double parseMessage(String message) {
        double integerPart = Double.parseDouble(message.replaceAll("\\D", ""));
        String stringPart = message.replaceAll("\\d", "").toUpperCase();

        stringPart = "1" + stringPart.replace("S", "")
                .replace("M", "*60")
                .replace("H", "*60*60")
                .replace("D", "*60*60*24")
                .replace("W", "*60*60*24*7")
                .replace("MO", "*60*60*24*7*4")
                .replace("Y", "*60*60*24*7*4*12");

        if(!stringPart.replace("*", "").replaceAll("\\d", "").isEmpty()) {
            return 0;
        }

        double multiplicationValue = integerPart;

        for (String num : stringPart.split("\\*")) {
            multiplicationValue = multiplicationValue*Integer.parseInt(num);
        }

        return Math.abs(multiplicationValue);
    }

    public static void shinify(ItemStack item) {
        ItemMeta meta = item.getItemMeta();

        meta.addEnchant(Enchantment.MENDING, 1, true);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);

        item.setItemMeta(meta);
    }

    public static void shinifyAccordingly(ItemStack item) {
        final boolean isShiny = isShiny(item);

        if(isShiny) {
            unshinify(item);
        } else {
            shinify(item);
        }
    }

    public static boolean isShiny(ItemStack item) {
        ItemMeta meta = item.getItemMeta();

        if(meta == null) {
            return false;
        }

        return meta.hasEnchant(Enchantment.MENDING) && meta.hasItemFlag(ItemFlag.HIDE_ENCHANTS);
    }

    public static void unshinify(ItemStack item) {
        ItemMeta meta = item.getItemMeta();

        meta.removeItemFlags(ItemFlag.HIDE_ENCHANTS);
        meta.removeEnchant(Enchantment.MENDING);

        item.setItemMeta(meta);
    }

    public static int getShinyItem(Inventory inventory) {
        ItemStack item;

        for(int i = 0; i < inventory.getSize(); i++) {
            item = inventory.getItem(i);

            if(item == null) continue;

            if(isShiny(item))
                return i;
        }

        return -1;
    }

    public static boolean hasMetadata(InventoryClickEvent event, String metadataKey) {
        final Player player = (Player) event.getWhoClicked();

        if(!player.hasMetadata(INVENTORY_STRING))
            return false;

        final Object metadataValue = player.getMetadata(INVENTORY_STRING).get(0).value();

        return player.hasMetadata(INVENTORY_STRING) && metadataValue != null && metadataValue.equals(metadataKey + "GUI");
    }

    public static String properCase(String input) {
        if (input == null || input.isEmpty()) {
            return input;
        }

        String[] words = input.split(" ");
        StringBuilder result = new StringBuilder();

        for (String word : words) {
            if (word.length() > 1) {
                result.append(word.substring(0, 1).toUpperCase());
                result.append(word.substring(1).toLowerCase());
            } else {
                result.append(word.toUpperCase());
            }

            result.append(' ');
        }

        return result.toString().trim();
    }


    public static ItemStack addLore(ItemStack item, String lines) {
        if(item == null) return null;

        ItemMeta meta = item.getItemMeta();
        List<Component> lore = meta.lore();

        if(lore == null) lore = new ArrayList<>();

        for(String line : lines.split("~")) {
            lore.add(componentFromText("&r"+line));
        }

        meta.lore(lore);
        item.setItemMeta(meta);

        return item;
    }

    public static List<Component> getLore(ItemStack item) {
        return item.getItemMeta().lore();
    }

    public static void setLore(ItemStack item, int lineNumber, String lineToSet) {
        List<Component> lore = getLore(item);

        if(lore.isEmpty() || lore.size() < lineNumber) return;

        lore.set(lineNumber-1, componentFromText(lineToSet));

        ItemMeta meta = item.getItemMeta();
        meta.lore(lore);

        item.setItemMeta(meta);

    }

    public static ItemStack rename(ItemStack item, String name) {
        ItemMeta meta = item.getItemMeta();

        if(meta == null)
            return item;

        meta.displayName(componentFromText("&r"+name));
        item.setItemMeta(meta);

        return item;
    }
    public static ItemStack typeFromName(String name) {
        return new ItemStack(Material.valueOf(name.toUpperCase().trim().replace(" ", "_")));
    }
}
