package me.itswagpvp.economyplus.commands;

import me.itswagpvp.economyplus.misc.StorageManager;
import me.itswagpvp.economyplus.hooks.vault.Economy;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import static me.itswagpvp.economyplus.EconomyPlus.plugin;
import static me.itswagpvp.economyplus.utils.Sounds.sounds;
import static me.itswagpvp.economyplus.utils.Utils.utils;

public class Pay implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (sender instanceof ConsoleCommandSender) {
            sender.sendMessage(plugin.getMessage("NoConsole"));
            return true;
        }

        Player p = (Player) sender;

        if (utils.noPerm(p, "economyplus.pay", true)) {
            return true;
        }

        if (args.length != 2) {
            p.sendMessage(plugin.getMessage("InvalidArgs.Pay"));
            sounds.error(p);
            return true;
        }

        Player target = Bukkit.getServer().getPlayer(args[0]);

        if (target == null) {
            p.sendMessage(plugin.getMessage("PlayerNotFound"));
            sounds.error(p);
            return true;
        }

        if (new StorageManager().getStorageConfig().getBoolean("PayToggle." + target.getName())) {
            p.sendMessage(plugin.getMessage("Pay.DisabledPayments"));
            sounds.error(p);
            return true;
        }

        if (target == p) {
            p.sendMessage(plugin.getMessage("Pay.NoSelf"));
            sounds.error(p);
            return true;
        }

        if (args[1].startsWith("-")) {
            p.sendMessage(plugin.getMessage("InvalidArgs.Pay"));
            sounds.error(p);
            return true;
        }

        double money;
        try {
            money = Double.parseDouble(args[1]);
        } catch (Exception e) {
            p.sendMessage(plugin.getMessage("InvalidArgs.Pay"));
            sounds.error(p);
            return true;
        }

        if (Double.isNaN(money) || Double.isInfinite(money)) {
            p.sendMessage(plugin.getMessage("InvalidArgs.Pay"));
            sounds.error(p);
            return true;
        }

        Economy selfEco = new Economy(p);

        if (!selfEco.detractable(money)) {
            p.sendMessage(plugin.getMessage("Pay.NoMoney"));
            sounds.error(p);
            return true;
        }

        Economy otherEco = new Economy(target);

        selfEco.takeBalance(money);
        otherEco.addBalance(money);

        sounds.success(p);
        sounds.success(target);

        p.sendMessage(plugin.getMessage("Pay.Self")
                .replaceAll("%money_formatted%", utils.fixMoney(money))
                .replaceAll("%money%", utils.format(money))
                .replaceAll("%player%", target.getName()));

        target.sendMessage(plugin.getMessage("Pay.Target")
                .replaceAll("%money_formatted%", utils.fixMoney(money))
                .replaceAll("%money%", utils.format(money))
                .replaceAll("%player%", p.getName()));

        return true;
    }

}
