package me.nikl.calendarevents;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.HandlerList;
import org.bukkit.scheduler.BukkitRunnable;

import java.time.Instant;
import java.time.ZoneId;
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
class EventsManager implements APICalendarEvents {
	private Main plugin;
	private FileConfiguration config;
	private Map<String, Timing> timings;
	private EventListener eventListener;
	
	EventsManager(Main plugin){
		this.plugin = plugin;
		config = plugin.getConfig();
		timings = new HashMap<>();
		loadEventsFromConfig();

		this.eventListener = new EventListener(plugin, timings.keySet());
		Bukkit.getServer().getPluginManager().registerEvents(eventListener, plugin);
	}
	
	private void loadEventsFromConfig() {
		if(!config.isConfigurationSection("events")) return;
		ConfigurationSection events = config.getConfigurationSection("events");
		Main.debug("***********************************************************************************");
		for (String key : events.getKeys(false)){
			if(!events.isString(key + ".timing.occasion") || !events.isString(key + ".timing.time")){
				Bukkit.getLogger().log(Level.WARNING, "Could not load the event '" + key + "'");
				Bukkit.getLogger().log(Level.WARNING, "Due to missing occasion or timing.");
				Bukkit.getLogger().log(Level.WARNING, "...  Skipping the event  ...");
				Main.debug("***********************************************************************************");
				continue;
			}
			if(timings.containsKey(key)){
				Bukkit.getLogger().log(Level.WARNING, "There is already an event with the label '" + key + "'");
				Bukkit.getLogger().log(Level.WARNING, "...  Skipping the event  ...");
				Main.debug("***********************************************************************************");
				continue;
			}
			Timing timing = new Timing(key, this);

			// from here on handle occasions and load them into the timing object
			String occasionString = events.getString(key + ".timing.occasion");
			if(!loadOccasion(timing, key, occasionString)){
				continue;
			}
			String timeString = events.getString(key + ".timing.time");
			if(!loadTimings(timing, key, timeString)){
				continue;
			}

			// additional debugging inside timing setup
			Main.debug("Listing loaded dates and times from: " + key);
			timing.setUp();
			Main.debug("***********************************************************************************");

		    // important: keep setNextMilli behind adding to the Map
		    // since setNextMilli can remove it again if all dates are in the past
			timings.put(key, timing);
			timing.setNextMilli();
		}
	}

	public void reload(){
		Map<String, Timing> apiRegisteredTimings = getAllApiRegisteredTimings();
		HandlerList.unregisterAll(eventListener);
		plugin.reloadConfiguration();
		config = plugin.getConfig();
		timings.clear();
		timings.putAll(apiRegisteredTimings);
		loadEventsFromConfig();
		this.eventListener = new EventListener(plugin, timings.keySet());
		Bukkit.getServer().getPluginManager().registerEvents(eventListener, plugin);
	}

	private boolean loadTimings(Timing timing, String label, String timeString){
		String[] times = timeString.replaceAll(" ","").split(",");
		times = handlePlaceholders(times);
		int shortTermInt1, shortTermInt2;
		for (String time : times){
			String[] timeParts = time.split(":");
			if(timeParts.length !=2 || timeParts[0].length() != 2 || timeParts[1].length() != 2){
				Bukkit.getLogger().log(Level.WARNING, "Could not load the time '" + time + "' in the event '" + label + "'");
				Bukkit.getLogger().log(Level.WARNING, "...  Skipping the event  ...");
				return false;
			}
			try {
				shortTermInt1 = Integer.parseInt(timeParts[0]);
				shortTermInt2 = Integer.parseInt(timeParts[1]);

				if(shortTermInt1 > 23 || shortTermInt1 < 0 || shortTermInt2 > 59 || shortTermInt2 < 0){
					Bukkit.getLogger().log(Level.WARNING, "Could not load the time '" + time + "' in the event '" + label + "'");
					Bukkit.getLogger().log(Level.WARNING, "Timing has invalid values! Use 24h format.");
					Bukkit.getLogger().log(Level.WARNING, "...  Skipping the event  ...");
					return false;
				}
			} catch (Exception e) {
				Bukkit.getLogger().log(Level.WARNING, "Could not load the time '" + time + "' in the event '" + label + "'");
				Bukkit.getLogger().log(Level.WARNING, "Timing has invalid values! Use integers.");
				Bukkit.getLogger().log(Level.WARNING, "...  Skipping the event  ...");
				return false;
			}
			timing.addTime(time);
		}
		return true;
	}

