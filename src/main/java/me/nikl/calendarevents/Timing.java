package me.nikl.calendarevents;

import org.bukkit.ChatColor;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * @author Niklas Eicker
 *
 * class to load, store and manage the timings of a CalendarEvent
 */
class Timing {
    private long nextCall;
    private String label;
    private EventsManager eventsManager;
    private ArrayList<Integer> days;
    private ArrayList<String> dates;
    private ArrayList<String> monthlyDates;
    private ArrayList<String> yearlyDates;
    private ArrayList<String> times;
    private Set<Long> relevantMillis;
    private Set<ZonedDateTime> relevantDates;
    private ZoneId zone;

    Timing(String label, EventsManager eventsManager) {
        this.label = label;
        this.eventsManager = eventsManager;

        days = new ArrayList<>();
        monthlyDates = new ArrayList<>();
        yearlyDates = new ArrayList<>();
        dates = new ArrayList<>();
        times = new ArrayList<>();


        relevantDates = new HashSet<>();
        relevantMillis = new HashSet<>();
    }

    void addDay(Integer day) {
        days.add(day);
    }

    void addTime(String time) {
        times.add(time);
    }

    void addDate(String date) {
        dates.add(date);
    }

    void addMonthlyDate(String date) {
        monthlyDates.add(date);
    }

    void addYearlyDate(String date) {
        yearlyDates.add(date);
    }

    /**
     * Set up the relevant dates
     */
    public void setUp() {
        CalendarEvents.debug("days: " + days.toString());
        CalendarEvents.debug("dates: " + dates.toString());
        CalendarEvents.debug("monthlyDates: " + monthlyDates.toString());
        CalendarEvents.debug("yearlyDates: " + yearlyDates.toString());
        CalendarEvents.debug("times: " + times.toString());

        // get references for current time (need to renew)
        ZonedDateTime now = ZonedDateTime.now();
        int currentMonth = now.getMonthValue();
        int currentYear = now.getYear();
        int currentMonthDay = now.getDayOfMonth();
        int currentWeekDay = now.getDayOfWeek().getValue();
        zone = ZoneId.systemDefault();

        // This loads all relevant dates including dates that are in the past
        // This is for future plans of making it possible to run missed events
        if (!dates.isEmpty()) {
            for (String date : dates) {
                int day, month, year;
                String[] dateParts = date.split("\\.");
                try {
                    day = Integer.parseInt(dateParts[0]);
                    month = Integer.parseInt(dateParts[1]);
                    year = Integer.parseInt(dateParts[2]);
                } catch (Exception e) {
                    // cant happen since this was already checked on load
                    continue;
                }
                relevantDates.add(ZonedDateTime.of(year, month, day, 0, 0, 0, 0, zone));
            }
        }
        if (!monthlyDates.isEmpty()) {
            for (String date : monthlyDates) {
                int day;
                try {
                    day = Integer.parseInt(date);
                } catch (Exception e) {
                    // cant happen since this was already checked on load
                    continue;
                }
                relevantDates.add(ZonedDateTime.of(currentYear, currentMonth, day, 0, 0, 0, 0, zone));
                relevantDates.add(ZonedDateTime.of(currentYear, currentMonth, day, 0, 0, 0, 0, zone).plusMonths(1));
            }
        }
        if (!yearlyDates.isEmpty()) {
            for (String date : yearlyDates) {
                int day, month;
                String[] dateParts = date.split("\\.");
                try {
                    day = Integer.parseInt(dateParts[0]);
                    month = Integer.parseInt(dateParts[1]);
                } catch (Exception e) {
                    // cant happen since this was already checked on load
                    continue;
                }
                relevantDates.add(ZonedDateTime.of(currentYear, month, day, 0, 0, 0, 0, zone));
                relevantDates.add(ZonedDateTime.of(currentYear, month, day, 0, 0, 0, 0, zone).plusYears(1));
            }
        }
        if (!days.isEmpty()) {
            int diffDay;
            for (int day : days) {
                diffDay = day - currentWeekDay;
                if (diffDay == 0) {
                    relevantDates.add(ZonedDateTime.of(currentYear, currentMonth, currentMonthDay, 0, 0, 0, 0, zone));
                    relevantDates.add(ZonedDateTime.of(currentYear, currentMonth, currentMonthDay, 0, 0, 0, 0, zone).plusWeeks(1));
                } else if (diffDay > 0) {
                    relevantDates.add(ZonedDateTime.of(currentYear, currentMonth, currentMonthDay, 0, 0, 0, 0, zone).plusDays(diffDay));
                    relevantDates.add(ZonedDateTime.of(currentYear, currentMonth, currentMonthDay, 0, 0, 0, 0, zone).plusDays(diffDay).plusWeeks(1));
                } else {
                    relevantDates.add(ZonedDateTime.of(currentYear, currentMonth, currentMonthDay, 0, 0, 0, 0, zone).minusDays(-diffDay));
                    relevantDates.add(ZonedDateTime.of(currentYear, currentMonth, currentMonthDay, 0, 0, 0, 0, zone).minusDays(-diffDay).plusWeeks(1));
                }
            }
        }
        CalendarEvents.debug("loaded " + relevantDates.size() + " days");

        for (ZonedDateTime date : relevantDates) {
            for (String timeString : times) {
                String[] timeParts = timeString.split(":");
                int hour, min;
                try {
                    hour = Integer.parseInt(timeParts[0]);
                    min = Integer.parseInt(timeParts[1]);
                } catch (Exception e) {
                    // can't happen, is parsed before on load
                    continue;
                }
                relevantMillis.add(date.plusHours(hour).plusMinutes(min).toInstant().toEpochMilli());
            }
        }
    }

    void setNextMilli() {
        long currentMillis = System.currentTimeMillis(), toReturn = Long.MAX_VALUE;
        Iterator<Long> relevantMillisIterator = relevantMillis.iterator();
        while (relevantMillisIterator.hasNext()) {
            Long milli = relevantMillisIterator.next();
            if (currentMillis > milli) {
                relevantMillisIterator.remove();
                continue;
            }
            if (milli < toReturn) {
                toReturn = milli;
            }
        }
        if (relevantMillis.isEmpty()) {
            relevantDates.clear();
            relevantMillis.clear();
            setUp();

            relevantMillisIterator = relevantMillis.iterator();
            while (relevantMillisIterator.hasNext()) {
                Long milli = relevantMillisIterator.next();
                if (currentMillis > milli) {
                    relevantMillisIterator.remove();
                    continue;
                }
                if (milli < toReturn) {
                    toReturn = milli;
                }
            }
            if (relevantMillis.isEmpty()) {
                CalendarEvents.debug("[CalendarEvents] " + ChatColor.RED + "All events with the label '" + label + "' are in the past!");
                eventsManager.removeEvent(label);
                return;
            }
        }
        CalendarEvents.debug("next date to schedule: " + ZonedDateTime.ofInstant(Instant.ofEpochMilli(toReturn), zone).toString());
        nextCall = toReturn;
    }

    long getNextCall() {
        return this.nextCall;
    }
}