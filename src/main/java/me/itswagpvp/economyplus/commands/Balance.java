package me.itswagpvp.economyplus.commands;

import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.itswagpvp.economyplus.listeners.PlayerHandler;
import me.itswagpvp.economyplus.utils.Utils;
import me.itswagpvp.economyplus.hooks.vault.Economy;
import org.jetbrains.annotations.NotNull;

import static me.itswagpvp.economyplus.EconomyPlus.plugin;
import static me.itswagpvp.economyplus.listeners.PlayerHandler.getName;
import static me.itswagpvp.economyplus.utils.SoundUtils.sound;
import static me.itswagpvp.economyplus.utils.Utils.utils;

public class Balance implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {

        if (utils.hasPerm(sender, "economyplus.balance", true)) {
            return true;
        }

        if (args.length == 0) {

            if (sender instanceof Player) {

                Player p = (Player) sender;

                Economy eco = new Economy(p);

                sender.sendMessage(plugin.getMessage("Balance.Self")
                        .replaceAll("%money%", new Utils().format(eco.getBalance()))
                        .replaceAll("%money_formatted%", new Utils().fixMoney(eco.getBalance())));

                sound.success(sender);

                return true;

            }

        }

        else if (args.length == 1) {

            OfflinePlayer p = PlayerHandler.getPlayer(args[0]);

            if (p == null) {
                sender.sendMessage(plugin.getMessage("PlayerNotFound"));
                return true;
            }

            String name = getName(p.getUniqueId(), false);
            if (name.equalsIgnoreCase("Invalid User")) {
                sender.sendMessage(plugin.getMessage("PlayerNotFound"));
                return true;
            }

            Economy eco = new Economy(p);

            if (sender == p) { // player mentioned is the sender
                sender.sendMessage(plugin.getMessage("Balance.Self")
                        .replaceAll("%money%", "" + new Utils().format(eco.getBalance()))
                        .replaceAll("%money_formatted%", "" + new Utils().fixMoney(eco.getBalance())));
            }

            else {
                sender.sendMessage(plugin.getMessage("Balance.Others")
                        .replaceAll("%money%", "" + new Utils().format(eco.getBalance()))
                        .replaceAll("%money_formatted%", "" + new Utils().fixMoney(eco.getBalance()))
                        .replaceAll("%player%", "" + name));
            }

            sound.success(sender);

            return true;

        }

        sender.sendMessage(plugin.getMessage("InvalidArgs.Balance"));

        sound.error(sender);

        return true;

    }
}
