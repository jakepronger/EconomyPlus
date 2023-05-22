package me.itswagpvp.economyplus.misc;

import me.itswagpvp.economyplus.listeners.PlayerHandler;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import me.itswagpvp.economyplus.EconomyPlus;
import me.itswagpvp.economyplus.utils.Utils;

import static me.itswagpvp.economyplus.EconomyPlus.getDBType;
import static me.itswagpvp.economyplus.EconomyPlus.plugin;

public class InterestsManager {

    public void startBankInterests() {

        long time = plugin.getConfig().getLong("Bank.Interests.Time", 300) * 20L;
        int interest = plugin.getConfig().getInt("Bank.Interests.Percentage", 10);

        Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, () -> {

            for (String player : EconomyPlus.getDBType().getList()) {

                OfflinePlayer p = PlayerHandler.getPlayer(player);
                if (p == null) {
                    continue;
                }

                if (plugin.getConfig().getBoolean("Bank.Interests.Online-Player", true)) {
                    if (!p.isOnline()) continue;
                }

                // Save the new bank in the cache and then in the db
                EconomyPlus.getDBType().setBank(player, getDBType().getBank(player) * (100 + interest) / 10);

                if (p.getPlayer() != null && p.isOnline()) {
                    p.getPlayer().sendMessage(plugin.getMessage("Bank.Interests").replaceAll("%percentage%", "" + interest));
                    Utils.playSuccessSound(p.getPlayer());
                }

            }

        }, time, time);
    }
}
