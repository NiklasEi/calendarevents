package me.nikl.calendarevents;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;

/**
 * Plugin intern listener for the event CalendarEvent
 *
 * load stuff to do from the config and do whatever was configured on the events
 */
class EventListener implements Listener {
	private Main plugin;
	private Map<String, ArrayList<String>> commands ;
	private Map<String, String> broadcast ;
	private Map<String, BroadcastWithPerm> broadCastWithPerm;
	
	private Set<String> labels;
	
	
	EventListener(Main plugin, Set<String> labels){
		this.plugin = plugin;
		this.commands = new HashMap<>();
		this.broadcast = new HashMap<>();
		this.broadCastWithPerm = new HashMap<>();
		
		this.labels =labels;
		
		loadListener();
	}
	
	private void loadListener() {
		FileConfiguration config = plugin.getConfig();
		
		if(!config.isConfigurationSection("listener")) return;
		
		ConfigurationSection listener = config.getConfigurationSection("listener");
		
		for(String label : listener.getKeys(false)){
			
			// check whether the label is a configured event
			if(!labels.contains(label)){
				Bukkit.getLogger().log(Level.WARNING, "Section 'listener' contains actions for a not in section 'events' defined event: " + label);
				continue;
			}
			if(listener.isList(label + ".commands")){
				ArrayList<String> commands= new ArrayList<>(listener.getStringList(label + ".commands"));
				for(int i = 0; i < commands.size();i++){
					commands.set(i, ChatColor.translateAlternateColorCodes('&',commands.get(i)));
				}
				this.commands.put(label,commands);
			}
			if(listener.isString(label + ".broadcast")){
				broadcast.put(label, ChatColor.translateAlternateColorCodes('&',listener.getString(label + ".broadcast")));
			}
			
			if(listener.isConfigurationSection(label + ".broadcastWithPerm") && listener.isString(label + ".broadcastWithPerm" + ".perm") && listener.isString(label + ".broadcastWithPerm" + ".broadcast")){
				broadCastWithPerm.put(label, new BroadcastWithPerm(listener.getString(label + ".broadcastWithPerm" + ".perm"), ChatColor.translateAlternateColorCodes('&',listener.getString(label + ".broadcastWithPerm" + ".broadcast"))));
			}
			
			
			
			
		}
		
	}
	
	
	
	@EventHandler
	public void onCalendarEvent(CalendarEvent event){
		if(Main.debug) {
			Bukkit.getConsoleSender().sendMessage("[Listener] event called: " + event.getLabels().toString());
			Bukkit.getConsoleSender().sendMessage("[Listener] called at: " + event.getTime());
		}
		// go through all labels in the listener section
		for(String label : event.getLabels()){
			if(commands.get(label) != null && !commands.get(label).isEmpty()){
				for(String cmd:commands.get(label)){
					if(cmd.contains("%allOnline%")){
						for(Player player : Bukkit.getOnlinePlayers()){
							Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd.replaceAll("%allOnline%", player.getName()));
						}
					} else {
						Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd);
					}
				}
			}
			
			if(broadcast.get(label) != null){
				Bukkit.broadcastMessage(broadcast.get(label));
			}
			
			if(broadCastWithPerm.get(label) != null){
				BroadcastWithPerm broadcastWithPerm = this.broadCastWithPerm.get(label);
				Bukkit.broadcast(broadcastWithPerm.message, broadcastWithPerm.perm);
			}
		}
	}
	
	
	private class BroadcastWithPerm {
		String perm, message;
		private BroadcastWithPerm(String perm, String message) {
			this.perm=perm;
			this.message=message;
		}
	}
}
