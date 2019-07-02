package io.github.cimbraien.compassradar;

import java.util.HashMap;
import java.util.Map;
import org.bukkit.entity.Player;


public class ActionBarHandler
{
    public static HashMap<Player, Boolean> using = new HashMap();

    public static void clearBar(Player p) {
        if (!((Boolean)using.get(p)).booleanValue()) {
            ActionBar bar = new ActionBar();
            bar.setMessage("").send(p);
        }
    }

    public static void checkClearAll() {
        for (Map.Entry e : using.entrySet()) {
            if (!((Boolean)e.getValue()).booleanValue())
                clearBar((Player)e.getKey());
        }
    }
}
