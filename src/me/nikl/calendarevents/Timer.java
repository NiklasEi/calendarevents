package me.nikl.calendarevents;

import org.bukkit.scheduler.BukkitRunnable;

/**
 * Created by niklas on 1/24/17.
 *
 *
 */
public class Timer extends BukkitRunnable{
	private EventsManager eventsManager;
	
	public Timer(Main plugin){
		this.eventsManager = plugin.getEventsManager();
		
		this.runTaskTimerAsynchronously(plugin, 0, 5);
	}
	
	
	@Override
	public void run() {
		
	}
}
