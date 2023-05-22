package me.itswagpvp.economyplus.utils;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static me.itswagpvp.economyplus.EconomyPlus.plugin;
import static me.itswagpvp.economyplus.utils.SoundUtils.sound;

public class Utils {

    public static Utils utils = new Utils();

    public boolean hasPerm(CommandSender sender, String permission, boolean isBasicPerm) {

        if (isBasicPerm && !plugin.basicperms) {
            return false;
        }

        if (sender.hasPermission(permission)) {
            return false;
        }

        sender.sendMessage(plugin.getMessage("NoPerms"));

        sound.error(sender);

        return true;
    }

    public void log(String text) { // send plugin message to console
        Bukkit.getConsoleSender().sendMessage("[" + plugin.getName() + "] " + text);
    }

    public String getString(String path, String def) {

        if (plugin.getConfig().getString(path) == null) {
            log("&cError getting string using path: " + path);
            log("&cUsing default: " + def);
            return def;
        }

        return plugin.getConfig().getString(path);
    }

    public String hexColor(String text) {

        Pattern pattern = Pattern.compile("#[a-fA-f0-9]{6}");
        Matcher matcher = pattern.matcher(text);

        while (matcher.find()) {
            String color = text.substring(matcher.start(), matcher.end());
            text = text.replace(color, net.md_5.bungee.api.ChatColor.of(color));
        }

        return ChatColor.translateAlternateColorCodes('&', text);
    }

    public boolean supportRGBColors() {
        return Bukkit.getVersion().contains("16")
                || Bukkit.getVersion().contains("17")
                || Bukkit.getVersion().contains("18")
                || Bukkit.getVersion().contains("19");
    }

    public String format(Double d) {

        DecimalFormat df = new DecimalFormat("#.##"); // NUMBER CANNOT GO ABOVE BILLION DUE TO IT BEING A DOUBLE
        if (plugin.getConfig().getBoolean("Pattern.Enabled")) {
            df = new DecimalFormat(plugin.getConfig().getString("Pattern.Value", "###,###.##"));
        }

        if (plugin.getConfig().getBoolean("Formatting.Round-Decimals", false)) {
            df.setRoundingMode(RoundingMode.HALF_UP);
        } else {
            df.setRoundingMode(RoundingMode.DOWN);
        }

        String value = df.format(d);

        if (!value.contains(".")) {
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

        if (d < 1000000L) {
            return format(d / 1000D) + plugin.getConfig().get("Formatted-Placeholder.1000");
        } else if (d < 1000000000L) {
            return format(d / 1000000D) + plugin.getConfig().get("Formatted-Placeholder.1000000");
        } else if (d < 1000000000000L) {
            return format(d / 1000000000D) + plugin.getConfig().get("Formatted-Placeholder.1000000000");
        } else if (d < 1000000000000000L) {
            return format(d / 1000000000000D) + plugin.getConfig().get("Formatted-Placeholder.1000000000000");
        } else if (d < 1000000000000000000L) {
            return format(d / 1000000000000000D) + plugin.getConfig().get("Formatted-Placeholder.1000000000000");
        }

        return format(d);
    }

}
