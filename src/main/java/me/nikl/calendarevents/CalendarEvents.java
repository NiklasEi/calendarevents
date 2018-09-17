package me.nikl.calendarevents;

import com.google.common.base.Charsets;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;

/**
 * @author Niklas Eicker
 *
 * Plugin class
 */
public class CalendarEvents extends JavaPlugin {
    private static final boolean DEBUG = false;
    private Timer timer;
    private EventsManager eventsManager;
    private File configurationFile;
    private FileConfiguration configuration;
    private Metrics metrics;

    static void debug(String message) {
        if (DEBUG) Bukkit.getLogger().info(message);
    }

    @Override
    public void onEnable() {
        reloadConfiguration();
        Settings.loadSettingsFromConfig(configuration);
        this.eventsManager = new EventsManager(this);
        this.timer = new Timer(this);
        this.getCommand("calendarevents").setExecutor(new Commands(this));
        setUpMetrics();
    }

    private void setUpMetrics() {

        // send data with bStats if not opt out
        if (!configuration.getBoolean("bstats.disabled", false)) {
            metrics = new Metrics(this);
            metrics.addCustomChart(new Metrics.SimplePie("number_of_events"
                    , () -> String.valueOf(eventsManager.getNumberOfEvents())));
        } else {
            Bukkit.getConsoleSender().sendMessage("[" + ChatColor.DARK_AQUA + "CalendarEvents" + ChatColor.RESET + "]" + " You have opt out bStats... That's sad!");
        }
    }

    @Override
    public void onDisable() {
        if (this.timer != null) this.timer.cancel();
    }

    void reloadConfiguration() {
        this.configurationFile = new File(this.getDataFolder().toString() + File.separatorChar + "config.yml");
        if (!configurationFile.exists()) {
            this.saveResource("config.yml", false);
        }
        try {
            this.configuration = YamlConfiguration.loadConfiguration(new InputStreamReader(new FileInputStream(configurationFile), Charsets.UTF_8));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void reload() {
        if (this.timer != null) this.timer.cancel();
        eventsManager.reload();
        getNewTimer();
    }

    void getNewTimer() {
        this.timer = new Timer(this);
    }

    /**
     * Get the API instance to manipulate Events on runtime.
     *
     * @return API instance
     */
    public CalendarEventsApi getApi() {
        return this.eventsManager;
    }

    @Override
    public FileConfiguration getConfig() {
        return this.configuration;
    }
}
