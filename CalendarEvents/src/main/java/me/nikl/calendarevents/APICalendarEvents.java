package me.nikl.calendarevents;

/**
 * Created by Niklas on 20.04.2017.
 *
 * API of CalendarEvents
 *
 * This API enables you to add events to the EventManager from other plugins.
 * You can listen for these custom events (see: https://github.com/NiklasEi/ExampleCalendarEventsAPI)
 */
public interface APICalendarEvents {
    /**
     * Add a CalendarEvent to the EventManager
     *
     * The event will be automatically scheduled just like the events from the configuration file. The label must be unique.
     *
     * @param label     label of the event
     * @param occasions the occasions (e.g. 'monday, 02.05.2150' see configuration file of this plugin for more examples)
     * @param timings   the timings (e.g. '14:25, 16:59' see configuration file of this plugin for more examples)
     * @return success in adding the event
     */
    boolean addEvent(String label, String occasions, String timings);

    /**
     * Remove the event with the given label.
     *
     * @param label label of the event to remove
     */
    void removeEvent(String label);

    /**
     * Check whether a given label is registered as an event.
     *
     * @param label
     * @return is registered
     */
    boolean isRegisteredEvent(String label);

    /**
     * Calculate the seconds remaining until the next call of the specified event.
     *
     * @param label
     * @return seconds to next call, or -1 if no call left / event does not exist
     */
    int secondsToNextCall(String label);
}