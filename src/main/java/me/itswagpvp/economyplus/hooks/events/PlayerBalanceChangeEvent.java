package me.itswagpvp.economyplus.hooks.events;

import me.itswagpvp.economyplus.hooks.vault.Economy;
import me.itswagpvp.economyplus.listeners.PlayerHandler;

import org.bukkit.OfflinePlayer;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PlayerBalanceChangeEvent extends Event implements Cancellable {

    private static final HandlerList HANDLERS = new HandlerList();

    private final String player;
    private boolean isCancelled;
    private double newBalance;
    private double oldBalance;

    public PlayerBalanceChangeEvent(String player, double newBalance) {
        this.player = player;
        this.oldBalance = new Economy(PlayerHandler.getPlayer(player)).getBalance();
        this.newBalance = newBalance;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    @Override
    public boolean isCancelled() {
        return isCancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        isCancelled = cancel;
    }

    public double newBalance() {
        return newBalance;
    }
    public double oldBalance() {
        return oldBalance;
    }

    public double getAmount() {
        return newBalance - oldBalance;
    }

    public OfflinePlayer getPlayer() {
        return PlayerHandler.getPlayer(player);
    }

}
