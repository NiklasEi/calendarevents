package me.nikl.calendarevents.nms;

import org.bukkit.entity.Player;

/**
 * Created by niklas on 10/17/16.
 *
 * interface for nms utils
 */
public interface NMSUtil {

    /**
     * Send a title with optional subtitle to a player
     *
     * @param player   player to sent the title to
     * @param title    main title
     * @param subTitle sub title
     */
    void sendTitle(Player player, String title, String subTitle);

    /**
     * Send an actionbar to a player
     *
     * @param p       player to send the actionbar to
     * @param message message in the actionbar
     */
    void sendActionbar(Player p, String message);

    void sendListHeader(Player player, String header);

    void sendListFooter(Player player, String footer);
}
