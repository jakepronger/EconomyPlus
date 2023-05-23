package me.itswagpvp.economyplus.hooks.events;

import me.itswagpvp.economyplus.hooks.vault.Economy;
import me.itswagpvp.economyplus.listeners.PlayerHandler;

import org.bukkit.OfflinePlayer;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PlayerBankChangeEvent extends Event implements Cancellable {

    private static final HandlerList HANDLERS_LIST = new HandlerList();

    private boolean isCancelled;

    private final String player;
    private final double newBank;
    private final double oldBank;

    public PlayerBankChangeEvent(String player, double newBank) {
        this.player = player;
        this.oldBank = new Economy(PlayerHandler.getPlayer(player)).getBank();
        this.newBank = newBank;
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

    public double newBank() {
        return newBank;
    }
    public double oldBank() {
        return oldBank;
    }

    public double getAmount() {
        return newBank - oldBank;
    }

    public OfflinePlayer getPlayer() {
        return PlayerHandler.getPlayer(player);
    }

}
