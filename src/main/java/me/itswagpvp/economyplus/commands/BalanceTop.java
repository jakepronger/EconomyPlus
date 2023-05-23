package me.itswagpvp.economyplus.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import me.itswagpvp.economyplus.misc.BalTopManager;

import java.util.List;

import static me.itswagpvp.economyplus.EconomyPlus.plugin;
import static me.itswagpvp.economyplus.utils.Config.config;
import static me.itswagpvp.economyplus.utils.Utils.utils;

public class BalanceTop implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (utils.noPerm(sender, "economyplus.baltop", true)) {
            return true;
        }

        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {

            List<String> header = plugin.getConfig().getStringList("Baltop.Chat.Header");

            BalTopManager balTopManager = plugin.getBalTopManager();

            int page;

            try {
                page = Integer.parseInt(args[0]);
                if (page < 1) {
                    page = 1;
                }
            } catch (Exception e) {
                page = 1;
            }

            int start = (page - 1) * 10;

            for (String message : header) {
                sender.sendMessage(message
                        .replaceAll("%page%", "" + page)
                        .replaceAll("&", "§"));
            }


            for (int i = start; i < balTopManager.getBalTop().size() && i < start + 10; i++) {

                BalTopManager.PlayerData pData = balTopManager.getBalTop().get(i);

                String name = pData.getName();
                double money = pData.getMoney();

                sender.sendMessage(config.getString("Baltop.Chat.Player-Format", "&6%number%) &f%player%: &c$%money%")
                        .replaceAll("&", "§")
                        .replaceAll("%number%", String.valueOf(i + 1))
                        .replaceAll("%player%", name)
                        .replaceAll("%money%", utils.format(money))
                        .replaceAll("%money_formatted%", utils.fixMoney(money)));
            }

            // add option for interactive arrows?

            // add option to show player's personal position on baltop?


        });

        return true;
    }
}