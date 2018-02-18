package me.nikl.calendarevents.nms;

import org.bukkit.Bukkit;

/**
 * Created by nikl on 18.02.18.
 */
public class NmsFactory {
    private static NMSUtil nmsUtil;

    public static NMSUtil getNmsUtil(){
        if (nmsUtil != null) return nmsUtil;
        String version;
        try {
            version = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
        } catch (ArrayIndexOutOfBoundsException e) {
            return null;
        }
        switch (version) {
            case "v1_10_R1":
                nmsUtil = new NMSUtil_1_10_R1();
                break;
            case "v1_9_R2":
                nmsUtil = new NMSUtil_1_9_R2();
                break;
            case "v1_9_R1":
                nmsUtil = new NMSUtil_1_9_R1();
                break;
            case "v1_8_R3":
                nmsUtil = new NMSUtil_1_8_R3();
                break;
            case "v1_8_R2":
                nmsUtil = new NMSUtil_1_8_R2();
                break;
            case "v1_8_R1":
                nmsUtil = new NMSUtil_1_8_R1();
                break;
            case "v1_11_R1":
                nmsUtil = new NMSUtil_1_11_R1();
                break;
            case "v1_12_R1":
                nmsUtil = new NMSUtil_1_12_R1();
                break;
        }
        return nmsUtil;

    }
}