	private String[] handlePlaceholders(String[] times) {
		HashSet<String> toReturn = new HashSet<>();
		// support X and x
		for(int i = 0; i < times.length; i++){
			times[i] = times[i].replace("X", "x");
		}
		ArrayList<String> current = new ArrayList<>();
		for (String timing : times){
			current.clear();
			if(!timing.contains("x")){
				toReturn.add(timing);
				continue;
			}
			current.add(timing);
			ListIterator<String > iterator = current.listIterator(1);
			while (iterator.hasPrevious()) {
				String currentTiming = iterator.previous();

				// check for placeholder and replace it
				if (currentTiming.charAt(0) == 'x') {
					// handle placeholder in 10 hour slot
					iterator.remove();
					iterator.add(currentTiming.replaceFirst("x", "0"));
					iterator.add(currentTiming.replaceFirst("x", "1"));
					if (currentTiming.charAt(1) == 'x' || currentTiming.charAt(1) == '0' || currentTiming.charAt(1) == '1'
							|| currentTiming.charAt(1) == '2' || currentTiming.charAt(1) == '3') {
						iterator.add(currentTiming.replaceFirst("x", "2"));
					}
				} else if (currentTiming.charAt(1) == 'x') {
					// handle placeholder in hour slot
					iterator.remove();
					iterator.add(currentTiming.replaceFirst("x", "0"));
					iterator.add(currentTiming.replaceFirst("x", "1"));
					iterator.add(currentTiming.replaceFirst("x", "2"));
					iterator.add(currentTiming.replaceFirst("x", "3"));
					if (currentTiming.charAt(0) == '0' || currentTiming.charAt(0) == '1') {
						iterator.add(currentTiming.replaceFirst("x", "4"));
						iterator.add(currentTiming.replaceFirst("x", "5"));
						iterator.add(currentTiming.replaceFirst("x", "6"));
						iterator.add(currentTiming.replaceFirst("x", "7"));
						iterator.add(currentTiming.replaceFirst("x", "8"));
						iterator.add(currentTiming.replaceFirst("x", "9"));
					}
				} else if (currentTiming.charAt(3) == 'x') {
					// handle placeholder in 10 minute slot
					iterator.remove();
					iterator.add(currentTiming.replaceFirst("x", "0"));
					iterator.add(currentTiming.replaceFirst("x", "1"));
					iterator.add(currentTiming.replaceFirst("x", "2"));
					iterator.add(currentTiming.replaceFirst("x", "3"));
					iterator.add(currentTiming.replaceFirst("x", "4"));
					iterator.add(currentTiming.replaceFirst("x", "5"));
				} else if (currentTiming.charAt(4) == 'x') {
					iterator.remove();
					// handle placeholder in minute slot
					iterator.add(currentTiming.replaceFirst("x", "0"));
					iterator.add(currentTiming.replaceFirst("x", "1"));
					iterator.add(currentTiming.replaceFirst("x", "2"));
					iterator.add(currentTiming.replaceFirst("x", "3"));
					iterator.add(currentTiming.replaceFirst("x", "4"));
					iterator.add(currentTiming.replaceFirst("x", "5"));
					iterator.add(currentTiming.replaceFirst("x", "6"));
					iterator.add(currentTiming.replaceFirst("x", "7"));
					iterator.add(currentTiming.replaceFirst("x", "8"));
					iterator.add(currentTiming.replaceFirst("x", "9"));
				}
			}
			// current now contains timing with all possible placeholder values
			toReturn.addAll(current);
		}
		String[] arrayToReturn = new String[toReturn.size()];
		Iterator<String> iterator = toReturn.iterator();
		int i = 0;
		while(iterator.hasNext()){
			arrayToReturn[i] = iterator.next();
			i++;
		}
		return arrayToReturn;
	}

