package me.nikl.calendarevents;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Map;

/**
 * @author Niklas Eicker
 *
 * Commands for CalendarEvents
 */
public class Commands implements CommandExecutor {

    private CalendarEvents plugin;

    public Commands(CalendarEvents plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        if (args.length == 1) {
            switch (args[0].toLowerCase()) {
                case "reload":
                    if (!checkPermission(sender, args[0].toLowerCase())) {
                        return true;
                    }
                    sender.sendMessage("[CalendarEvents] Reloading...");
                    plugin.reload();
                    sender.sendMessage("[CalendarEvents] CalendarEvents was successfully reloaded");
                    return true;
                case "help":
                    if (!checkPermission(sender, args[0].toLowerCase())) {
                        return true;
                    }
                    sender.sendMessage("[CalendarEvents] Commands:");
                    sender.sendMessage("[CalendarEvents]   reload:  Reload the plugin");
                    sender.sendMessage("[CalendarEvents]   list:    List all CalendarEvents");
                    sender.sendMessage("[CalendarEvents]   help:    Reload the plugin");
                    return true;
                case "list":
                    if (!checkPermission(sender, args[0].toLowerCase())) {
                        return true;
                    }
                    SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
                    Map<String, Long> nextCalls = this.plugin.getApi().getNextCallsOfEvents();
                    sender.sendMessage("[CalendarEvents] Event labels and next call:");
                    nextCalls.forEach((String event, Long millis) -> sender.sendMessage("[CalendarEvents]   " + event + " -> " + dateFormat.format(millis)));
                    return true;
            }
        }
        sender.sendMessage("[CalendarEvents] There is no such command!");
        return true;
    }

    private boolean checkPermission(CommandSender sender, String command) {
        if (!sender.hasPermission("calendarevents." + command)) {
            sender.sendMessage("[CalendarEvents] You have no permission!");
            return false;
        }
        return true;
    }
}
