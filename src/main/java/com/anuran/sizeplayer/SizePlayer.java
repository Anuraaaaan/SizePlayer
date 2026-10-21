package com.anuran.sizeplayer;

import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class SizePlayer extends JavaPlugin {

    private ConfigManager configManager;

    private final Map<UUID, BukkitTask> changingPlayers = new HashMap<>();

    @Override
    public void onEnable() {
        configManager = new ConfigManager(this);
        configManager.loadConfig();

        Objects.requireNonNull(getServer().getPluginCommand("sizeplayer")).setExecutor(new SizePlayerCMD(this, configManager, changingPlayers));

        this.getServer().getPluginManager().registerEvents(new PlayerUUIDRemover(changingPlayers), this);

        getLogger().info(colorize(configManager.getLangMap().get("plugin_enable")));

        try {
            Expression expressionMinFormula = new ExpressionBuilder(configManager.getMinFormula()).variable("x").build();
            expressionMinFormula.setVariable("x", 0.4).evaluate();
        } catch (IllegalArgumentException | ArithmeticException e) {
            this.getLogger().warning(SizePlayer.colorize(configManager.getLangMap().get("min_formula_invalid_start_server")));
            this.getServer().getPluginManager().disablePlugin(this);
        }
        try {
            Expression expressionMaxFormula = new ExpressionBuilder(configManager.getMaxFormula()).variable("x").build();
            expressionMaxFormula.setVariable("x", 2).evaluate();
        } catch (IllegalArgumentException | ArithmeticException e) {
            this.getLogger().warning(SizePlayer.colorize(configManager.getLangMap().get("max_formula_invalid_start_server")));
            this.getServer().getPluginManager().disablePlugin(this);
        }

    }

    @Override
    public void onDisable() {
        getLogger().info(colorize(configManager.getLangMap().get("plugin_disable")));
        // Plugin shutdown logic
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
