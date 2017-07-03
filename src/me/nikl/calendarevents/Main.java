package me.nikl.calendarevents;

import com.google.common.base.Charsets;
import me.nikl.calendarevents.nms.*;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;

/**
 * Main class
 */
public class Main extends JavaPlugin{
	private NMSUtil nms;
	private Timer timer;
	private EventsManager eventsManager;
	static boolean debug = false;
	
	private File con;
	private FileConfiguration config;

	private APICalendarEvents api;
	
	
	@Override
	public void onEnable(){
		reloadConfiguration();
		setUpNMS();
		this.eventsManager = new EventsManager(this);
		this.timer = new Timer(this);

		this.getCommand("calendarevents").setExecutor(new Commands(this));

		this.api = new APICalendarEvents(eventsManager);
	}
	
	@Override
	public void onDisable(){
		if(this.timer != null) this.timer.cancel();
	}
	
	private void reloadConfiguration() {
		this.con = new File(this.getDataFolder().toString() + File.separatorChar + "config.yml");
		if (!con.exists()) {
			this.saveResource("config.yml", false);
		}

		// reload configuration
		try {
			this.config = YamlConfiguration.loadConfiguration(new InputStreamReader(new FileInputStream(con), Charsets.UTF_8));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	public void reload(){
		if(this.timer != null) this.timer.cancel();
		reloadConfiguration();
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
		
		if(debug) getLogger().info("Your server is running version " + version);
		
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
		return this.api;
	}

	@Override
	public FileConfiguration getConfig(){
		return this.config;
	}
}
