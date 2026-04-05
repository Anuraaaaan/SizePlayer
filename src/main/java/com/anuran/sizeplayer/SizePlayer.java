package com.anuran.sizeplayer;

import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class SizePlayer extends JavaPlugin {

    public static SizePlayer plugin;
    public static double minValue;
    public static double maxValue;

    public static String minFormula;
    public static String maxFormula;

    public static String getMSG(String path) {
        return plugin.getConfig().getString(path);
    }

    @Override
    public void onEnable() {
        plugin = this;
        saveDefaultConfig();

        // регистрация конфига
        Messages.register();

        // регистрация команд

        Objects.requireNonNull(getServer().getPluginCommand("sizeplayer")).setExecutor(new SizePlayerCMD());

        minValue = getConfig().getDouble("values.min_value");
        maxValue = getConfig().getDouble("values.max_value");

        minFormula = getConfig().getString("values.min_formula");
        maxFormula = getConfig().getString("values.max_formula");
        getLogger().info(colorize(getConfig().getString("messages.plugin_enable")));

    }

    @Override
    public void onDisable() {
        getLogger().info(colorize(getConfig().getString("messages.plugin_disable")));
        // Plugin shutdown logic
    }

    public static SizePlayer getPlugin() {
        return plugin;
    }

    public static String colorize(String message) {
        if (message == null) return null;
        Pattern pattern = Pattern.compile("&#([a-fA-F0-9]{6})");
        Matcher matcher = pattern.matcher(message);
        while (matcher.find()) {
            String hex = matcher.group(1);
            StringBuilder builder = new StringBuilder("§x");
            for (char c : hex.toCharArray()) builder.append('§').append(c);
            message = message.replace("&#" + hex, builder.toString());
        }
        return ChatColor.translateAlternateColorCodes('&', message);
    }
}
