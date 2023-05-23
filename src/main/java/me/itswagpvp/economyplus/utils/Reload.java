package me.itswagpvp.economyplus.utils;

import me.itswagpvp.economyplus.EconomyPlus;
import me.itswagpvp.economyplus.database.misc.DatabaseType;
import me.itswagpvp.economyplus.misc.StorageManager;

import org.bukkit.command.CommandSender;

import static me.itswagpvp.economyplus.EconomyPlus.plugin;
import static me.itswagpvp.economyplus.managers.ConfigManager.configManager;
import static me.itswagpvp.economyplus.utils.Config.config;
import static me.itswagpvp.economyplus.utils.Utils.utils;

public class Reload {

    public static Reload reload = new Reload();

    public void execute(CommandSender sender) {

        long delay = System.currentTimeMillis();

        utils.log("&aReloading the plugin! This action may take a while!");

        try {

            if (EconomyPlus.getDBType() == DatabaseType.YAML) {
                plugin.createYMLStorage();
            }

            new StorageManager().createStorageConfig();

        } catch (Exception e) {
            sender.sendMessage("§cError on reloading the plugin! (" + e.getMessage() + ")");
        } finally {
            sender.sendMessage(plugin.getMessage("Reload")
                    .replaceAll("%time%", String.valueOf(System.currentTimeMillis() - delay)));
        }

        configManager.load();

        // languageManager

        // initialize plugin vars
        plugin.basicperms = config.getBoolean("require-basic-permissions", true);
        plugin.updater = config.getBoolean("updater.use", true);
        plugin.debug = config.getBoolean("debug", false);
        plugin.bank = config.getBoolean("bank.use", true);

        utils.log("&aReloaded!");

    }

}
