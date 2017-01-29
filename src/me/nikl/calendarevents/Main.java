package me.nikl.calendarevents;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;
import java.util.logging.Level;

/**
 * Main class
 */
public class Main extends JavaPlugin{
	private Timer timer;
	private EventsManager eventsManager;
	static boolean debug = false;
	
	private File sta, con;
	private FileConfiguration stats, config;
	
	
	@Override
	public void onEnable(){
		reload();
		this.eventsManager = new EventsManager(this);
		this.timer = new Timer(this);
		
	}
	
	@Override
	public void onDisable(){
		this.timer.cancel();
	}
	
	
	
	private void reload() {
		if(this.con == null)this.con = new File(this.getDataFolder().toString() + File.separatorChar + "config.yml");
		if(this.sta == null)this.sta = new File(this.getDataFolder().toString() + File.separatorChar + "data.yml");
		if (!con.exists()) {
			this.saveResource("config.yml", false);
		}
		if (!sta.exists()) {
			try {
				sta.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		// reload configuration
		try {
			this.config = YamlConfiguration.loadConfiguration(new InputStreamReader(new FileInputStream(this.con), "UTF-8"));
		} catch (UnsupportedEncodingException | FileNotFoundException e) {
			e.printStackTrace();
		}
		
		InputStream defConfigStream = this.getResource("config.yml");
		if (defConfigStream != null){
			@SuppressWarnings("deprecation")
			YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(defConfigStream);
			this.config.setDefaults(defConfig);
		}
		
		// if this method was not called from onEnable stats is not null and has to be saved to the file first!
		if (stats != null) {
			try {
				this.stats.save(sta);
			} catch (IOException e) {
				getLogger().log(Level.SEVERE, "Could not save statistics", e);
			}
		}
		
		// load data file
		try {
			this.stats = YamlConfiguration.loadConfiguration(new InputStreamReader(new FileInputStream(this.sta), "UTF-8"));
		} catch (UnsupportedEncodingException | FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	
	
	
	EventsManager getEventsManager(){
		return this.eventsManager;
	}
	
	void getNewTimer() {
		this.timer = new Timer(this);
	}
}
