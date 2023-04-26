package me.itswagpvp.economyplus.misc;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import me.itswagpvp.economyplus.EconomyPlus;
import me.itswagpvp.economyplus.database.misc.DatabaseType;
import me.itswagpvp.economyplus.Messages;

import static me.itswagpvp.economyplus.EconomyPlus.plugin;

public class Utils {

    // No Perms
    public static boolean hasPerm(CommandSender sender, String permission, boolean isBasicPerm) {

        if (isBasicPerm && !plugin.REQUIRE_BASIC_PERMISSIONS) {
            return true;
        }

        if (sender.hasPermission(permission)) {
            return true;
        }

        sender.sendMessage(plugin.getMessage("NoPerms"));
        Utils.playErrorSound(sender);

        return false;
    }

    // Error sound played to player
    public static void playErrorSound(CommandSender sender) {

        if (!plugin.getConfig().getBoolean("Sounds.Use")) {
            return;
        }

        if (sender instanceof ConsoleCommandSender) {
            return;
        }

        Player player = (Player) sender;

        try {
            Sound x = Sound.valueOf(plugin.getConfig().getString("Sounds.Error", "ENTITY_VILLAGER_NO"));
            player.playSound(player.getPlayer().getLocation(), x, 1, 1);
        } catch (Exception e) {
            plugin.pluginLog("[EconomyPlus] &7Error on the &cplayErrorSound&7! Check your config!");
            e.printStackTrace();
        }

    }

    // Success sound played to player
    public static void playSuccessSound(CommandSender sender) {

        if (!plugin.getConfig().getBoolean("Sounds.Use")) {
            return;
        }

        if (sender instanceof ConsoleCommandSender) {
            return;
        }

        Player player = (Player) sender;

        try {
            Sound x = Sound.valueOf(plugin.getConfig().getString("Sounds.Success", "ENTITY_PLAYER_LEVELUP"));
            player.playSound(player.getPlayer().getLocation(), x, 1, 1);
        } catch (Exception e) {
            plugin.pluginLog("[EconomyPlus] &7Error on the &cplaySuccessSound§&! Check your config!");
            e.printStackTrace();
        }
    }

    public static void reloadPlugin(CommandSender p) {

        long before = System.currentTimeMillis();

        plugin.pluginLog("[EconomyPlus] &aReloading the plugin! This action may take a while!");

        try {

            if (EconomyPlus.getDBType() == DatabaseType.YAML) {
                plugin.createYMLStorage();
            }

            plugin.saveDefaultConfig();
            plugin.reloadConfig();

            new StorageManager().createStorageConfig();

            Messages.load();

        } catch (Exception e) {
            p.sendMessage("§cError on reloading the plugin! (" + e.getMessage() + ")");
        } finally {
            p.sendMessage(plugin.getMessage("Reload")
                    .replaceAll("%time%", "" + (System.currentTimeMillis() - before)));
        }

        plugin.pluginLog("&aReloaded!");

    }

    public static String hexColor(String text) {
        Pattern pattern = Pattern.compile("#[a-fA-f0-9]{6}");
        Matcher matcher = pattern.matcher(text);
        while (matcher.find()) {
            String color = text.substring(matcher.start(), matcher.end());
            text = text.replace(color, net.md_5.bungee.api.ChatColor.of(color) + "");
        }
        return net.md_5.bungee.api.ChatColor.translateAlternateColorCodes('&', text);
    }

    public static boolean supportRGBColors() {
        return Bukkit.getVersion().contains("16")
                || Bukkit.getVersion().contains("17")
                || Bukkit.getVersion().contains("18")
                || Bukkit.getVersion().contains("19");
    }

    public String format(Double d) {

        DecimalFormat df = new DecimalFormat("#.##"); //NUMBER CANNOT GO ABOVE BILLION DUE TO IT BEING A DOUBLE
        if (plugin.getConfig().getBoolean("Pattern.Enabled")) {
            df = new DecimalFormat(plugin.getConfig().getString("Pattern.Value", "###,###.##"));
        }

        if (plugin.getConfig().getBoolean("Formatting.Round-Decimals", false)) {
            df.setRoundingMode(RoundingMode.HALF_UP);
        } else {
            df.setRoundingMode(RoundingMode.DOWN);
        }

        String value = df.format(d);

        if (!(value.contains("."))) {
            value = value + "." + 00;
        }

        if (plugin.getConfig().getBoolean("Formatting.Use-Decimals", true)) {

            if (value.split("\\.")[1].length() == 1) {
                value = value + "0";
            }

            if (!plugin.getConfig().getBoolean("Formatting.Have-Excessive-Zeros", false)) {

                if (value.split("\\.")[1].matches("00")) {
                    value = value.split("\\.")[0];
                }

            } else {

                if (!(value.contains("."))) {
                    value = value + ".00";
                }

            }

        } else {
            value = new DecimalFormat("#").format(d);
        }

        return value;

    }

    public String fixMoney(Double d) {

        if (d < 1000L) {
            return format(d);
        }
        if (d < 1000000L) {
            return format(d / 1000D) + plugin.getConfig().get("Formatted-Placeholder.1000");
        }
        if (d < 1000000000L) {
            return format(d / 1000000D) + plugin.getConfig().get("Formatted-Placeholder.1000000");
        }
        if (d < 1000000000000L) {
            return format(d / 1000000000D) + plugin.getConfig().get("Formatted-Placeholder.1000000000");
        }
        if (d < 1000000000000000L) {
            return format(d / 1000000000000D) + plugin.getConfig().get("Formatted-Placeholder.1000000000000");
        }
        if (d < 1000000000000000000L) {
            return format(d / 1000000000000000D) + plugin.getConfig().get("Formatted-Placeholder.1000000000000");
        }

        return format(d);
    }

}
