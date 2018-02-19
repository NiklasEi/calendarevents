package me.nikl.calendarevents;

import me.nikl.calendarevents.nms.NMSUtil;
import me.nikl.calendarevents.nms.NmsFactory;
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
    private NMSUtil nms;
    private Map<String, ArrayList<String>> commands;
    private Map<String, String> broadcast;
    private Map<String, BroadcastWithPerm> broadCastWithPerm;
    private Map<String, ActionBar> actionBars;
    private Map<String, Title> titles;

    private Set<String> labels;


    EventListener(Main plugin, Set<String> labels) {
        this.plugin = plugin;
        this.nms = NmsFactory.getNmsUtil();

        // checking for null nms later
        if (nms == null)
            plugin.getLogger().warning("Your version is not (jet) supported for titles or actionbars!");

        this.labels = labels;

        loadListener();
    }

    /**
     * Load configured actions on events
     */
    private void loadListener() {
        this.commands = new HashMap<>();
        this.broadcast = new HashMap<>();
        this.broadCastWithPerm = new HashMap<>();
        this.actionBars = new HashMap<>();
        this.titles = new HashMap<>();

        FileConfiguration config = plugin.getConfig();

        if (!config.isConfigurationSection("listener")) return;

        ConfigurationSection listener = config.getConfigurationSection("listener");

        for (String label : listener.getKeys(false)) {

            // check whether the label is a configured event
            if (!labels.contains(label)) {
                Bukkit.getLogger().log(Level.WARNING, "[CalendarEvents] " + "Section 'listener' contains actions for a not scheduled event: " + label);
                continue;
            }
            if (listener.isList(label + ".commands")) {
                ArrayList<String> commands = new ArrayList<>(listener.getStringList(label + ".commands"));
                for (int i = 0; i < commands.size(); i++) {
                    commands.set(i, ChatColor.translateAlternateColorCodes('&', commands.get(i)));
                }
                this.commands.put(label, commands);
            }
            if (listener.isString(label + ".broadcast")) {
                broadcast.put(label, ChatColor.translateAlternateColorCodes('&', listener.getString(label + ".broadcast")));
            }

            if (listener.isConfigurationSection(label + ".broadcastWithPerm") && listener.isString(label + ".broadcastWithPerm" + ".perm") && listener.isString(label + ".broadcastWithPerm" + ".broadcast")) {
                broadCastWithPerm.put(label, new BroadcastWithPerm(listener.getString(label + ".broadcastWithPerm" + ".perm"), ChatColor.translateAlternateColorCodes('&', listener.getString(label + ".broadcastWithPerm" + ".broadcast"))));
            }

            if (listener.isConfigurationSection(label + ".actionbar") && listener.isString(label + ".actionbar" + ".bar")) {
                actionBars.put(label, new ActionBar(listener.getString(label + ".actionbar" + ".perm"), ChatColor.translateAlternateColorCodes('&', listener.getString(label + ".actionbar" + ".bar"))));
            }

            if (listener.isConfigurationSection(label + ".title") && listener.isString(label + ".title" + ".title") && listener.isString(label + ".title" + ".subTitle")) {
                titles.put(label, new Title(listener.getString(label + ".title" + ".perm"), ChatColor.translateAlternateColorCodes('&', listener.getString(label + ".title" + ".title")), ChatColor.translateAlternateColorCodes('&', listener.getString(label + ".title" + ".subTitle"))));
            }

        }
    }


    @EventHandler
    public void onCalendarEvent(CalendarEvent event) {
        Main.debug("[Listener] event called: " + event.getLabels().toString());
        Main.debug("[Listener] called at: " + event.getTime());
        // go through all labels in the listener section
        for (String label : event.getLabels()) {

            // check for commands on the event
            if (commands.get(label) != null && !commands.get(label).isEmpty()) {
                for (String cmd : commands.get(label)) {
                    cmd = cmd.replaceAll("%time%", event.getTime());
                    if (cmd.contains("%allOnline%")) {
                        for (Player player : Bukkit.getOnlinePlayers()) {
                            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd.replaceAll("%allOnline%", player.getName()).replaceAll("%player%", player.getName()));
                        }
                    } else {
                        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd);
                    }
                }
            }

            // check for broadcast
            if (broadcast.get(label) != null) {
                Bukkit.broadcastMessage(broadcast.get(label).replaceAll("%time%", event.getTime()));
            }

            // check for broadcast with permission node
            if (broadCastWithPerm.get(label) != null) {
                BroadcastWithPerm broadcastWithPerm = this.broadCastWithPerm.get(label);
                Bukkit.broadcast(broadcastWithPerm.message.replaceAll("%time%", event.getTime()), broadcastWithPerm.perm);
            }

            // check for actionbar
            if (actionBars.get(label) != null && this.nms != null) {
                ActionBar actionBar = this.actionBars.get(label);
                String bar = actionBar.bar.replaceAll("%time%", event.getTime());
                if (actionBar.perm == null || actionBar.perm.equals("")) {
                    // no permission => send to every player
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        nms.sendActionbar(player, bar.replaceAll("%player%", player.getName()));
                    }
                } else {
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        // check for permission node first
                        if (!player.hasPermission(actionBar.perm)) continue;
                        nms.sendActionbar(player, bar.replaceAll("%player%", player.getName()));
                    }
                }
            }

            // check for title
            if (titles.get(label) != null && this.nms != null) {
                Title title = this.titles.get(label);
                String titleString = title.title.replaceAll("%time%", event.getTime());
                String subTitle = title.subTitle.replaceAll("%time%", event.getTime());
                if (title.perm == null || title.perm.equals("")) {
                    // no permission => send to every player
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        nms.sendTitle(player, titleString.replaceAll("%player%", player.getName()), subTitle.replaceAll("%player%", player.getName()));
                    }
                } else {
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        // check for permission node first
                        if (!player.hasPermission(title.perm)) continue;
                        nms.sendTitle(player, titleString.replaceAll("%player%", player.getName()), subTitle.replaceAll("%player%", player.getName()));
                    }
                }
            }
        }
    }


    /**
     * Store broadcast info
     */
    private class BroadcastWithPerm {
        String perm, message;

        private BroadcastWithPerm(String perm, String message) {
            this.perm = perm;
            this.message = message;
        }
    }

    /**
     * Store actionbar info
     */
    private class ActionBar {
        String perm, bar;

        private ActionBar(String perm, String bar) {
            this.perm = perm;
            this.bar = bar;
        }
    }

    /**
     * Store title info
     */
    private class Title {
        String perm, title, subTitle;

        private Title(String perm, String title, String subTitle) {
            this.perm = perm;
            this.title = title;
            this.subTitle = subTitle;
        }
    }
}
