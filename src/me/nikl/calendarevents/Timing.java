package me.nikl.calendarevents;

import org.bukkit.Bukkit;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by niklas on 1/25/17.
 *
 * class to store the timings of a CalendarEvent
 */
class Timing {
	private ArrayList<Integer> days;
	private ArrayList<String> dates;
	private ArrayList<String> monthlyDates;
	private ArrayList<String> yearlyDates;
	private ArrayList<String> times;
	
	long nextCall;
	
	private Set<Long> relevantMillis;
	private Set<ZonedDateTime> relevantDates;
	private ZoneId zone;
	private int currentMonth, currentYear, currentMonthDay, currentWeekDay;
	
	
	Timing(){
		days = new ArrayList<>();
		monthlyDates = new ArrayList<>();
		yearlyDates = new ArrayList<>();
		dates = new ArrayList<>();
		times = new ArrayList<>();
		
		
		relevantDates = new HashSet<>();
		relevantMillis = new HashSet<>();
		
		ZonedDateTime now = ZonedDateTime.now();
		currentMonth = now.getMonthValue();
		currentYear = now.getYear();
		currentMonthDay = now.getDayOfMonth();
		currentWeekDay = now.getDayOfWeek().getValue();
		zone = ZoneId.systemDefault();
	}
	
	void addDay(Integer day){
		days.add(day);
	}
	
	void addTime(String time){
		times.add(time);
	}
	
	void addDate(String date){
		dates.add(date);
	}
	void addMonthlyDate(String date){
		monthlyDates.add(date);
	}
	void addYearlyDate(String date){
		yearlyDates.add(date);
	}
	
	/**
	 * Set up the relevant dates
	 */
	public void setUp(){
		if(Main.debug){
			Bukkit.getConsoleSender().sendMessage("days: " + days.toString());
			Bukkit.getConsoleSender().sendMessage("dates: " + dates.toString());
			Bukkit.getConsoleSender().sendMessage("monthlyDates: " + monthlyDates.toString());
			Bukkit.getConsoleSender().sendMessage("yearlyDates: " + yearlyDates.toString());
			Bukkit.getConsoleSender().sendMessage("times: " + times.toString());
		}
		// This loads all relevant dates including dates that are in the past
		// This is for future plans of making it possible to run missed events
		if(!dates.isEmpty()){
			for(String date : dates){
				int day, month, year;
				String[] dateParts = date.split("\\.");
				try{
					day = Integer.parseInt(dateParts[0]);
					month = Integer.parseInt(dateParts[1]);
					year = Integer.parseInt(dateParts[2]);
				} catch (Exception e) {
					// cant happen since this was already checked on load
					continue;
				}
				relevantDates.add(ZonedDateTime.of(year,month,day,0,0,0,0,zone));
			}
		}
		if(!monthlyDates.isEmpty()){
			for(String date : monthlyDates){
				int day;
				try{
					day = Integer.parseInt(date);
				} catch (Exception e) {
					// cant happen since this was already checked on load
					continue;
				}
				relevantDates.add(ZonedDateTime.of(currentYear,currentMonth,day,0,0,0,0,zone));
				relevantDates.add(ZonedDateTime.of(currentYear,currentMonth,day,0,0,0,0,zone).plusMonths(1));
				relevantDates.add(ZonedDateTime.of(currentYear,currentMonth,day,0,0,0,0,zone).minusMonths(1));
			}
		}
		if(!yearlyDates.isEmpty()){
			for(String date : yearlyDates){
				int day, month;
				String[] dateParts = date.split("\\.");
				try{
					day = Integer.parseInt(dateParts[0]);
					month = Integer.parseInt(dateParts[1]);
				} catch (Exception e) {
					// cant happen since this was already checked on load
					continue;
				}
				relevantDates.add(ZonedDateTime.of(currentYear,month,day,0,0,0,0,zone));
				relevantDates.add(ZonedDateTime.of(currentYear,month,day,0,0,0,0,zone).plusYears(1));
				relevantDates.add(ZonedDateTime.of(currentYear,month,day,0,0,0,0,zone).minusYears(1));
			}
		}
		if(!days.isEmpty()){
			int diffDay;
			for(int day : days){
				diffDay = day - currentWeekDay;
				if(diffDay == 0) {
					relevantDates.add(ZonedDateTime.of(currentYear, currentMonth, currentMonthDay, 0, 0, 0, 0, zone));
					relevantDates.add(ZonedDateTime.of(currentYear, currentMonth, currentMonthDay, 0, 0, 0, 0, zone).plusWeeks(1));
					relevantDates.add(ZonedDateTime.of(currentYear, currentMonth, currentMonthDay, 0, 0, 0, 0, zone).minusWeeks(1));
				} else if (diffDay > 0) {
					relevantDates.add(ZonedDateTime.of(currentYear,currentMonth,currentMonthDay,0,0,0,0,zone).plusDays(diffDay));
					relevantDates.add(ZonedDateTime.of(currentYear,currentMonth,currentMonthDay,0,0,0,0,zone).plusDays(diffDay).plusWeeks(1));
					relevantDates.add(ZonedDateTime.of(currentYear,currentMonth,currentMonthDay,0,0,0,0,zone).plusDays(diffDay).minusWeeks(1));
				} else {
					relevantDates.add(ZonedDateTime.of(currentYear,currentMonth,currentMonthDay,0,0,0,0,zone).minusDays(diffDay));
					relevantDates.add(ZonedDateTime.of(currentYear,currentMonth,currentMonthDay,0,0,0,0,zone).minusDays(diffDay).plusWeeks(1));
					relevantDates.add(ZonedDateTime.of(currentYear,currentMonth,currentMonthDay,0,0,0,0,zone).minusDays(diffDay).minusWeeks(1));
				}
			}
		}
		if(Main.debug)Bukkit.getConsoleSender().sendMessage("loaded " + relevantDates.size() + " days");
		
		for(ZonedDateTime date : relevantDates){
			for(String timeString : times){
				String[] timeParts = timeString.split(":");
				int hour, min;
				try{
					hour = Integer.parseInt(timeParts[0]);
					min = Integer.parseInt(timeParts[1]);
				} catch (Exception e){
					// can't happen, is parsed before on load
					continue;
				}
				relevantMillis.add(date.plusHours(hour).plusMinutes(min).toInstant().toEpochMilli());
			}
		}
		setNextMilli();
	}
	
	void setNextMilli(){
		long currentMillis = System.currentTimeMillis(), toReturn = Long.MAX_VALUE;
		for(Long milli : relevantMillis){
			if(currentMillis > milli) continue;
			if(milli < toReturn){
				toReturn = milli;
			}
		}
		if(Main.debug)Bukkit.getConsoleSender().sendMessage("next date to schedule: " + ZonedDateTime.ofInstant(Instant.ofEpochMilli(toReturn), zone).toString());
		nextCall = toReturn;
	}
	
	long getNextCall(){
		return this.nextCall;
	}
	
	
	
}
