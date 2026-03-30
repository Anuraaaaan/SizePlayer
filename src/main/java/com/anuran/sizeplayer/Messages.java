package com.anuran.sizeplayer;

public class Messages {

    public static void register() {
        SizePlayer plugin = SizePlayer.getPlugin();

        int version = 1;

        if (plugin.getConfig().getInt("version") == 0) {

            plugin.getConfig().set("version", 1);

            plugin.getConfig().set("console-message", "Эту комманду может вводить только игрок");
            plugin.getConfig().set("no-permissions", "У вас недостаточно прав");
            plugin.getConfig().set("config-reloaded", "Конфинг перезагружен");
            plugin.getConfig().set("size-set", "Ваш размер установлен на %d");
            plugin.getConfig().set("invalid-range", "Значение размера находится вне диапазона от 0.4 до 10.0");
        }

        plugin.saveConfig();
    }
}
