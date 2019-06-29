package me.nikl.calendarevents.scheduling;

import me.nikl.calendarevents.CalendarEvents;
import me.nikl.calendarevents.EventsManager;
import org.bukkit.scheduler.BukkitRunnable;

import java.time.ZonedDateTime;

/**
 * @author Niklas Eicker
 */
public class Timer extends BukkitRunnable {
    private EventsManager eventsManager;
    private CalendarEvents plugin;

    public Timer(CalendarEvents plugin) {
        this.plugin = plugin;
        this.eventsManager = (EventsManager) plugin.getApi();

        int diff = ZonedDateTime.now().getSecond() - 30;

        // one second before the first timer runs make sure that the next events are not shifted by one minute.
        new BukkitRunnable() {
            @Override
            public void run() {
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        eventsManager.reCalcNextMillis();
                    }
                }.runTask(plugin);
            }
        }.runTaskLaterAsynchronously(plugin, (diff < 0 ? (-diff * 20 - 20) : ((60 - diff) * 20 - 20)));

        this.runTaskTimerAsynchronously(plugin, (diff < 0 ? (-diff * 20) : ((60 - diff) * 20)), 20 * 60);
    }


    @Override
    public void run() {
        CalendarEvents.debug("Timer run");
        new BukkitRunnable() {
            @Override
            public void run() {
                eventsManager.callNextMinute();
            }
        }.runTask(plugin);

        // make sure the timer is not getting of time
        //   tolerance: xx:20 to xx:40
        int sec = ZonedDateTime.now().getSecond();
        if (sec < 20 || sec > 40) {
            CalendarEvents.debug("out of tolerance");
            plugin.getNewTimer();
            cancel();
        }
    }
}
