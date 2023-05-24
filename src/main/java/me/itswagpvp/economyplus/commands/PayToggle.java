package me.itswagpvp.economyplus.commands;

import me.itswagpvp.economyplus.misc.StorageManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import static me.itswagpvp.economyplus.EconomyPlus.plugin;
import static me.itswagpvp.economyplus.utils.Sounds.sounds;
import static me.itswagpvp.economyplus.utils.Utils.utils;

public class PayToggle implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        if (sender instanceof ConsoleCommandSender) {
            sender.sendMessage(plugin.getMessage("NoConsole"));
            return true;
        }

        Player p = (Player) sender;

        if (utils.noPerms(p, "economyplus.paytoggle", true)) {
            return true;
        }

        StorageManager storage = new StorageManager();
        if (!storage.getStorageConfig().getBoolean("PayToggle." + p.getName())) {
            storage.getStorageConfig().set("PayToggle." + p.getName(), true);
            p.sendMessage(plugin.getMessage("Pay.Toggle.Enabled"));
        } else {
            storage.getStorageConfig().set("PayToggle." + p.getName(), false);
            p.sendMessage(plugin.getMessage("Pay.Toggle.Disabled"));
        }

        sounds.success(p);
        storage.saveStorageConfig();

        return true;
    }

}
