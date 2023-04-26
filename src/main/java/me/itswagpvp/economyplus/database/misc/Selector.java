package me.itswagpvp.economyplus.database.misc;

import me.itswagpvp.economyplus.EconomyPlus;

import org.bukkit.OfflinePlayer;

public class Selector {

    public static String playerToString(OfflinePlayer player) {

        if (EconomyPlus.getStorageMode() == StorageMode.NICKNAME) {
            return player.getName();
        }

        else {
            return player.getUniqueId().toString();
        }

    }

}
