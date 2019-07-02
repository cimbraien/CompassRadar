package io.github.cimbraien.compassradar;

import org.bukkit.Bukkit;

public class Loops
{
    public static void everyFiveTicks() {
        Bukkit.getScheduler().scheduleSyncRepeatingTask(CompassRadar.getInstance(), new Runnable() {
            public void run() {
                CompassRadar.checkHand();
                CompassRadar.refreshTracker();
            }
        },  0L, 5L);
    }


    public static void everySecond() {
        Bukkit.getScheduler().scheduleSyncRepeatingTask(CompassRadar.getInstance(), new Runnable() {
            public void run() {
                ActionBarHandler.checkClearAll();
            }
        },  0L, 20L);
    }
}
