package com.anuran.sizeplayer;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class ConfigManager {

    private final SizePlayer plugin;

    @Getter
    private Map<String, String> langMap = new HashMap<>();

    @Getter
    private BigDecimal minValue;
    @Getter
    private BigDecimal maxValue;

    @Getter
    @Setter
    private String minFormula;
    @Getter
    @Setter
    private String maxFormula;

    @Getter
    private boolean animationEnabled;

    @Getter
    private int animationInterval;

    @Getter
    private BigDecimal scaleStep;


    public ConfigManager(SizePlayer plugin) {
        this.plugin = plugin;
    }

    public void loadConfig () {
        plugin.saveDefaultConfig();

        config();
    }

    public void reloadConfig() {
        plugin.reloadConfig();

        config();
    }

    private void config() {
        langMap.clear();

        String lang = plugin.getConfig().getString("values.lang");

        langMap = loadLang(lang);

        minValue = new BigDecimal(plugin.getConfig().getString("values.min_value", "0.4"));
        maxValue = new BigDecimal(plugin.getConfig().getString("values.max_value", "10"));

        minFormula = plugin.getConfig().getString("values.min_formula", "20 * x");
        maxFormula = plugin.getConfig().getString("values.max_formula", "2.22222 * x + 17.77778");

        animationEnabled = plugin.getConfig().getBoolean("values.scale.animation_enabled", true);

        if (animationEnabled) {
            animationInterval = plugin.getConfig().getInt("values.scale.animation_interval", 1);
            scaleStep = new BigDecimal(plugin.getConfig().getString("values.scale.scale_step", "0.1"));
            if (animationInterval < 0) {
                animationInterval = 1;
            }

            if (scaleStep.compareTo(BigDecimal.ZERO) <= 0) {
                scaleStep = new BigDecimal("0.1");
            }
        }
    }


    private Map<String, String> loadLang(String lang) {

        if (lang == null || lang.isBlank()) {
            plugin.getLogger().warning("Language is not specified in config.yml");
            return null;
        }

        File langFile = new File(plugin.getDataFolder() + File.separator + lang + ".yml");
        InputStream inputStream = plugin.getResource("en.yml");

        if (!langFile.exists()) {
            plugin.saveResource(lang + ".yml", false);
        }

        FileConfiguration fileConfiguration = new YamlConfiguration();

        if (inputStream == null) {
            plugin.getLogger().warning("Language file was not found inside the jar");
            return null;
        }

        YamlConfiguration langDefault = YamlConfiguration.loadConfiguration(
                new InputStreamReader(inputStream, StandardCharsets.UTF_8));
        try {
            fileConfiguration.load(langFile);
        } catch (IOException | InvalidConfigurationException e) {
            plugin.getLogger().warning("Failed to load language file: " + langFile.getPath());
        }

        Map<String, String> mapLang = new HashMap<>();

        for (String key : fileConfiguration.getKeys(false)) {
            mapLang.put(key, fileConfiguration.getString(key));
        }

        boolean changed = false;
        for (String key : langDefault.getKeys(false)) {
            if (!mapLang.containsKey(key)) {
                String message = langDefault.getString(key);
                fileConfiguration.set(key, message);
                mapLang.put(key, message);

                changed = true;
            }
        }
        if (changed) {
            try {
                fileConfiguration.save(langFile);
            } catch (IOException e) {
                plugin.getLogger().warning("Failed to save language file: " + langFile.getPath());
            }
        }

        return mapLang;
    }


}
