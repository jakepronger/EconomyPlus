package me.itswagpvp.economyplus.utils;

import me.itswagpvp.economyplus.EconomyPlus;
import me.itswagpvp.economyplus.database.misc.DatabaseType;
import me.itswagpvp.economyplus.misc.StorageManager;

import org.bukkit.command.CommandSender;

import static me.itswagpvp.economyplus.EconomyPlus.plugin;
import static me.itswagpvp.economyplus.messages.Messages.messages;
import static me.itswagpvp.economyplus.utils.Utils.utils;

public class Reload {

    public static Reload reload = new Reload();

    public void execute(CommandSender p) {

        long delay = System.currentTimeMillis();

        utils.log("[EconomyPlus] &aReloading the plugin! This action may take a while!");

        try {

            if (EconomyPlus.getDBType() == DatabaseType.YAML) {
                plugin.createYMLStorage();
            }

            plugin.saveDefaultConfig();
            plugin.reloadConfig();

            new StorageManager().createStorageConfig();

            messages.load();

        } catch (Exception e) {
            p.sendMessage("§cError on reloading the plugin! (" + e.getMessage() + ")");
        } finally {
            p.sendMessage(plugin.getMessage("Reload")
                    .replaceAll("%time%", String.valueOf(System.currentTimeMillis() - delay)));
        }

        utils.log("&aReloaded!");

    }

}
