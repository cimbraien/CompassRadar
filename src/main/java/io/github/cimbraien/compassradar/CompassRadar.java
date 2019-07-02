package io.github.cimbraien.compassradar;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;


public class CompassRadar
        extends JavaPlugin
{
    private static CompassRadar plugin;

    public static CompassRadar getInstance() { return plugin; }


    public static HashMap<Player, Player> tracks = new HashMap();

    public static String prefix;
    public static boolean recipe;
    public static String format;
    public static String item_name;
    public static List<String> item_lore = new ArrayList();
    public static boolean gui;

    public void onEnable() {
        getLogger().info("-----------------------------------------------");
        getLogger().warning("This plugin is fully coded by Kimbrian Marshall");
        getLogger().warning("Visit my website at http://www.cimbraien.id");
        getLogger().warning("Contact me on : me@cimbraien.id");
        getLogger().info("-----------------------------------------------");

        plugin = this;
        Loops.everyFiveTicks();
        Loops.everySecond();

        getServer().getPluginManager().registerEvents(new RadarMenu(),
                this);

        getCommand("radar").setExecutor(new Commands());

        saveDefaultConfig();
        reloadConfigFile();
    }

    public void reloadConfigFile() {
        String previous_name = ChatColor.translateAlternateColorCodes('&', getConfig().getString("radar.name"));
        if (item_name != null) {
            previous_name = item_name;
        }

        reloadConfig();
        prefix = ChatColor.translateAlternateColorCodes('&',
                String.valueOf(getConfig().getString("prefix")) + " ");
        recipe = getConfig().getBoolean("customrecipe");
        gui = getConfig().getBoolean("gui");
        format = ChatColor.translateAlternateColorCodes('&',
                getConfig().getString("format"));
        item_name = ChatColor.translateAlternateColorCodes('&',
                getConfig().getString("radar.name"));

        item_lore.clear();
        for (String line : getConfig().getStringList("radar.lore")) {
            item_lore.add(ChatColor.translateAlternateColorCodes('&', line));
        }

        ItemStack item = getTracker();
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(previous_name);
        item.setItemMeta(meta);

        Iterator<Recipe> iter = getServer().recipeIterator();
        while (iter.hasNext()) {
            if (((Recipe)iter.next()).getResult().isSimilar(item)) {
                iter.remove();
            }
        }
        ShapelessRecipe rec = new ShapelessRecipe(getTracker());
        rec.addIngredient(1, Material.COMPASS);

        if (recipe) {
            getServer().addRecipe(rec);
            return;
        }
    }

    public static ItemStack getTracker() {
        ItemStack tracker = new ItemStack(Material.COMPASS);
        ItemMeta tracker_meta = tracker.getItemMeta();

        tracker_meta.setDisplayName(item_name);
        tracker_meta.setLore(item_lore);
        tracker.setItemMeta(tracker_meta);

        return tracker;
    }

    public static void refreshTracker() {
        for (Player p : Bukkit.getOnlinePlayers()) {
            World world = p.getWorld();
            Player nearest = null;
            double nearest_range = 0.0D;

            for (Player target : Bukkit.getOnlinePlayers()) {
                if (target.getWorld().equals(world) && !target.equals(p)) {
                    double x = Math.abs(p.getLocation().getX() -
                            target.getLocation().getX());
                    double y = Math.abs(p.getLocation().getY() -
                            target.getLocation().getY());
                    double z = Math.abs(p.getLocation().getZ() -
                            target.getLocation().getZ());
                    double range = Math.sqrt(
                            Math.pow(x, 2.0D) + Math.pow(y, 2.0D) + Math.pow(z, 2.0D));

                    if (nearest == null) {
                        nearest = target;
                        nearest_range = range;
                    }

                    if (range < nearest_range) {
                        nearest = target;
                        nearest_range = range;
                    }
                }
            }
            if (nearest == null) {
                tracks.remove(p);
                p.setCompassTarget(p.getLocation());
                return;
            }
            tracks.put(p, nearest);
        }
    }

    public static String getFormatted(Player p, Player target, double range) {
        String new_format = format;
        new_format = new_format.replace("{name}", p.getName());
        new_format = new_format.replace("{target}", target.getName());
        return new_format.replace("{range}", String.valueOf((int)range));
    }


    public static void setBar(final Player p, Player target, double range) {
        ActionBarHandler.using.put(p, Boolean.valueOf(true));

        ActionBar bar = new ActionBar();
        bar.setMessage(getFormatted(p, target, range)).send(p);

        Bukkit.getScheduler().runTaskLater(getInstance(), new Runnable() {
            public void run() {
                ItemStack tracker = CompassRadar.getTracker();
                tracker.setAmount(p.getItemInHand().getAmount());

                if (!p.getItemInHand().equals(tracker))
                    ActionBarHandler.using.put(p, Boolean.valueOf(false));
            }
        },60L);
    }

    public static void checkHand() {
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (!tracks.containsKey(p))
                return;
            ItemStack tracker = getTracker();
            tracker.setAmount(p.getItemInHand().getAmount());

            if (p.getItemInHand().equals(tracker)) {
                Player target = (Player)tracks.get(p);

                p.setCompassTarget(target.getLocation());
                setBar(p, target,
                        getRange(p.getLocation(), target.getLocation())); continue;
            }
            p.setCompassTarget(p.getLocation());
        }
    }


    public static double getRange(Location loc1, Location loc2) {
        double x = Math.abs(loc1.getX() - loc2.getX());
        double y = Math.abs(loc1.getY() - loc2.getY());
        double z = Math.abs(loc1.getZ() - loc2.getZ());


        return Math.sqrt(Math.pow(x, 2.0D) + Math.pow(y, 2.0D) + Math.pow(z, 2.0D));
    }
}
