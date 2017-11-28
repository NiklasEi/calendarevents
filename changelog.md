v 1.3.1:
- mavenized the repository
- fix scheduling bug (happening when a daily event is loaded on a day that comes after the event day...)
- added CalendarEvents to bStats (can be opt out in config)
- added missing Prefix in warnings


v 1.3.0:
- add support for placeholders in timings
    - 'x' and 'X' will be replaced with every possible number that makes sense in the position. Meaning the 'x' in ``00:x5`` will be replaced by 0, 1, 2, 3, 4 and 5. At the end ``00:x5`` 
    is the same as ``00:05, 00:15, 00:25, 00:35, 00:45, 00:55``
    - another example:
    
        ``time: "xx:00, 11:18"``
    
        will call an event on every full hour and at 11:18
    
    
v 1.1.0:
- added api to add events on runtime
- checking for existing events with same label (no overriding)
- event with only past timings will be removed
- recalculate timings of event if there are no next timings (should fix problems arising when plugin was running for a few weeks)
- calculating of next millis to call is more efficient
- removed unused data file