	private boolean loadOccasion(Timing timing, String label, String occasionString){
		if (occasionString.equalsIgnoreCase("every day")) {
			timing.addDay(1);
			timing.addDay(2);
			timing.addDay(3);
			timing.addDay(4);
			timing.addDay(5);
			timing.addDay(6);
			timing.addDay(7);
			return true;
		}

		occasionString = occasionString.replaceAll(" ", "");
		String[] occasions = occasionString.split(",");
		Main.debug("occasions: " + Arrays.asList(occasions));

		singleOccasion:
		for(String singleOccasion : occasions) {
			Main.debug("singleOccasion: " + singleOccasion);
			if (!singleOccasion.contains(".")) {
				// month-date or days
				if (singleOccasion.equalsIgnoreCase("monday") || singleOccasion.equalsIgnoreCase("tuesday") || singleOccasion.equalsIgnoreCase("wednesday") || singleOccasion.equalsIgnoreCase("thursday") || singleOccasion.equalsIgnoreCase("friday") || singleOccasion.equalsIgnoreCase("saturday") || singleOccasion.equalsIgnoreCase("sunday")) {
					switch (singleOccasion.toLowerCase()){
						case "monday":
							timing.addDay(1);
							break;
						case "tuesday":
							timing.addDay(2);
							break;
						case "wednesday":
							timing.addDay(3);
							break;
						case "thursday":
							timing.addDay(4);
							break;
						case "friday":
							timing.addDay(5);
							break;
						case "saturday":
							timing.addDay(6);
							break;
						case "sunday":
							timing.addDay(7);
							break;
					}
					continue singleOccasion;

				} else if (singleOccasion.length() == 2) {
					try {
						Integer.parseInt(singleOccasion);
					} catch (Exception e) {
						Bukkit.getLogger().log(Level.WARNING, "Could not load the dd date '" + singleOccasion + "' in the event '" + label + "'");
						Bukkit.getLogger().log(Level.WARNING, "...  Skipping the event  ...");
						Main.debug("***********************************************************************************");
						return false;
					}
					timing.addMonthlyDate(singleOccasion);
				} else {
					Bukkit.getLogger().log(Level.WARNING, "Could not load the dd date or day '" + singleOccasion + "' in the event '" + label + "'");
					Bukkit.getLogger().log(Level.WARNING, "...  Skipping the event  ...");
					Main.debug("***********************************************************************************");
					return false;
				}
			} else {
				String[] dateParts = singleOccasion.split("\\.");
				Main.debug("dateParts: " + Arrays.asList(dateParts));
				if (dateParts.length == 2) {
					// year-date
					if (!(dateParts[0].length() == 2 && dateParts[1].length() == 2)) {
						Bukkit.getLogger().log(Level.WARNING, "Could not load the dd.mm date '" + singleOccasion + "' in the event '" + label + "'");
						Bukkit.getLogger().log(Level.WARNING, "...  Skipping the event  ...");
						Main.debug("***********************************************************************************");
						return false;
					}
					for (String datePart : dateParts) {
						try {
							Integer.parseInt(datePart);
						} catch (Exception e) {
							Bukkit.getLogger().log(Level.WARNING, "Could not load the dd.mm date '" + singleOccasion + "' in the event '" + label + "'");
							Bukkit.getLogger().log(Level.WARNING, "...  Skipping the event  ...");
							Main.debug("***********************************************************************************");
							return false;
						}
					}
					timing.addYearlyDate(singleOccasion);

				} else if (dateParts.length == 3) {
					// full-date
					if (!(dateParts[0].length() == 2 && dateParts[1].length() == 2 && dateParts[2].length() == 4)) {
						Bukkit.getLogger().log(Level.WARNING, "Could not load the date '" + singleOccasion + "' in the event '" + label + "'");
						Bukkit.getLogger().log(Level.WARNING, "...  Skipping the event  ...");
						Main.debug("***********************************************************************************");
						return false;
					}
					for (String datePart : dateParts) {
						try {
							Integer.parseInt(datePart);
						} catch (Exception e) {
							Bukkit.getLogger().log(Level.WARNING, "Could not load the date '" + singleOccasion + "' in the event '" + label + "'");
							Bukkit.getLogger().log(Level.WARNING, "...  Skipping the event  ...");
							Main.debug("***********************************************************************************");
							return false;
						}
					}
					timing.addDate(singleOccasion);

				} else {
					Bukkit.getLogger().log(Level.WARNING, "Could not load the general date '" + singleOccasion + "' in the event '" + label + "'");
					Bukkit.getLogger().log(Level.WARNING, "...  Skipping the event  ...");
					Main.debug("***********************************************************************************");
					return false;
				}
			}
		}
		return true;
	}
	
