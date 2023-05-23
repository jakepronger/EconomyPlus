package me.itswagpvp.economyplus.utils;

import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;

import static me.itswagpvp.economyplus.EconomyPlus.plugin;
import static me.itswagpvp.economyplus.utils.Utils.utils;

public class Config {

    public static Config config = new Config();

    static { // make sure the file exists before continuing
        plugin.saveDefaultConfig();
    }

    public FileConfiguration getConfig() { // get proper config instance
        return plugin.getConfig();
    }

    public double getVersion() {
        return Double.parseDouble(get("version", 0).toString());
    }

    public String getString(String path, String def) {
        return get(path, def).toString();
    }

    public boolean getBoolean(String path, boolean def) {
        return Boolean.parseBoolean(get(path, def).toString());
    }

    public Sound getSound(String path, Sound def) {

        String string = get(path, def).toString();

        Sound sound;
        try {
            sound = Sound.valueOf(string);
        } catch (IllegalArgumentException e) {
            utils.log("&cError parsing sound: " + string);
            utils.log("&cUsing default: " + def);
            return def;
        }

        return sound;

    }

    public Object get(String path, Object def) {

        if (getConfig().getString(path) == null) {
            utils.log("&cError using path: " + path);
            utils.log("&cUsing default: " + def);
            return def;
        }

        return getConfig().get(path, def);

    }

}
