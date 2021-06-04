package com.zachduda.tonicfoods;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class TonicFoodGiveEvent extends Event implements Cancellable {
	
	private final CommandSender sender;
	private final Player target;
	private final int amount;
	private final String food;
    private boolean isCancelled;

    public TonicFoodGiveEvent(CommandSender sender, Player target, int amount, String food) {
        this.sender = sender;
        this.target = target;
        this.amount = amount;
        this.food = food;
        this.isCancelled = false;
    }

    public boolean isCancelled() {
        return this.isCancelled;
    }

    public void setCancelled(boolean isCancelled) {
        this.isCancelled = isCancelled;
    }

    private static final HandlerList HANDLERS = new HandlerList();

    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    public CommandSender getSender() {
        return this.sender;
    }
    
    public String getFood() {
    	return this.food;
    }
    
    public int getAmount() {
    	return this.amount;
    }
    
    public Player getTarget() {
    	return this.target;
    }

}