	private void callEvent(ArrayList<String> labels){
		// labels not empty!
		long callMilli = timings.get(labels.get(0)).getNextCall(), current = System.currentTimeMillis();
		if(callMilli > current){
			Main.debug("rescheduling " + labels.toString() + " by " +((callMilli - current)/50 + 1 )+ "tics");
			new BukkitRunnable(){
				@Override
				public void run() {
					new BukkitRunnable(){
						@Override
						public void run() {
							callEvent(labels);
						}
					}.runTask(plugin);
				}
			}.runTaskLaterAsynchronously(plugin, (callMilli - current)/50 + 1);
			return;
		}
		Main.debug("calling " + labels.toString());
		Bukkit.getPluginManager().callEvent(new CalendarEvent(labels));
		
		// before the next timer checks for new events to call in the next minute.
		// This will ensure that all called events have up to date next millis
		new BukkitRunnable(){
			@Override
			public void run() {
				new BukkitRunnable(){
					@Override
					public void run() {
						for(String label : labels){
							Timing timing = timings.get(label);
							timing.setNextMilli();
						}
					}
				}.runTask(plugin);
			}
		}.runTaskLaterAsynchronously(plugin, 100);
	}
	
	void callNextMinute() {
		ArrayList<String> toCall = new ArrayList<>();
		long currentMillis = System.currentTimeMillis(), milli = 0;
		long diff = 0;
		for(String label : timings.keySet()){
			Timing timing = timings.get(label);
			if(((timing.getNextCall() - currentMillis)/1000 )< 60){
				if(toCall.isEmpty()) {
					milli = timing.getNextCall();
					diff = (milli - currentMillis) / 1000;
				}
				toCall.add(label);
			}
		}
		if(!toCall.isEmpty()){
			Main.debug("scheduling " + toCall.toString() + " for " + ZonedDateTime.ofInstant(Instant.ofEpochMilli(milli), ZoneId.systemDefault()));
			// this call is already pretty accurate (+- a few tics)
			//   it is made more accurate by a recheck of the timings in callEvent
			new BukkitRunnable(){
				@Override
				public void run() {
					new BukkitRunnable(){
						@Override
						public void run() {
							callEvent(toCall);
						}
					}.runTask(plugin);
				}
			}.runTaskLaterAsynchronously(plugin, diff*20 + 10);
			Main.debug("scheduled");
		}
	}
	
	void reCalcNextMillis() {
		timings.values().forEach(Timing::setNextMilli);
	}

	@Override
	public boolean addEvent(String label, String occasions, String timings) {
		if (this.timings.keySet().contains(label)){
			return false;
		}
		Timing timing = new Timing(label, this);
		if(!loadOccasion(timing, label, occasions)){
			return false;
		}
		if(!loadTimings(timing, label, timings)){
			return false;
		}
		Main.debug("Listing loaded dates and times from: " + label);
		timing.setUp();
		Main.debug("***********************************************************************************");
		// important: keep setNextMilli behind adding to the Map
		// since setNextMilli can remove it again if all dates are in the past
		this.timings.put(label, timing);
		timing.setNextMilli();
		return true;
	}

	@Override
	public void removeEvent(String label){
		this.timings.remove(label);
	}

    int getNumberOfEvents() {
		return timings.keySet().size();
    }

	private Map<String,Timing> getAllApiRegisteredTimings() {
		HashMap<String, Timing> toReturn = new HashMap<>();
		for(String label : timings.keySet()){
			if(config.isConfigurationSection("events." + label)) continue;
			toReturn.put(label, timings.get(label));
		}
		return toReturn;
	}
}
