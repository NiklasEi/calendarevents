package me.nikl.calendarevents;

import com.google.common.base.Charsets;
import me.nikl.calendarevents.nms.*;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;

/**
 * Main class
 */
public class Main extends JavaPlugin{
	private static final boolean DEBUG = false;
	private NMSUtil nms;
	private Timer timer;
	private EventsManager eventsManager;
	private File configurationFile;
	private FileConfiguration configuration;
	private Metrics metrics;

	
	@Override
	public void onEnable(){
		reloadConfiguration();
		setUpNMS();
		this.eventsManager = new EventsManager(this);
		this.timer = new Timer(this);
		this.getCommand("calendarevents").setExecutor(new Commands(this));
		setUpMetrics();
	}

	private void setUpMetrics() {

		// send data with bStats if not opt out
		if(!configuration.getBoolean("bstats.disabled", false)) {
			metrics = new Metrics(this);
			metrics.addCustomChart(new Metrics.SimplePie("number_of_events"
					, () -> String.valueOf(eventsManager.getNumberOfEvents())));
		} else {
			Bukkit.getConsoleSender().sendMessage("[" + ChatColor.DARK_AQUA + "CalendarEvents" + ChatColor.RESET + "]" + " You have opt out bStats... That's sad!");
		}
	}

	@Override
	public void onDisable(){
		if(this.timer != null) this.timer.cancel();
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

	public void reload(){
		if(this.timer != null) this.timer.cancel();
		eventsManager.reload();
		getNewTimer();
	}

	private boolean setUpNMS() {
		String version;
		try {
			version = Bukkit.getServer().getClass().getPackage().getName().replace(".",  ",").split(",")[3];
		} catch (ArrayIndexOutOfBoundsException e) {
			return false;
		}
		debug("Your server is running version " + version);
		switch (version) {
			case "v1_10_R1":
				nms = new NMSUtil_1_10_R1();
				break;
			case "v1_9_R2":
				nms = new NMSUtil_1_9_R2();
				break;
			case "v1_9_R1":
				nms = new NMSUtil_1_9_R1();
				break;
			case "v1_8_R3":
				nms = new NMSUtil_1_8_R3();
				break;
			case "v1_8_R2":
				nms = new NMSUtil_1_8_R2();
				break;
			case "v1_8_R1":
				nms = new NMSUtil_1_8_R1();
				break;
			case "v1_11_R1":
				nms = new NMSUtil_1_11_R1();
				break;
			case "v1_12_R1":
				nms = new NMSUtil_1_12_R1();
				break;
		}
		return nms != null;
	}
	
	NMSUtil getNms(){
		return this.nms;
	}
	
	EventsManager getEventsManager(){
		return this.eventsManager;
	}
	
	void getNewTimer() {
		this.timer = new Timer(this);
	}

	/**
	 * Get the API instance to manipulate Events on runtime.
	 *
	 * @return API instance
	 */
	public APICalendarEvents getApi(){
		return this.eventsManager;
	}

	@Override
	public FileConfiguration getConfig(){
		return this.configuration;
	}

	static void debug(String message){
		if(DEBUG) Bukkit.getLogger().info(message);
	}
}
