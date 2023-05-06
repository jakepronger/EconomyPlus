package me.itswagpvp.economyplus.utils;

import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import static me.itswagpvp.economyplus.EconomyPlus.plugin;
import static me.itswagpvp.economyplus.utils.ConfigUtils.config;

public class SoundUtils {

    public static SoundUtils sound = new SoundUtils();

    public void error(CommandSender sender) { // Error sound played to player

        if (!plugin.getConfig().getBoolean("Sounds.Use")) return;

        if (sender instanceof ConsoleCommandSender) return;

        Player p = (Player) sender;

        p.playSound(p.getLocation(), getSound("Sounds.Error", Sound.ENTITY_VILLAGER_NO), 1, 1);

    }

    public void success(CommandSender sender) { // Success sound played to player

        if (!plugin.getConfig().getBoolean("Sounds.Use")) return;

        if (sender instanceof ConsoleCommandSender) return;

        Player p = (Player) sender;

        p.playSound(p.getLocation(), getSound("Sounds.Success", Sound.ENTITY_PLAYER_LEVELUP), 1, 1);

    }

    //public Sound getSound(String path) {

    //}

    public Sound getSound(String path, Sound def) {
        String string = config.getString(path);
        if (string == null) {
            // error handle (utils?)
            return def;
        } else {
            try {
                return Sound.valueOf(string);
            } catch (IllegalArgumentException e) {
                // error handle (utils?)
                return def;
            }
        }
    }

}
