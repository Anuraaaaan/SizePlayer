package com.anuran.sizeplayer;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class confReloadCMD implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        SizePlayer plugin = SizePlayer.getPlugin();
            plugin.reloadConfig();
            commandSender.sendMessage(colorize(Objects.requireNonNull(plugin.getConfig().getString("config-reloaded"))));
        return true;
    }

    private String colorize(String message) {
        return ChatColor.translateAlternateColorCodes('&', message);
    }
}
