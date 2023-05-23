package me.itswagpvp.economyplus.commands;

import me.itswagpvp.economyplus.hooks.holograms.HolographicDisplays;
import me.itswagpvp.economyplus.misc.Converter;
import me.itswagpvp.economyplus.misc.StorageManager;
import me.itswagpvp.economyplus.misc.Updater;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import static me.itswagpvp.economyplus.EconomyPlus.plugin;
import static me.itswagpvp.economyplus.utils.Reload.reload;
import static me.itswagpvp.economyplus.utils.Sounds.sounds;
import static me.itswagpvp.economyplus.utils.Utils.utils;

public class Main implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        if (args.length == 0) {
            sender.sendMessage("§d§lEconomy§5§lPlus §7v" + plugin.getDescription().getVersion() + " made by §d_ItsWagPvP");
            sender.sendMessage("§7For help do /economyplus help");
            return true;
        }

        if (args.length == 1) {

            if (args[0].equalsIgnoreCase("reload")) {

                if (utils.noPerm(sender, "economyplus.reload", false)) {
                    return true;
                }

                reload.execute(sender);
                sounds.success(sender);

                return true;
            }

            if (args[0].equalsIgnoreCase("debug")) {

                if (sender instanceof Player) {

                    if (plugin.isMessageEnabled("NoPlayer")) {
                        sender.sendMessage(plugin.getMessage("NoPlayer"));
                    }

                    sounds.error(sender);

                    return true;
                }

                sender.sendMessage("§8+------------------------------------+");
                sender.sendMessage("             §dEconomy§5Plus");
                sender.sendMessage("                §4Debug");
                sender.sendMessage("§a");
                sender.sendMessage("§f-> §7MC-Version of the server: §c" + Bukkit.getBukkitVersion());
                sender.sendMessage("§f-> §7Version of the plugin: §e" + plugin.getDescription().getVersion());
                sender.sendMessage("§f-> §7Version of the config: §e" + plugin.getConfig().getString("Version"));
                sender.sendMessage("§a");
                sender.sendMessage("§f-> §7Database: §b" + plugin.getConfig().getString("Database.Type"));
                sender.sendMessage("§f-> §7Storage-mode: §2" + plugin.getConfig().getString("Database.Mode", "UUID"));
                sender.sendMessage("§f-> §7Messages file: §2" + plugin.getConfig().getString("Language", "EN"));
                sender.sendMessage("§a");
                sender.sendMessage("§f-> §7Server software: §6" + Bukkit.getName());
                sender.sendMessage("§f-> §7Software version: §6" + Bukkit.getVersion());
                sender.sendMessage("§f-> §7Vault Version: §d" + Bukkit.getServer().getPluginManager().getPlugin("Vault").getDescription().getVersion());
                sender.sendMessage("§a");
                sender.sendMessage("§f-> §7PlaceholderAPI: §a" + Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI"));
                sender.sendMessage("§f-> §7HolographicDisplays: §a" + Bukkit.getPluginManager().isPluginEnabled("HolographicDisplays"));
                sender.sendMessage("§8+------------------------------------+");

                return true;

            }

            else if (args[0].equalsIgnoreCase("help")) {
                sender.sendMessage("§d§lEconomy§5§lPlus §7v" + plugin.getDescription().getVersion() + " made by §d_ItsWagPvP");
                sender.sendMessage("§7If you need support, join the discord server!");
                sender.sendMessage("§f-> §9https://discord.itswagpvp.eu/");
                return true;
            }

            else if (args[0].equalsIgnoreCase("hologram")) {

                if (sender instanceof ConsoleCommandSender) {
                    sender.sendMessage(plugin.getMessage("NoConsole"));
                    return true;
                }

                Player p = (Player) sender;

                if (utils.noPerm(p, "economyplus.hologram", false)) {
                    return true;
                }

                Location loc = p.getLocation();

                StorageManager storageManager = new StorageManager();

                storageManager.getStorageConfig().set("Hologram.BalTop.World", loc.getWorld().getName());
                new StorageManager().saveStorageConfig();

                storageManager.getStorageConfig().set("Hologram.BalTop.X", loc.getX());
                new StorageManager().saveStorageConfig();

                storageManager.getStorageConfig().set("Hologram.BalTop.Y", loc.getY());
                new StorageManager().saveStorageConfig();

                storageManager.getStorageConfig().set("Hologram.BalTop.Z", loc.getZ());
                new StorageManager().saveStorageConfig();

                new HolographicDisplays().createHologram();

                return true;

            }

            else if (args[0].equalsIgnoreCase("update")) {

                if (utils.noPerm(sender, "economyplus.update", false)) {
                    return true;
                }

                Updater.downloadUpdate(sender);

                return true;

            }

        }

        else if (args.length == 2) {

            if (args[0].equalsIgnoreCase("convert")) {

                if (sender instanceof Player) {
                    if (plugin.isMessageEnabled("NoPlayer")) {
                        sender.sendMessage(plugin.getMessage("NoPlayer"));
                        sounds.error(sender);
                    }
                    return true;
                }

                int accounts;

                String mode = args[1];
                if (mode.equalsIgnoreCase("uuid")) {
                    accounts = new Converter().NameToUUID();
                } else if (mode.equalsIgnoreCase("nickname")) {
                    accounts = new Converter().UUIDToName();
                } else {
                    sender.sendMessage("§cYou have to set /ep convert <UUID/NICKNAME>");
                    return true;
                }

                if (accounts == -1) {
                    sender.sendMessage(ChatColor.RED + "Storage mode is already set to " + mode.toUpperCase());
                } else {

                    //wait a tick
                    Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
                        @Override
                        public void run() {
                            sender.sendMessage("§aYou have converted " + accounts + " accounts to " + mode.toUpperCase() + " storage mode!");
                        }
                    }, 1);

                }

                return true;

            }

            else if (args[0].equalsIgnoreCase("exclude")) {

                if (!(sender instanceof ConsoleCommandSender)) {
                    if (plugin.isMessageEnabled("NoPlayer")) {
                        sender.sendMessage(plugin.getMessage("NoPlayer"));
                        sounds.error(sender);
                    }
                    return true;
                }

                if (plugin.getConfig().getBoolean("BalTop.Exclude." + args[1])) {
                    new StorageManager().getStorageConfig().set("BalTop.Exclude." + args[1], false);
                    sender.sendMessage("§aIncluded " + args[1] + " in the BalTop!");
                } else {
                    new StorageManager().getStorageConfig().set("BalTop.Exclude." + args[1], true);
                    sender.sendMessage("§aExcluded " + args[1] + " from the BalTop!");
                }

                new StorageManager().saveStorageConfig();

                return true;

            }

        }

        sender.sendMessage(plugin.getMessage("InvalidArgs.Main"));
        sounds.error(sender);

        return true;

    }

}