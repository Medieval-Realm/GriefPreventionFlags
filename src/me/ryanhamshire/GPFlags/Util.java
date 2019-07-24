package me.ryanhamshire.GPFlags;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashMap;

@SuppressWarnings("WeakerAccess")
public class Util {

    public static HashMap<Player, Boolean> hash = new HashMap<>();

    /** Check if server is running a minimum Minecraft version
     * @param major Major version to check (Most likely just going to be 1)
     * @param minor Minor version to check
     * @return True if running this version or higher
     */
    public static boolean isRunningMinecraft(int major, int minor) {
        return isRunningMinecraft(major, minor, 0);
    }

    /** Check if server is running a minimum Minecraft version
     * @param major Major version to check (Most likely just going to be 1)
     * @param minor Minor version to check
     * @param revision Revision to check
     * @return True if running this version or higher
     */
    public static boolean isRunningMinecraft(int major, int minor, int revision) {
        String[] version = Bukkit.getServer().getBukkitVersion().split("-")[0].split("\\.");
        int maj = Integer.valueOf(version[0]);
        int min = Integer.valueOf(version[1]);
        int rev;
        try {
            rev = Integer.valueOf(version[2]);
        } catch (Exception ignore) {
            rev = 0;
        }
        return maj >= major && min >= minor && rev >= revision;
    }

}
