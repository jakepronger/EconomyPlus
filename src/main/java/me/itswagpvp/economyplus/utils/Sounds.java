package me.itswagpvp.economyplus.utils;

import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import static me.itswagpvp.economyplus.utils.Config.config;

public class Sounds {

    public static Sounds sounds = new Sounds();

    public void error(CommandSender sender) { // Error sound played to player

        if (!config.getBoolean("sounds.use", true)) return;
        if (sender instanceof ConsoleCommandSender) return;

        Player p = (Player) sender;

        p.playSound(p.getLocation(), config.getSound("Sounds.Error", Sound.ENTITY_VILLAGER_NO), 1, 1);

    }

    public void success(CommandSender sender) { // Success sound played to player

        if (!config.getBoolean("sounds.use", true)) return;
        if (sender instanceof ConsoleCommandSender) return;

        Player p = (Player) sender;

        p.playSound(p.getLocation(), config.getSound("Sounds.Success", Sound.ENTITY_PLAYER_LEVELUP), 1, 1);

    }

}
