package me.nikl.calendarevents;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import java.time.ZonedDateTime;
import java.util.*;
import java.util.logging.Level;

/**
 * Created by niklas on 1/24/17.
 *
 * load events from config file
 * keeps set of labels
 *
 * Events are called by this class
 */
public class EventsManager {
	private FileConfiguration config;
	private FileConfiguration data;
	
	private Set<String> labels;
	private Map<String, Timing> timings;
	private Map<String, Long> events;
	private Map<Long, ArrayList<String>> toCall;
	
	private int currentMonth, currentYear;
	
	private Timer timer;
	
	
	public EventsManager(Main plugin){
		toCall = new HashMap<>();
		labels = new HashSet<>();
		
		this.timer = new Timer(plugin);
		
		config = plugin.getConfig();
		ZonedDateTime now = ZonedDateTime.now();
		
		currentMonth = now.getMonth().getValue();
		currentYear = now.getYear();
		
		loadEvents();
	}
	
	private void loadEvents() {
		if(!config.isConfigurationSection("events")) return;
		ConfigurationSection events = config.getConfigurationSection("events");
		events:
		for (String key : events.getKeys(false)){
			if(!events.isString(key + ".occasionString") || !events.isString(key + ".time")){
				Bukkit.getLogger().log(Level.WARNING, "Could not load the event '" + key + "'");
				Bukkit.getLogger().log(Level.WARNING, "...  Skipping the event  ...");
				continue events;
			}
			
			Timing timing = new Timing();
			
			
			/*
			  from here on handle occasions and load them into the timing object
			 */
			String occasionString = events.getString(key + ".occasionString");
			
			handleOccasion:
			{
				if (occasionString.equalsIgnoreCase("every day")) {
					timing.addDay("monday");
					timing.addDay("tuesday");
					timing.addDay("wednesday");
					timing.addDay("thursday");
					timing.addDay("friday");
					timing.addDay("saturday");
					timing.addDay("sunday");
					break handleOccasion;
				}
				
				occasionString = occasionString.replaceAll(" ", "");
				String[] occasions = occasionString.split(",");
				
				singleOccasion:
				for(String singleOccasion : occasions) {
					if (occasionString.split(".").length == 1) {
						// month-date or days
						if (singleOccasion.equalsIgnoreCase("monday") || singleOccasion.equalsIgnoreCase("tuesday") || singleOccasion.equalsIgnoreCase("wednesday") || singleOccasion.equalsIgnoreCase("thursday") || singleOccasion.equalsIgnoreCase("friday") || singleOccasion.equalsIgnoreCase("saturday") || singleOccasion.equalsIgnoreCase("sunday")) {
							
							timing.addDay(singleOccasion.toLowerCase());
							continue singleOccasion;
							
						} else if (singleOccasion.length() == 2) {
							try {
								Integer.parseInt(singleOccasion);
							} catch (Exception e) {
								Bukkit.getLogger().log(Level.WARNING, "Could not load the date '" + singleOccasion + "' in the event '" + key + "'");
								Bukkit.getLogger().log(Level.WARNING, "...  Skipping the event  ...");
								continue events;
							}
							// NOTE: plugin would have to reload every new month for this to work proper
							// ToDo: better way of handling those dates?
							timing.addMonthlyDate(singleOccasion);
						} else {
							Bukkit.getLogger().log(Level.WARNING, "Could not load the date '" + singleOccasion + "' in the event '" + key + "'");
							Bukkit.getLogger().log(Level.WARNING, "...  Skipping the event  ...");
							continue events;
						}
					} else {
						String[] dateParts = singleOccasion.split(".");
						if (dateParts.length == 2) {
							// year-date
							if (!(dateParts[0].length() == 2 && dateParts[1].length() == 2)) {
								Bukkit.getLogger().log(Level.WARNING, "Could not load the date '" + singleOccasion + "' in the event '" + key + "'");
								Bukkit.getLogger().log(Level.WARNING, "...  Skipping the event  ...");
								continue events;
							}
							for (String datePart : dateParts) {
								try {
									Integer.parseInt(datePart);
								} catch (Exception e) {
									Bukkit.getLogger().log(Level.WARNING, "Could not load the date '" + singleOccasion + "' in the event '" + key + "'");
									Bukkit.getLogger().log(Level.WARNING, "...  Skipping the event  ...");
									continue events;
								}
							}
							timing.addYearlyDate(singleOccasion);
							
						} else if (dateParts.length == 3) {
							// full-date
							if (!(dateParts[0].length() == 2 && dateParts[1].length() == 2 && dateParts[2].length() == 4)) {
								Bukkit.getLogger().log(Level.WARNING, "Could not load the date '" + singleOccasion + "' in the event '" + key + "'");
								Bukkit.getLogger().log(Level.WARNING, "...  Skipping the event  ...");
								continue events;
							}
							for (String datePart : dateParts) {
								try {
									Integer.parseInt(datePart);
								} catch (Exception e) {
									Bukkit.getLogger().log(Level.WARNING, "Could not load the date '" + singleOccasion + "' in the event '" + key + "'");
									Bukkit.getLogger().log(Level.WARNING, "...  Skipping the event  ...");
									continue events;
								}
							}
							timing.addDate(singleOccasion);
							
							
						} else {
							Bukkit.getLogger().log(Level.WARNING, "Could not load the date '" + singleOccasion + "' in the event '" + key + "'");
							Bukkit.getLogger().log(Level.WARNING, "...  Skipping the event  ...");
							continue events;
						}
					}
				}
			}
			
			
			
			/*
			  read timings
			 */
			String timeString = events.getString(key + ".time").replaceAll(" ","");
			String[] times = timeString.split(",");
			
			int shortTermInt1, shortTermInt2;
			
			timesLoop:
			for (String time : times){
				String[] timeParts = time.split(":");
				if(timeParts.length !=2 || timeParts[0].length() != 2 || timeParts[1].length() != 2){
					Bukkit.getLogger().log(Level.WARNING, "Could not load the time '" + time + "' in the event '" + key + "'");
					Bukkit.getLogger().log(Level.WARNING, "...  Skipping the event  ...");
					continue events;
				}
				try {
					shortTermInt1 = Integer.parseInt(timeParts[0]);
					shortTermInt2 = Integer.parseInt(timeParts[1]);
					
					if(shortTermInt1 > 23 || shortTermInt1 < 0 || shortTermInt2 > 59 || shortTermInt2 < 0){
						Bukkit.getLogger().log(Level.WARNING, "Could not load the time '" + time + "' in the event '" + key + "'");
						Bukkit.getLogger().log(Level.WARNING, "...  Skipping the event  ...");
						continue events;
					}
				} catch (Exception e) {
					Bukkit.getLogger().log(Level.WARNING, "Could not load the time '" + time + "' in the event '" + key + "'");
					Bukkit.getLogger().log(Level.WARNING, "...  Skipping the event  ...");
					continue events;
				}
				
				timing.addTime(time);
			}
			if(Main.debug) Bukkit.getConsoleSender().sendMessage("Listing loaded dates and times from: " + key);
			timing.setUp();
			if(Main.debug) Bukkit.getConsoleSender().sendMessage("**********************************************");
			timings.put(key, timing);
		}
	}
}
