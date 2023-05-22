package me.itswagpvp.economyplus.hooks.events;

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

    public PlayerBalanceChangeEvent(String player, double newBalance) {
        this.player = player;
        this.newBalance = newBalance;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

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

    public double getNewBalance() {
        return newBalance;
    }

    public void setNewBalance(double newBalance) {
        this.newBalance = newBalance;
    }

    public OfflinePlayer getPlayer() {
        return PlayerHandler.getPlayer(player);
    }
}
