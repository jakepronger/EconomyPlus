package me.itswagpvp.economyplus.hooks.vault;

import me.itswagpvp.economyplus.listeners.PlayerHandler;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.text.NumberFormat;
import java.util.*;
import java.util.function.Supplier;

import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;

import me.itswagpvp.economyplus.EconomyPlus;
import me.itswagpvp.economyplus.misc.Selector;

public class VEconomy implements Economy {

    public EconomyPlus plugin;

    public VEconomy(EconomyPlus plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean isEnabled() {
        return plugin.isEnabled();
    }

    @Override
    public String getName() {
        return plugin.getDescription().getName();
    }

    @Override
    public boolean hasBankSupport() {
        return plugin.getConfig().getBoolean("Bank.Enabled", true);
    }

    @Override
    public int fractionalDigits() {
        return 2;
    }

    @Override
    public String format(double amount) {
        NumberFormat format = NumberFormat.getInstance(Locale.ENGLISH);
        format.setMaximumFractionDigits(2);
        format.setMinimumFractionDigits(0);
        return format.format(amount);
    }

    @Override
    public String currencyNamePlural() {
        return "Dollar";
    }

    @Override
    public String currencyNameSingular() {
        return "Dollars";
    }

    @Override
    public boolean hasAccount(String playerName) {
        final String actualInput = recoverType(playerName);
        return loggingAction(
                actualInput + ".hasAccount",
                () -> EconomyPlus.getDBType().contains(actualInput)
        );
    }

    @Override
    public boolean hasAccount(OfflinePlayer player) {
        return hasAccount(Selector.playerToString(player));
    }

    @Override
    public boolean hasAccount(String playerName, String worldName) {
        return hasAccount(playerName);
    }

    @Override
    public boolean hasAccount(OfflinePlayer player, String worldName) {
        return hasAccount(Selector.playerToString(player));
    }

    @Override
    public double getBalance(String playerName) {
        final String actualInput = recoverType(playerName);
        return loggingAction(
                actualInput + ".getBalance",
                () -> new me.itswagpvp.economyplus.hooks.vault.Economy(PlayerHandler.getPlayer(playerName)).getBalance()
        );
    }

    @Override
    public double getBalance(OfflinePlayer player) {
        return getBalance(Selector.playerToString(player));
    }

    @Override
    public double getBalance(String playerName, String world) {
        return getBalance(playerName);
    }

    @Override
    public double getBalance(OfflinePlayer player, String world) {
        return getBalance(Selector.playerToString(player));
    }

    @Override
    public boolean has(String playerName, double amount) {
        final String actualInput = recoverType(playerName);
        return loggingAction(
                actualInput + ".has",
                () -> getBalance(actualInput) >= amount
        );
    }

    @Override
    public boolean has(OfflinePlayer player, double amount) {
        return has(Selector.playerToString(player), amount);
    }

    @Override
    public boolean has(String playerName, String worldName, double amount) {
        return has(playerName, amount);
    }

    @Override
    public boolean has(OfflinePlayer player, String worldName, double amount) {
        return has(Selector.playerToString(player), amount);
    }

    @Override
    public EconomyResponse withdrawPlayer(String playerName, double amount) {
        final String actualInput = recoverType(playerName);
        return loggingAction(
                actualInput + ".withdrawPlayer",
                () -> {
                    double tokens = 0D;
                    try {
                        tokens = getBalance(actualInput) - amount;
                        if (tokens >= 0) {
                            EconomyPlus.getDBType().setTokens(actualInput, tokens);
                        } else {
                            return new EconomyResponse(amount, tokens, EconomyResponse.ResponseType.FAILURE, "Not enough moneys!");
                        }
                    } catch (Exception e) {
                        return new EconomyResponse(amount, tokens, EconomyResponse.ResponseType.FAILURE, "Error while removing moneys from the player " + actualInput);
                    }

                    return new EconomyResponse(amount, tokens, EconomyResponse.ResponseType.SUCCESS, "Done");
                }
        );
    }

    @Override
    public EconomyResponse withdrawPlayer(OfflinePlayer player, double amount) {
        return withdrawPlayer(Selector.playerToString(player), amount);
    }

    @Override
    public EconomyResponse withdrawPlayer(String playerName, String worldName, double amount) {
        return withdrawPlayer(playerName, amount);
    }

    @Override
    public EconomyResponse withdrawPlayer(OfflinePlayer player, String worldName, double amount) {
        return withdrawPlayer(Selector.playerToString(player), amount);
    }

    @Override
    public EconomyResponse depositPlayer(String playerName, double amount) {
        final String actualInput = recoverType(playerName);
        return loggingAction(
                actualInput + ".depositPlayer",
                () -> {
                    double tokens = 0D;
                    try {
                        tokens = getBalance(actualInput) + amount;
                        EconomyPlus.getDBType().setTokens(actualInput, tokens);
                    } catch (Exception e) {
                        return new EconomyResponse(amount, tokens, EconomyResponse.ResponseType.FAILURE, "Can't add moneys to the player " + actualInput);
                    }

                    return new EconomyResponse(amount, tokens, EconomyResponse.ResponseType.SUCCESS, "Action done");
                }
        );
    }

    @Override
    public EconomyResponse depositPlayer(OfflinePlayer player, double amount) {
        return depositPlayer(Selector.playerToString(player), amount);
    }

    @Override
    public EconomyResponse depositPlayer(String playerName, String worldName, double amount) {
        return depositPlayer(playerName, amount);
    }

    @Override
    public EconomyResponse depositPlayer(OfflinePlayer player, String worldName, double amount) {
        return depositPlayer(Selector.playerToString(player), amount);
    }

    @Override
    public EconomyResponse createBank(String name, String player) {
        return new EconomyResponse(0, getBalance(player), EconomyResponse.ResponseType.NOT_IMPLEMENTED, "Economy#createBank() is not implemented");
    }

    @Override
    public EconomyResponse createBank(String name, OfflinePlayer player) {
        return new EconomyResponse(0, getBalance(player), EconomyResponse.ResponseType.NOT_IMPLEMENTED, "Economy#createBank() is not implemented");
    }

    @Override
    public EconomyResponse deleteBank(String name) {
        return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "Economy#deleteBank() is not implemented");
    }

