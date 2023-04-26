package me.itswagpvp.economyplus.vault;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import me.itswagpvp.economyplus.EconomyPlus;
import me.itswagpvp.economyplus.database.misc.Selector;
import me.itswagpvp.economyplus.hooks.events.PlayerBalanceChangeEvent;
import me.itswagpvp.economyplus.hooks.events.PlayerBankChangeEvent;

public class Economy extends VEconomy {

    private final OfflinePlayer player;

    public Economy(OfflinePlayer player) {
        super(EconomyPlus.plugin);
        this.player = player;
    }

    // Returns the money of a player
    public Double getBalance() {
        return super.getBalance(Selector.playerToString(player));
    }

    // Set the money of a player
    public void setBalance(Double money) {

        PlayerBalanceChangeEvent event = new PlayerBalanceChangeEvent(Selector.playerToString(player), money);
        Bukkit.getPluginManager().callEvent(event);

        if (event.isCancelled()) return;

        EconomyPlus.getDBType().setTokens(Selector.playerToString(player), money);
    }

    // Add moneys to a player account
    public void addBalance(Double money) {

        PlayerBalanceChangeEvent event = new PlayerBalanceChangeEvent(Selector.playerToString(player), getBalance() + money);
        Bukkit.getPluginManager().callEvent(event);

        if (event.isCancelled()) return;
        super.depositPlayer(Selector.playerToString(player), money);
    }

    // Remove moneys from a player's account
    public void takeBalance(Double money) {

        PlayerBalanceChangeEvent event = new PlayerBalanceChangeEvent(Selector.playerToString(player), getBalance() - money);
        Bukkit.getPluginManager().callEvent(event);

        if (event.isCancelled()) return;
        super.withdrawPlayer(Selector.playerToString(player), money);
    }

    // Returns the player bank
    public double getBank() {
        return new Economy(player).getBank();
    }

    // Set player's bank to the constructor value
    public void setBank(Double money) {

        PlayerBankChangeEvent event = new PlayerBankChangeEvent(Selector.playerToString(player), money);
        Bukkit.getPluginManager().callEvent(event);

        if (event.isCancelled()) return;

        EconomyPlus.getDBType().setBank(Selector.playerToString(player), money);
    }

    // Controls if the player has enough moneys
    public boolean detractable(Double money) {
        return has(player, money);
    }

}