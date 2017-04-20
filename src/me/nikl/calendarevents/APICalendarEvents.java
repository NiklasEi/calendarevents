package me.nikl.calendarevents;

/**
 * Created by Niklas on 20.04.2017.
 *
 * API of CalendarEvents
 *
 * This API enables you to add events to the EventManager from other plugins.
 * You can listen for these custom events (see: )
 */
public class APICalendarEvents {
    private EventsManager eventsManager;


    public APICalendarEvents(EventsManager eventsManager){
        this.eventsManager = eventsManager;
    }

    /**
     * Add a CalendarEvent to the EventManager
     *
     * The event will be automatically scheduled just like the events from the configuration file. The label must be unique.
     * @param label label of the event
     * @param occasions the occasions (e.g. 'monday, 02.05.2150' see configuration file of this plugin for more examples)
     * @param timings the timings (e.g. '14:25, 16:59' see configuration file of this plugin for more examples)
     * @return success in adding the event
     */
    public boolean addEvent(String label, String occasions, String timings){
        return eventsManager.addEvent(label, occasions, timings);
    }
}