    @Override
    public EconomyResponse bankBalance(String name) {
        return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "Economy#bankBalance() is not implemented");
    }

    @Override
    public EconomyResponse bankHas(String name, double amount) {
        return new EconomyResponse(amount, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "Economy#bankHas() is not implemented");
    }

    @Override
    public EconomyResponse bankWithdraw(String name, double amount) {
        return new EconomyResponse(amount, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "Economy#bankWithdraw() is not implemented");
    }

    @Override
    public EconomyResponse bankDeposit(String name, double amount) {
        return new EconomyResponse(amount, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "Economy#bankDeposit() is not implemented");
    }

    @Override
    public EconomyResponse isBankOwner(String name, String playerName) {
        return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "Economy#isBankOwner() is not implemented");
    }

    @Override
    public EconomyResponse isBankOwner(String name, OfflinePlayer player) {
        return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "Economy#isBankOwner() is not implemented");
    }

    @Override
    public EconomyResponse isBankMember(String name, String playerName) {
        return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "Economy#isBankMember() is not implemented");
    }

    @Override
    public EconomyResponse isBankMember(String name, OfflinePlayer player) {
        return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "Economy#isBankMember() is not implemented");
    }

    @Override
    public List<String> getBanks() {
        return Collections.emptyList();
    }

    @Override
    public boolean createPlayerAccount(String playerName) {
        final String actualInput = recoverType(playerName);
        return loggingAction(
                actualInput + ".createPlayerAccount",
                () -> EconomyPlus.getDBType().createPlayer(actualInput)
        );
    }

    @Override
    public boolean createPlayerAccount(OfflinePlayer player) {
        return EconomyPlus.getDBType().createPlayer(Selector.playerToString(player));
    }

    @Override
    public boolean createPlayerAccount(String playerName, String worldName) {
        return EconomyPlus.getDBType().createPlayer(playerName);
    }

    @Override
    public boolean createPlayerAccount(OfflinePlayer player, String worldName) {
        return EconomyPlus.getDBType().createPlayer(Selector.playerToString(player));
    }

    private <T> T loggingAction(
            String actionTrace,
            Supplier<T> action
    ) {
        if (EconomyPlus.debugMode) {
            plugin.getLogger().info(actionTrace);
        }

        final T result = action.get();

        if (EconomyPlus.debugMode) {
            plugin.getLogger().info(() -> "Result: " + result);
        }

        return result;
    }

    /**
     * Fixes if the supplied string is a user but was expecting a UUID
     */
    private String recoverType(String input) {
        switch (EconomyPlus.getStorageMode()) {
            case NICKNAME:
                return input;
            case UUID:
            default:
                try {
                    return input;
                } catch (IllegalArgumentException e) {
                    return Objects.requireNonNull(Bukkit.getPlayer(input)).getUniqueId().toString();
                }
        }
    }
}