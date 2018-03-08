package me.nikl.calendarevents;

/**
 * Created by Niklas on 20.04.2017.
 *
 * API of CalendarEvents
 *
 * This API enables you to add events to the EventManager from other plugins.
 * You can listen for these custom events.
 * See: https://github.com/NiklasEi/ExampleCalendarEventsAPI
 */
public interface CalendarEventsApi {
    /**
     * Add a CalendarEvent to the EventManager
     *
     * The event will be automatically scheduled just like the events from the configuration file.
     * The label must be unique.
     *
     * @param label     of the event to add
     * @param occasions e.g. 'monday, 02.05.2150' see configuration file of this plugin for examples
     * @param timings   e.g. '14:25, 16:59' see configuration file of this plugin for more examples
     * @return success in adding the event
     */
    boolean addEvent(String label, String occasions, String timings);

    /**
     * Remove the event with the given label.
     *
     * @param label of the event to remove
     */
    void removeEvent(String label);

    /**
     * Check whether a given label is registered as an event.
     *
     * @param label of the event to check for
     * @return is registered
     */
    boolean isRegisteredEvent(String label);

    /**
     * Calculate the seconds remaining until the next call of the specified event.
     *
     * @param label of the event to remove
     * @return seconds to next call, or -1 if no call left / event does not exist
     */
    int secondsToNextCall(String label);
}