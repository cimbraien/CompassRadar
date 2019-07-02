package io.github.cimbraien.compassradar;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class Commands
        implements CommandExecutor {
    public boolean onCommand(CommandSender s, Command cmd, String label, String[] args) {
        if (args.length == 0) {
            printHelp(s);
            return true;
        }

        if (args[0].equals("give")) {
            if (!(s instanceof Player)) {
                s.sendMessage(ChatColor.RED + "This is a player only command!");
                return true;
            }

            Player p = (Player)s;
            if (args.length == 1) {
                if (!p.hasPermission("radar.give")) {
                    p.sendMessage(String.valueOf(CompassRadar.prefix) + ChatColor.RED +
                            "No permission!");
                    return true;
                }
                p.getInventory().addItem(new ItemStack[] { CompassRadar.getTracker() });

                p.sendMessage(String.valueOf(CompassRadar.prefix) + ChatColor.YELLOW +
                        "A radar has been added to your inventory.");
                return true;
            }
            if (!p.hasPermission("radar.give.other")) {
                p.sendMessage(String.valueOf(CompassRadar.prefix) + ChatColor.RED +
                        "No permission!");
                return true;
            }

            for (Player q : Bukkit.getOnlinePlayers()) {
                if (q.getName().equals(args[1])) {
                    q.getInventory().addItem(new ItemStack[] { CompassRadar.getTracker() });
                    q.sendMessage(
                            "A radar has been added to your inventory.");
                    p.sendMessage(String.valueOf(CompassRadar.prefix) + ChatColor.YELLOW +
                            "A radar has been added to " + q.getName() +
                            "'s inventory.");
                    return true;
                }
            }
            p.sendMessage(String.valueOf(CompassRadar.prefix) + ChatColor.RED + "Player not found!");
            return true;
        }



        if (args[0].equals("reload")) {
            if (!s.hasPermission("radar.reload")) {
                s.sendMessage(
                        String.valueOf(CompassRadar.prefix) + ChatColor.RED + "No permission!");
                return true;
            }
            CompassRadar.getInstance().reloadConfigFile();
            s.sendMessage(String.valueOf(CompassRadar.prefix) + ChatColor.GREEN +
                    "Configuration reloaded!");
            return true;
        }
        printHelp(s);
        return true;
    }

    void printHelp(CommandSender s) {
        s.sendMessage(String.valueOf(CompassRadar.prefix) + ChatColor.YELLOW +
                "/radar give <player> - Gives a radar to your inventory / other player's");
        s.sendMessage(String.valueOf(CompassRadar.prefix) + ChatColor.YELLOW +
                "/radar reload - Reload configuration files for CompassRadar");
    }
}
