# CalendarEvents

This is a plugin for minecraft written with the Spigot/Bukkit API. The plugin is [published on Spigot][spigot].

Calendar Events is intended to be used for repeating occasions in rl-time on which you want to run code. It allows to call a custom event with a precision of about 2 tics (~100ms) at the beginning of a minute. There is a build in listener to run some basic code when an event is fired. To run more complex or customized code you can add this plugin as a dependency and listen for me.nikl.calendarevents.CalendarEvent

The configuration file allows to easily set up the date and time for an event. To later recognise the occasion of the event each event contains a list of labels that refere to the specified occasion set in the configuration file (see config.yml for examples).  

If you want to use this plugin as a dependency you can use the built in API to add and remove events at runtime ([see this example project][example]).

For more information and some examples visit the [plugins site on Spigot][spigot] or take a look at [the project on my homepage][hp].


[spigot]: https://www.spigotmc.org/resources/calendar-events.35536/
[example]: https://github.com/NiklasEi/EggsampleCalendarEventsAPI
[hp]: https://www.nikl.me/projects/CalendarEvents/