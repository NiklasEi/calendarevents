package me.nikl.calendarevents;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

/**
 * Created by niklas on 1/24/17.
 *
 *
 */
public class EventListener implements Listener {
	
	
	@EventHandler
	public void onCalendarEvent(CalendarEvent event){
		if(Main.debug)
			Bukkit.getConsoleSender().sendMessage("event called: " + event.getLabels().toString());
	}
}
