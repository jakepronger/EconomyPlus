package me.itswagpvp.economyplus.commands;

import me.itswagpvp.economyplus.EconomyPlus;
import me.itswagpvp.economyplus.listeners.PlayerHandler;
import me.itswagpvp.economyplus.hooks.vault.Economy;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

import static me.itswagpvp.economyplus.EconomyPlus.plugin;
import static me.itswagpvp.economyplus.utils.Sounds.sounds;
import static me.itswagpvp.economyplus.utils.Utils.utils;

public class Eco implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        if (utils.noPerms(sender, "economyplus.eco.reset", false)
                || utils.noPerms(sender, "economyplus.eco.give", false)
                || utils.noPerms(sender, "economyplus.eco.take", false)
                || utils.noPerms(sender, "economyplus.eco.set", false))
            return true;

        if (args.length == 2) {

            OfflinePlayer p;

            String name = args[0];

            double starting = plugin.getConfig().getDouble("Starting-Balance"); // gets starting balance

            if (args[1].equalsIgnoreCase("reset")) {

                if (name.equalsIgnoreCase("@a") || name.equalsIgnoreCase("*")) {

                    // is using @a or *

                    if (plugin.getConfig().getBoolean("Reset-All.Flags", false)) {

                        if (plugin.getConfig().getBoolean("Reset-All.Console-Only", true)) {
                            if (sender instanceof Player) {
                                // player is not console
                                sender.sendMessage(ChatColor.RED + "Only console can use @a and * flags!");
                                return true;
                            }
                        }

                    } else {
                        sender.sendMessage(ChatColor.RED + "Resetting everyone's balance is disabled!");
                        sounds.error(sender);
                        return true;
                    }

                    // change cache and economy bal, get economy profile and set it to starting bal?
                    for (String s : EconomyPlus.getDBType().getList()) {
                        if (plugin.getStorageMode() == StorageMode.UUID) {
                            Economy eco = new Economy(Bukkit.getOfflinePlayer(UUID.fromString(s)));
                            eco.setBalance(starting);
                        } else {
                            Economy eco = new Economy(Bukkit.getOfflinePlayer(s));
                            eco.setBalance(starting);
                        }
                    }

                    if (plugin.isMessageEnabled("Money.Done")) {
                        sender.sendMessage(ChatColor.GREEN + "You have just refreshed everyone's balances!");
                    }

                    sounds.success(sender);

                } else {

                    // isn't using * or @a

                    if (args[0].equalsIgnoreCase("@a") || args[0].equalsIgnoreCase("*")) {
                        sender.sendMessage(ChatColor.RED + "You cannot use * or @a as a name!");
                        sounds.error(sender);
                        return true;
                    }

                    p = PlayerHandler.getPlayer(args[0]);

                    if (p == null || (!p.hasPlayedBefore() && !p.isOnline())) {
                        sender.sendMessage(plugin.getMessage("PlayerNotFound"));
                        sounds.error(sender);
                        return true;
                    }

                    if (utils.noPerms(sender, "economyplus.eco.reset", false)) {
                        return true;
                    } else {

                        Economy eco = new Economy(p);
                        eco.setBalance(starting);

                        if (plugin.isMessageEnabled("Money.Done")) {
                            sender.sendMessage(plugin.getMessage("Money.Done"));
                        }

                        if (p.isOnline() && p.getPlayer() != null) {
                            p.getPlayer().sendMessage(plugin.getMessage("Money.Reset"));
                        }

                        sounds.success(sender);

                    }

                }

                return true;

            }

        } else if (args.length == 3) {

            if (!args[1].equalsIgnoreCase("set") & !args[1].equalsIgnoreCase("give") & !args[1].equalsIgnoreCase("take")) {
                sender.sendMessage(plugin.getMessage("InvalidArgs.Eco"));
                sounds.error(sender);
                return true;
            }

            if (args[0].equalsIgnoreCase("@a") || args[0].equalsIgnoreCase("*")) {
                sender.sendMessage(ChatColor.RED + "You cannot use * or @a as a name!");
                sounds.error(sender);
                return true;
            }

            OfflinePlayer p = PlayerHandler.getPlayer(args[0]);

            if (p == null || (!p.hasPlayedBefore() && !p.isOnline())) {
                sender.sendMessage(plugin.getMessage("PlayerNotFound"));
                sounds.success(sender);
                return true;
            }

            String arg = args[2].replace(",", ".");

            double value;
            try {
                value = Double.parseDouble(arg);
            } catch (NumberFormatException e) {
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cInvalid number!"));
                return true;
            }

            Economy money = new Economy(p);

            if (args[1].equalsIgnoreCase("set")) {

                if (utils.noPerms(sender, "economyplus.eco.set", false)) {
                    return true;
                }

                money.setBalance(value);

                if (plugin.isMessageEnabled("Money.Done")) {
                    sender.sendMessage(plugin.getMessage("Money.Done"));
                }

                if (p.getPlayer() != null) {
                    if (plugin.isMessageEnabled("Money.Refreshed")) {
                        p.getPlayer().sendMessage(plugin.getMessage("Money.Refreshed")
                                .replaceAll("%money_formatted%", utils.fixMoney(value))
                                .replaceAll("%money%", utils.format(value)));

                        sounds.success(p.getPlayer());
                    }
                }

                sounds.success(sender);

                return true;

            } else if (args[1].equalsIgnoreCase("take")) {

                if (utils.noPerms(sender, "economyplus.eco.take", false)) {
                    return true;
                }

                double res = money.getBalance(p) - value;

                if (res < 0D) {
                    res = 0D;
                    money.setBalance(0D);
                } else {
                    money.takeBalance(value);
                }

                if (plugin.isMessageEnabled("Money.Done")) {
                    sender.sendMessage(plugin.getMessage("Money.Done"));
                }

                if (p.getPlayer() != null) {
                    if (plugin.isMessageEnabled("Money.Refreshed")) {
                        p.getPlayer().sendMessage(plugin.getMessage("Money.Refreshed")
                                .replaceAll("%money%", utils.format(res))
                                .replaceAll("%money_formatted%", utils.fixMoney(res)));
                        sounds.success(p.getPlayer());
                    }
                }

                sounds.success(sender);

                return true;

            } else if (args[1].equalsIgnoreCase("give")) {

                if (utils.noPerms(sender, "economyplus.eco.give", false)) {
                    return true;
                }

                money.addBalance(value);

                if (plugin.isMessageEnabled("Money.Done")) {
                    sender.sendMessage(plugin.getMessage("Money.Done"));
                }

                if (p.getPlayer() != null) {
                    if (plugin.isMessageEnabled("Money.Refreshed")) {
                        p.getPlayer().sendMessage(plugin.getMessage("Money.Refreshed")
                                .replaceAll("%money_formatted%", utils.fixMoney(money.getBalance()))
                                .replaceAll("%money%", utils.format(money.getBalance())));
                        sounds.success(p.getPlayer());
                    }
                }

                sounds.success(sender);
                return true;
            }

        }

        // usage
        sender.sendMessage(plugin.getMessage("InvalidArgs.Eco"));
        sounds.error(sender);
        return true;
    }

}