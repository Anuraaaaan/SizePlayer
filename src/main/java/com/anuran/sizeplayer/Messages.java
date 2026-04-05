package com.anuran.sizeplayer;

public class Messages {

    public static void register() {
        SizePlayer plugin = SizePlayer.getPlugin();

        int version = 2;

        if (plugin.getConfig().getInt("version") == 0) {
            //установка значений в конфиг
            plugin.getConfig().set("version", version);

            //values
            plugin.getConfig().set("values.min_value", 0.4);
            plugin.getConfig().set("values.max_value", 10);
            plugin.getConfig().set("values.min_formula", "20 * x");
            plugin.getConfig().set("values.max_formula", "2.22222 * x + 17.77778");

            //messages
            plugin.getConfig().set("messages.plugin_enable", "&#138808Plugin enabled");
            plugin.getConfig().set("messages.plugin_disable", "&#ff0033Plugin disabled");
            plugin.getConfig().set("messages.config_reloaded", "&#138808Config reloaded");

            plugin.getConfig().set("messages.console_message", "&#ff0033You are not a player");
            plugin.getConfig().set("messages.no_permissions", "&#ff0033You do not have permission");
            plugin.getConfig().set("messages.no_arguments", "&#ff0033You did not provide arguments or provided incomplete arguments");
            plugin.getConfig().set("messages.no_online", "&#ff0033Player is not online");
            plugin.getConfig().set("messages.no_number", "&#ff0033You did not enter a number");

            plugin.getConfig().set("messages.invalid_range", "&#ff0033Your size is not within the range %min_value% - %max_value%");
            plugin.getConfig().set("messages.formula_invalid", "&#ff0033The formula specified in the config is incorrect");

            plugin.getConfig().set("messages.size_set_sender", "&#138808You changed the size of %player_target%");
            plugin.getConfig().set("messages.size_set_target", "&#138808Your size was changed by %player_sender%");
            plugin.getConfig().set("messages.size_set_own", "&#138808You changed your size");
        }
        // сохранение конфига
        plugin.saveConfig();
    }
}
