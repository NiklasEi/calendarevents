package me.nikl.calendarevents;

import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import java.time.ZonedDateTime;
import java.util.ArrayList;

/**
 * Event called at a specified date and time.
 * Use {@link CalendarEvent#getLabels()} and check for the label you are looking for.
 * The label is the key of the event in the configuration file of the plugin 'CalendarEvents'
 */
public class CalendarEvent extends Event implements Cancellable{
	private static final HandlerList handlers = new HandlerList();
	private boolean cancelled;
	private ArrayList<String> labels;
	private int hour, minute, year;
	private String month;
	
	public CalendarEvent(ArrayList labels){
		this.labels = labels;
		ZonedDateTime now = ZonedDateTime.now();
		this.minute = now.getSecond() < 20?now.getMinute(): (now = now.plusMinutes(1)).getMinute();
		this.hour = now.getHour();
		this.year = now.getYear();
		this.month = now.getMonth().toString();
	}
	
	/**
	 * Get a list containing all labels this event was called with
	 *
	 * a label is the name of the event in the configuration file of the plugin CalendarEvents
	 * @return labels
	 */
	public ArrayList<String> getLabels(){
		return this.labels;
	}
	
	/**
	 * get the hour this event is called in
	 * @return hour
	 */
	public int getHour(){
		return this.hour;
	}
	
	/**
	 * get the month the event was called in
	 * @return month
	 */
	public String getMonth(){
		return this.month;
	}
	
	/**
	 * get the time in the format hh:mm (24h cycle)
	 * @return hh:mm
	 */
	public String getTime(){
		return (this.hour<10? "0" + this.hour: String.valueOf(this.hour)) + ":" + (this.minute<10? "0" + this.minute: String.valueOf(this.minute));
	}
	
	/**
	 * get the year the event was called in
	 * @return year
	 */
	public int getYear(){
		return this.year;
	}
	
	/**
	 * get the minute this event is called in
	 * @return minute
	 */
	public int getMinute(){
		return this.minute;
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
