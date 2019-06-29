package me.nikl.calendarevents.external;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import me.nikl.calendarevents.CalendarEvents;
import org.bukkit.entity.Player;

public class PlaceholderHook {

    public PlaceholderHook(CalendarEvents instance) {
        new CalendarEventsExpansion(instance).register();
    }

    class CalendarEventsExpansion extends PlaceholderExpansion {
        private CalendarEvents plugin;

        public CalendarEventsExpansion(CalendarEvents plugin){
            this.plugin = plugin;
        }

        @Override
        public boolean persist(){
            return true;
        }

        @Override
        public boolean canRegister(){
            return true;
        }

        @Override
        public String getAuthor(){
            return plugin.getDescription().getAuthors().toString();
        }

        @Override
        public String getIdentifier(){
            return "calendarevents";
        }

        @Override
        public String getVersion(){
            return plugin.getDescription().getVersion();
        }

        @Override
        public String onPlaceholderRequest(Player player, String identifier){
            String[] ids = identifier.split(":");
            switch (ids[0].toLowerCase()) {
                case "until":
                    if (ids.length < 2 || ids[1].isEmpty()) return null;
                    int seconds = plugin.getApi().secondsToNextCall(ids[1]);
                    if (seconds < 0) return "never";
                    int days = seconds / (3600*24);
                    int hours = (seconds % (3600*24)) / 3600;
                    int minutes = (seconds % 3600) / 60;
                    seconds = seconds % 60;
                    return (days > 0 ? days + "d " : "")
                            + (hours > 9 ? hours + "h " : hours > 0 ? "0" + hours + "h " : "")
                            + (minutes > 9 ? minutes + "min " : minutes > 0 ? "0" + minutes + "min " : "")
                            + (seconds > 9 ? seconds + "s" : seconds > 0 ? "0" + seconds + "s" : "");
                case "until-s":
                    if (ids.length < 2 || ids[1].isEmpty()) return null;
                    return String.valueOf(plugin.getApi().secondsToNextCall(ids[1])%60);
                case "until-min":
                    if (ids.length < 2 || ids[1].isEmpty()) return null;
                    return String.valueOf((plugin.getApi().secondsToNextCall(ids[1])%3600)/60);
                case "until-h":
                    if (ids.length < 2 || ids[1].isEmpty()) return null;
                    return String.valueOf((plugin.getApi().secondsToNextCall(ids[1])%(3600*24))/3600);
                case "until-d":
                    if (ids.length < 2 || ids[1].isEmpty()) return null;
                    return String.valueOf((plugin.getApi().secondsToNextCall(ids[1])/(3600*24)));
            }
            return null;
        }
    }
}
