package me.nikl.calendarevents;

import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import java.util.ArrayList;

/**
 * Event called at a specified date and time.
 * Use getLabels() and check for the label you are looking for.
 * The label is the key of the event in the configuration file of the plugin 'CalendarEvents'
 */
public class CalendarEvent extends Event implements Cancellable{
	private static final HandlerList handlers = new HandlerList();
	private boolean cancelled;
	private ArrayList labels;
	
	public CalendarEvent(ArrayList labels){
		this.labels = labels;
	}
	
	public ArrayList getLabels(){
		return this.labels;
	}
	
	@Override
	public HandlerList getHandlers() {
		return handlers;
	}
	
	@SuppressWarnings("unused")
	public static HandlerList getHandlerList(){
		return handlers;
	}
	
	@Override
	public boolean isCancelled() {
		return this.cancelled;
	}
	
	@Override
	public void setCancelled(boolean cancel) {
		this.cancelled = cancel;
	}
}
