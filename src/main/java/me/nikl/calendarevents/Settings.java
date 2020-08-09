package me.nikl.calendarevents;

import org.bukkit.configuration.file.FileConfiguration;

import java.util.Locale;

/**
 * @author Niklas Eicker
 */
public class Settings {
    public static long offsetHours = 0;
    public static Locale locale = Locale.ENGLISH;

    public static void loadSettingsFromConfig(FileConfiguration config) {
        offsetHours = config.getLong("settings.offsetHours", 0);
        if (offsetHours == 0) {
            offsetHours = config.getLong("settings.addHoursToServerTime", 0);
        }
        locale = Locale.forLanguageTag(config.getString("settings.language", "en"));
        if (locale == null) locale = Locale.ENGLISH;
    }
}
