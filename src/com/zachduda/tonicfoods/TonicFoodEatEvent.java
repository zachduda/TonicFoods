package com.zachduda.tonicfoods;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;

public class TonicFoodEatEvent extends Event implements Cancellable {
	
	private final Player target;
	private final ItemStack item;
    private boolean isCancelled;

    public TonicFoodEatEvent(Player target, ItemStack item) {
        this.target = target;
        this.item = item;
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
    
    public ItemStack getItem() {
    	return this.item;
    }
    
    public Player getTarget() {
    	return this.target;
    }

}
