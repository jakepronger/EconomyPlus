package me.itswagpvp.economyplus.managers;

import static me.itswagpvp.economyplus.EconomyPlus.plugin;

public class ConfigManager {

    public static ConfigManager configManager = new ConfigManager();

    public void load() {
        plugin.saveDefaultConfig(); // creates config.yml if it doesn't exist
        plugin.saveConfig(); // saves config to the file
        plugin.reloadConfig(); // updates the config in memory
    }

}
