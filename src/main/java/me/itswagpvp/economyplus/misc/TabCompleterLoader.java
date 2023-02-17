package me.itswagpvp.economyplus.misc;

import me.itswagpvp.economyplus.EconomyPlus;
import me.itswagpvp.economyplus.database.CacheManager;
import me.itswagpvp.economyplus.database.misc.DatabaseType;
import me.itswagpvp.economyplus.listener.PlayerHandler;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static me.itswagpvp.economyplus.EconomyPlus.balTopManager;

public class TabCompleterLoader implements TabCompleter {
    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {

        List<String> listDefault = Collections.singletonList("");
        List<String> playerNames = new ArrayList<>();
        playerNames.addAll(PlayerHandler.getUsernames());

        // /eco
        if (command.getName().equalsIgnoreCase("eco")) {
            int i = (args.length);
            switch (i) {
                case 1: {
                    return playerNames;
                }
                case 2: {
                    return Arrays.asList("give", "set", "take", "reset");
                }
                case 3: {

                    if (args[1].equals("reset")) {
                        return listDefault;
                    }

                    return Arrays.asList("0", "100", "1000");
                }
                default:
                    return listDefault;
            }
        }

        // EconomyPlus main command TabCompleter
        if (command.getName().equalsIgnoreCase("economyplus")) {
            int i = (args.length);
            switch (i) {
                case 1:
                    return Arrays.asList("help", "debug", "reload", "hologram", "update", "convert", "exclude");
                case 2:
                    if (args[1].equalsIgnoreCase("convert")) {
                        Arrays.asList("UUID", "NICKNAME");
                    }
            }
            return listDefault;
        }

        // /bal
        if (command.getName().equalsIgnoreCase("bal")) {
            int i = (args.length);
            if (i == 1) {
                return playerNames;
            }
            return listDefault;
        }

        // /pay
        if (command.getName().equalsIgnoreCase("pay")) {
            int i = (args.length);
            switch (i) {
                case 1: {
                    return playerNames;
                }

                case 2: {
                    return Arrays.asList("10", "100", "1000");
                }

                default:
                    return listDefault;
            }
        }

        // /baltop
        if (command.getName().equalsIgnoreCase("baltop")) {

            int i = args.length;

            EconomyPlus.balTopManager = new BalTopManager();
            new BalTopManager();
            BalTopManager balTopManager = EconomyPlus.plugin.getBalTopManager();

            double v = balTopManager.getBalTop().size();
            v = v / 10; // gets total amount of pages balance top uses

            int vi = Integer.parseInt(String.valueOf(v).split("\\.")[0]); // gets the int of v

            if (v > vi) { // if v is greater than the int of v
                // set v to v + 1
                v = vi + 1;
            }

            List<String> balTopPages = new ArrayList<>();

            for (int l = 1; (l - 1) < v; l++) {
                balTopPages.add(String.valueOf(l));
            }

            if (i == 1) {
                return balTopPages;
            }

            return listDefault;

        }

        // /bank
        if (command.getName().equalsIgnoreCase("bank")) {
            int i = (args.length);
            switch (i) {
                case 1: {
                    return Arrays.asList("deposit", "withdraw", "admin");
                }
                case 2: {
                    if (args[0].equalsIgnoreCase("admin")) {
                        return Arrays.asList("set", "get");
                    }

                    return Arrays.asList("10", "100", "1000");
                }

                case 3: {
                    if (args[1].equalsIgnoreCase("set") || args[1].equalsIgnoreCase("get")) {
                        return playerNames;
                    }

                    return listDefault;
                }

                case 4: {
                    if (args[1].equalsIgnoreCase("set")) {
                        return Arrays.asList("0", "10", "100");
                    }

                    return listDefault;
                }

                case 5: {
                    if (args[1].equalsIgnoreCase("set")) {
                        return playerNames;
                    }

                    return listDefault;
                }

                case 6: {
                    if (args[1].equalsIgnoreCase("set")) {
                        return Arrays.asList("0", "10", "100");
                    }
                }

                default:
                    return listDefault;
            }
        }

        return listDefault;
    }
}
