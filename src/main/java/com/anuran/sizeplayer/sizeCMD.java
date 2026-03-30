package com.anuran.sizeplayer;


import org.bukkit.ChatColor;
import org.bukkit.attribute.Attribute;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class sizeCMD implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {

        SizePlayer plugin = SizePlayer.getPlugin();
        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage(colorize(Objects.requireNonNull(plugin.getConfig().getString("console-message"))));
            return true;
        }
        if (!(commandSender.hasPermission("size-player.sizeplayer"))) {
            commandSender.sendMessage(colorize(Objects.requireNonNull(plugin.getConfig().getString("no-permissions"))));
            return true;
        }
        if ((strings.length != 1)) return false;

        Player player = (Player) commandSender;

        String sizeNum = strings[0];
        try {
            float sizeNumValue = Float.parseFloat(sizeNum);
            if (sizeNumValue >= 0.4 && sizeNumValue <= 10.0) {
                if (command.getName().equalsIgnoreCase("sizeplayer")) {
                    Objects.requireNonNull(player.getAttribute(Attribute.GENERIC_SCALE)).setBaseValue(sizeNumValue);
                    Objects.requireNonNull(player.getAttribute(Attribute.GENERIC_MAX_HEALTH)).setBaseValue(20);
                    if (sizeNumValue >= 0.4 && sizeNumValue < 1.0) {
                        Objects.requireNonNull(player.getAttribute(Attribute.GENERIC_MAX_HEALTH)).setBaseValue(20 * sizeNumValue + 0);
                    } else if (sizeNumValue > 1.0 && sizeNumValue <= 10.0) {
                        Objects.requireNonNull(player.getAttribute(Attribute.GENERIC_MAX_HEALTH)).setBaseValue(2.22222 * sizeNumValue + 17.77778);
                    }
                    player.sendMessage(colorize(String.format(Objects.requireNonNull(plugin.getConfig().getString("size-set")), sizeNumValue)));
                }
            } else {
                player.sendMessage(colorize(Objects.requireNonNull(plugin.getConfig().getString("invalid-range"))));
                return true;
            }
        } catch (NumberFormatException e) {
            player.sendMessage(colorize(Objects.requireNonNull(plugin.getConfig().getString("invalid-range"))));
            return true;
        }

        return true;
    }
    private String colorize(String message) {
        return ChatColor.translateAlternateColorCodes('&', message);
    }
}
