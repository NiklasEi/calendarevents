package me.nikl.calendarevents;

import org.bukkit.configuration.file.FileConfiguration;

import java.util.Locale;

/**
 * @author Niklas Eicker
 */
public class Settings {
    public static long addHoursToServerTime = 0;
    public static Locale locale = Locale.ENGLISH;

    public static void loadSettingsFromConfig(FileConfiguration config) {
        addHoursToServerTime = config.getLong("settings.addHoursToServerTime", 0);
        locale = Locale.forLanguageTag(config.getString("settings.language", "en"));
        if (locale == null) locale = Locale.ENGLISH;
    }
}