This is a plugin for minecraft written with the Spigot/Bukkit API. You can find the plugin's site on Spigot here: <a href="https://www.spigotmc.org/resources/calendar-events.35536/" target="_blank">Link</a>.

Calendar Events is intended to be used for repeating occasions in rl-time on which you want to run code. It allows to call a custom event with a precision of about 2 tics (~100ms) at the beginning of a minute. There is a build in listener to run some basic code when an event is fired. To run more complex or customized code you can add this plugin as a dependency and listen for me.nikl.calendarevents.CalendarEvent

The configuration file allows to easily set up the date and time for an event. To later recognise the occasion of the event each event contains a list of labels that refere to the specified occasion set in the configuration file (see config.yml for examples).  
