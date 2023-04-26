package me.itswagpvp.economyplus.hooks.events;

import me.itswagpvp.economyplus.PlayerHandler;
import org.bukkit.OfflinePlayer;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PlayerBankChangeEvent extends Event implements Cancellable {

    private static final HandlerList HANDLERS_LIST = new HandlerList();
    private final String player;
    private boolean isCancelled;
    private double newBank;

    public PlayerBankChangeEvent(String player, double newBank) {
        this.player = player;
        this.newBank = newBank;
        this.isCancelled = false;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS_LIST;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS_LIST;
    }

    @Override
    public boolean isCancelled() {
        return isCancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        isCancelled = cancel;
    }

    public double getNewBank() {
        return newBank;
    }

    public void setNewBank(double newBank) {
        this.newBank = newBank;
    }

    public OfflinePlayer getPlayer() {
        return PlayerHandler.getPlayer(player);
    }
}