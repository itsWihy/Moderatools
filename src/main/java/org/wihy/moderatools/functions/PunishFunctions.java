package org.wihy.moderatools.functions;

import org.bukkit.NamespacedKey;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;

import java.util.Date;

import static org.wihy.moderatools.Constants.MUTE_KEY;
import static org.wihy.moderatools.Utilities.*;

public class PunishFunctions {
    private final Plugin plugin;

    public PunishFunctions(Plugin plugin) {
        this.plugin = plugin;
    }

    private Date getDateFromDuration(double duration) {
        final Date date = new Date();
        return new Date((long) (date.getTime() + duration*1000L));
    }

    private void broadcast(OfflinePlayer player, OfflinePlayer punished, String type) {
        b("&6&l&m                             \n\n");
        b("&f" + punished.getName() + " &bhas been &f" + type + "&b by &f" + player.getName());
        b("\n\n&6&l&m                             ");
    }

    public void banPlayer(Player sender, OfflinePlayer player, String reason, double duration, boolean isBanIp, boolean shouldBroadcast) {
        if(!isBanIp) {
            if (shouldBroadcast) broadcast(sender, player, "banned");

            player.banPlayer(reason, getDateFromDuration(duration), "god", true);
            sendMessage(sender, "Player &b" + player.getName() + " has been banned!");
            return;
        }

        if(!player.isOnline()) {
            sendMessage(sender, "To IP ban a player must be online!");
            return;
        }

        if (shouldBroadcast) broadcast(sender, player, "IP-banned");

        ((Player)player).banIp(reason, getDateFromDuration(duration), "god", true);
        sendMessage(sender, "Player &b" + player.getName() + " has been IP-banned!");
    }

    public void mutePlayer(Player sender, Player player, String reason, double duration, boolean shouldBroadcast) {
        if(!player.isOnline()) return;

        sendMessage(player, "You have been &b" + MUTE_KEY + "&f until &6" + getDateFromDuration(duration) + "\n&7" + reason);
        PersistentDataContainer container = player.getPersistentDataContainer();
        container.set(new NamespacedKey(plugin, MUTE_KEY), PersistentDataType.STRING, reason + "~" + getDateFromDuration(duration).getTime());

        if (shouldBroadcast) broadcast(sender, player, MUTE_KEY);

    }

    public boolean unmutePlayer(Player sender, Player player) {
        if(!player.isOnline()) return false;

        PersistentDataContainer container = player.getPersistentDataContainer();
        container.remove(new NamespacedKey(plugin, "muted"));

        sendMessage(player, "You have been &bunmuted by &a" + sender + "&f!");
        return true;
    }

    public void kickPlayer(Player sender, Player player, String reason, boolean shouldBroadcast) {
        if(!player.isOnline()) return;

        sendMessage(player, "You have been &bkicked &fby &a" + sender + "&f!");
        player.kick(componentFromText(reason));

        if (shouldBroadcast)
            broadcast(sender, player, "kicked");
    }

    public void warnPlayer(Player sender, Player player, String reason, boolean shouldBroadcast) {
        if(!player.isOnline())
            return;

        sendMessage(player, "You have been &bwarned by &a" + sender + "\n&7" + reason);

        if (shouldBroadcast) broadcast(sender, player, "warned");
    }
}
