package io.github.cimbraien.compassradar;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;


public class RadarMenu
        implements Listener
{
    public static Inventory getInventory(Player p) {
        Inventory inv = Bukkit.createInventory(null, 27, "Radar");

        Map<Player, Integer> targets = new HashMap<Player, Integer>();
        for (Player x : Bukkit.getOnlinePlayers()) {
            if (!x.equals(p) && x.getWorld().equals(p.getWorld())) {
                int range = (int)CompassRadar.getRange(p.getLocation(),
                        x.getLocation());
                targets.put(x, Integer.valueOf(range));
            }
        }

        SortedSet<Map.Entry<Player, Integer>> sortedTargets = sort(targets);
        for (Map.Entry<Player, Integer> e : sortedTargets) {
            int range = ((Integer)e.getValue()).intValue();
            Player q = (Player)e.getKey();
            List<String> lore = new ArrayList<String>();
            lore.add(CompassRadar.getFormatted(p, q, range));

            ItemStack head = new ItemStack(Material.PLAYER_HEAD);

            SkullMeta head_meta = (SkullMeta)head.getItemMeta();
            head_meta.setOwner(q.getName());
            head_meta.setDisplayName(CompassRadar.getFormatted(p, q, range));

            head.setItemMeta(head_meta);

            inv.addItem(new ItemStack[] { head });
        }
        return inv;
    }

    @EventHandler
    public void onCompassClick(PlayerInteractEvent e) {
        if (!CompassRadar.gui) {
            return;
        }
        if (e.getPlayer().getItemInHand().equals(CompassRadar.getTracker()) && (
                e.getAction().equals(Action.RIGHT_CLICK_AIR) ||
                        e.getAction().equals(Action.RIGHT_CLICK_BLOCK))) {
            e.getPlayer().openInventory(getInventory(e.getPlayer()));
        }
    }


    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        if (e.getClickedInventory().getName().equals("Radar")) {
            e.setCancelled(true);
        }
    }


    public static SortedSet<Map.Entry<Player, Integer>> sort(Map<Player, Integer> map) {
        SortedSet<Map.Entry<Player, Integer>> treeMap = new TreeSet<Map.Entry<Player, Integer>>(
                new Comparator<Map.Entry<Player, Integer>>()
                {

                    public int compare(Map.Entry<Player, Integer> e1, Map.Entry<Player, Integer> e2)
                    {
                        return ((Integer)e1.getValue()).compareTo((Integer)e2.getValue());
                    }
                });

        treeMap.addAll(map.entrySet());
        return treeMap;
    }
}
