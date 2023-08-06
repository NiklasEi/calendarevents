# Changelog for CalendarEvents
Releases can be found on [Spigot](https://www.spigotmc.org/resources/35536/)
#

### v 2.4.0
- Bump to latest nmsutilities for support of MC 1.19/1.20
- switch maven repository

### v 2.3.0
- new placeholder `until-min-plus-one` to show correct amount of minutes left till next event call

### v 2.2.0
- Support up to MC 1.16.4

### v 2.1.0
- Send broadcasts and all commands through PlaceholderAPI

### v 2.0.0
- Specify api version to remove legacy support

#


### v 1.9.0
- Support %allPlayers% and %allOffline% in commands and run them for OfflinePlayers

### v 1.8.3
- Support combined events with the reload command [#12](https://github.com/NiklasEi/calendarevents/issues/12)
- add help and list commands
  - **/ce help** will display commdand help
  - **/ce list** will show all scheduled events (combined events are currently not shown)

### v 1.8.2
- support minecraft version 1.15

### v 1.8.1
- Fix combined events not being reloaded on command
- Support placeholders (PlaceholderAPI) in commands, titles and actionbars

### v 1.8.0
- NEW FEATURE: Combined events
  - allow events to fire when a specified list of other events was fired
  - this allows special scheduling like e.g. "first/second/third" sunday of a month

### v 1.7.0
- provide placeholders to display the time till next call via PlaceholderAPI

### v 1.6.0
- support minecraft 1.14

### v 1.5.0
- add commands with permission action (closes #6)
- make the ticks that a title is visible configurable

### v 1.4.2
- compatibility with MC 1.13 and 1.13.1

### v 1.4.1
- plugin will no longer shut down on versions that are not supported by nms utilities
  - Instead only those functions will not work, that nms utilities are needed for. Those are action bar and title messages.

### v 1.4.0
- move nms modules into separate project and shade them
- optional server time offset in config
- new placeholders in event messages (%day% and %month%)
- locale setting for day and month names

### v 1.3.3
- Default events are less obvious ingame now. Most actions are commented out, or need an example permission.
- Refined the default configuration
- Two new methods in API
  - `boolean isRegisteredEvent(String label)`
  - `int secondsToNextCall(String label)`

### v 1.3.2
- events registered through the api will not be lost anymore due to a CalendarEvents reload.
- create and use NmsFactory

### v 1.3.1:
- mavenized the repository
- fix scheduling bug (happening when a daily event is loaded on a day that comes after the event day...)
- added CalendarEvents to bStats (can be opt out in config)
- added missing Prefix in warnings

### v 1.3.0:
- add support for placeholders in timings
    - 'x' and 'X' will be replaced with every possible number that makes sense in the position. Meaning the 'x' in ``00:x5`` will be replaced by 0, 1, 2, 3, 4 and 5. At the end ``00:x5`` 
    is the same as ``00:05, 00:15, 00:25, 00:35, 00:45, 00:55``
    - another example:
    
        ``time: "xx:00, 11:18"``
    
        will call an event on every full hour and at 11:18
    
### v 1.1.0:
- added api to add events on runtime
- checking for existing events with same label (no overriding)
- event with only past timings will be removed
- recalculate timings of event if there are no next timings (should fix problems arising when plugin was running for a few weeks)
- calculating of next millis to call is more efficient
- removed unused data file
