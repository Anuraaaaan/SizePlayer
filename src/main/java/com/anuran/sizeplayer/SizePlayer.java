package com.anuran.sizeplayer;

import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

public final class SizePlayer extends JavaPlugin {

    public static SizePlayer plugin;

    @Override
    public void onEnable() {
        plugin = this;
        saveDefaultConfig();

        Messages.register();
        Objects.requireNonNull(getServer().getPluginCommand("sizeplayer")).setExecutor(new sizeCMD());
        Objects.requireNonNull(getServer().getPluginCommand("sizereload")).setExecutor(new confReloadCMD());

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public static SizePlayer getPlugin() {
        return plugin;
    }
}
