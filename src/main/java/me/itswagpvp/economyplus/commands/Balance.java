package me.itswagpvp.economyplus.commands;

import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.itswagpvp.economyplus.listeners.PlayerHandler;
import me.itswagpvp.economyplus.utils.Utils;
import me.itswagpvp.economyplus.hooks.vault.Economy;

import static me.itswagpvp.economyplus.EconomyPlus.plugin;
import static me.itswagpvp.economyplus.listeners.PlayerHandler.getName;
import static me.itswagpvp.economyplus.utils.Sounds.sounds;
import static me.itswagpvp.economyplus.utils.Utils.utils;

public class Balance implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (utils.noPerm(sender, "economyplus.balance", true)) {
            return true;
        }

        if (args.length == 0) {

            if (sender instanceof Player) {

                Player p = (Player) sender;

                Economy eco = new Economy(p);

                sender.sendMessage(plugin.getMessage("Balance.Self")
                        .replaceAll("%money%", new Utils().format(eco.getBalance()))
                        .replaceAll("%money_formatted%", new Utils().fixMoney(eco.getBalance())));

                sounds.success(sender);

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
                sender.sendMessage(plugin.getMessage("balance.self")
                        .replaceAll("%money%", utils.format(eco.getBalance()))
                        .replaceAll("%money_formatted%", utils.fixMoney(eco.getBalance())));
            }

            else {
                sender.sendMessage(plugin.getMessage("balance.others")
                        .replaceAll("%money%", utils.format(eco.getBalance()))
                        .replaceAll("%money_formatted%", utils.fixMoney(eco.getBalance()))
                        .replaceAll("%player%", name));
            }

            sounds.success(sender);

            return true;

        }

        sender.sendMessage(plugin.getMessage("InvalidArgs.Balance"));

        sounds.error(sender);

        return true;

    }
}
