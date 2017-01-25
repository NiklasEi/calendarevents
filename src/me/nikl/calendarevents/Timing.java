package me.nikl.calendarevents;

import org.bukkit.Bukkit;

import java.sql.Date;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.ArrayList;

/**
 * Created by niklas on 1/25/17.
 *
 * class to store the timings of a CalendarEvent
 */
public class Timing {
	private ArrayList<String> days;
	private ArrayList<String> dates;
	private ArrayList<String> monthlyDates;
	private ArrayList<String> yearlyDates;
	private ArrayList<String> times;
	
	private long nextMilli;
	
	public Timing(){
		days = new ArrayList<>();
		monthlyDates = new ArrayList<>();
		yearlyDates = new ArrayList<>();
		dates = new ArrayList<>();
		times = new ArrayList<>();
		
		
		// testing
		java.sql.Date date = new Date(System.currentTimeMillis());
		Bukkit.getConsoleSender().sendMessage("java.sql.Date initiated with currentTimeMillis(): " + date.toString());
		
		LocalDateTime localDateTime = LocalDateTime.now();
		ZonedDateTime zonedDateTime = ZonedDateTime.now();
		
		ZonedDateTime myDate = ZonedDateTime.of(1993,8,18,0,0,0,0,ZonedDateTime.now().getZone());
		long millis = myDate.toInstant().toEpochMilli();
		
		
		Bukkit.getConsoleSender().sendMessage("LocalDateTime: " + localDateTime.toString());
		Bukkit.getConsoleSender().sendMessage("ZonedDateTime: " + zonedDateTime.toString());
	}
	
	public void addDay(String day){
		days.add(day);
	}
	
	public void addTime(String time){
		times.add(time);
	}
	
	public void addDate(String date){
		dates.add(date);
	}
	public void addMonthlyDate(String date){
		monthlyDates.add(date);
	}
	public void addYearlyDate(String date){
		yearlyDates.add(date);
	}
	
	public void setUp(){
		if(Main.debug){
			Bukkit.getConsoleSender().sendMessage("days: " + days.toString());
			Bukkit.getConsoleSender().sendMessage("dates: " + dates.toString());
			Bukkit.getConsoleSender().sendMessage("monthlyDates: " + monthlyDates.toString());
			Bukkit.getConsoleSender().sendMessage("yearlyDates: " + yearlyDates.toString());
			Bukkit.getConsoleSender().sendMessage("times: " + times.toString());
		}
	}
	
	
}
