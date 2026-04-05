package com.anuran.sizeplayer;

import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;
import org.bukkit.Bukkit;
import org.bukkit.attribute.Attribute;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class SizePlayerCMD implements CommandExecutor, TabCompleter {

    // замена плейсхолдеров в некоторых сообщениях
    public String setPlaceholdersPlayer(String message, Player player) {
        return message
                .replace("%player_target%", player.getName())
                .replace("%player_sender%", player.getName());
    }
    public String setPlaceholdersNoPlayer(String message) {
        return message
                .replace("%min_value%", String.valueOf(SizePlayer.minValue))
                .replace("%max_value%", String.valueOf(SizePlayer.maxValue));
    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender,
                             @NotNull Command command, @NotNull String s, @NotNull String[] strings) {

        SizePlayer plugin = SizePlayer.getPlugin();
        // проверка если нет аргументов
        if (strings.length < 1) {
            commandSender.sendMessage(SizePlayer.colorize(SizePlayer.getMSG("messages.no_arguments")));
            return true;
        }

        switch (strings[0]) {
            // перезапуск конфига
            case "reload":

                if (!(commandSender.hasPermission("sizeplayer.reload"))) {
                    commandSender.sendMessage(SizePlayer.colorize(SizePlayer.getMSG("messages.no_permissions")));
                    return true;
                }

                //перезапуск конфига
                plugin.reloadConfig();

                //вывод сообщения о перезагрузке

                commandSender.sendMessage(SizePlayer.colorize(SizePlayer.getMSG("messages.config_reloaded")));

                //обновление переменных
                SizePlayer.minValue = Double.parseDouble(SizePlayer.getMSG("values.min_value"));
                SizePlayer.maxValue = Double.parseDouble(SizePlayer.getMSG("values.max_value"));
                SizePlayer.minFormula = SizePlayer.getMSG("values.min_formula");
                SizePlayer.maxFormula = SizePlayer.getMSG("values.max_formula");
                return true;

            // меняет рост игроку вызвавшему команду
            case "own":
                // проверка на количество аргументов
                if (strings.length < 2) {
                    commandSender.sendMessage(SizePlayer.colorize(SizePlayer.getMSG("messages.no_arguments")));
                    return true;
                }
                // проверка на консоль
                if (!(commandSender instanceof Player)) {
                    commandSender.sendMessage(SizePlayer.colorize(SizePlayer.getMSG("messages.console_message")));
                    return true;
                }
                // проверка на разрешение
                if (!(commandSender.hasPermission("sizeplayer.own"))) {
                    commandSender.sendMessage(SizePlayer.colorize(SizePlayer.getMSG("messages.no_permissions")));
                    return true;
                }

                changeSize(strings[1],(Player) commandSender, (Player) commandSender);

                return true;

            // меняет рост игроку из аргумента
            case "set":
                // проверка на консоль
                if (!(commandSender instanceof Player)) {
                    commandSender.sendMessage(SizePlayer.colorize(SizePlayer.getMSG("messages.console_message")));
                    return true;
                }
                // проверка на количество аргументов
                if (strings.length < 3) {
                    commandSender.sendMessage(SizePlayer.colorize(SizePlayer.getMSG("messages.no_arguments")));
                    return true;
                }
                // проверка на разрешение
                if (!(commandSender.hasPermission("sizeplayer.set"))) {
                    commandSender.sendMessage(SizePlayer.colorize(SizePlayer.getMSG("messages.no_permissions")));
                    return true;
                }
                // сохраняет ник цели
                Player target = Bukkit.getPlayerExact(strings[1]);
                // проверяет, онлайн ли цель
                if (target == null) {
                    commandSender.sendMessage(SizePlayer.colorize(SizePlayer.getMSG("messages.no_online")));
                    return true;
                }

                changeSize(strings[2],target, (Player) commandSender);

                return true;
        }
        return true;
    }

    private void changeSize(String arg, Player target, Player player) {
        double numValue;
        // конвертирует значение роста из команды в double
        try {
            numValue = Double.parseDouble(arg);
        } catch (NumberFormatException e) {
            player.sendMessage(SizePlayer.colorize(SizePlayer.getMSG("messages.no_number")));
            return;
        }
        // проверка на диапазон размера
        if (numValue < SizePlayer.minValue || numValue > SizePlayer.maxValue) {
            player.sendMessage(SizePlayer.colorize(setPlaceholdersNoPlayer(SizePlayer.getMSG("messages.invalid_range"))));
            return;
        }
        // ставит размер и откатывает количество хп, во избежание ошибки
        Objects.requireNonNull(target.getAttribute(Attribute.GENERIC_SCALE)).setBaseValue(numValue);
        Objects.requireNonNull(target.getAttribute(Attribute.GENERIC_MAX_HEALTH)).setBaseValue(20);

        // проверяет, к какой формуле относится размер и в связи с этим применяет формулу хп
        try {
            if (numValue < 1) {
                Expression expression = new ExpressionBuilder(SizePlayer.minFormula).variable("x").build();
                Objects.requireNonNull(target.getAttribute(Attribute.GENERIC_MAX_HEALTH))
                        .setBaseValue(expression.setVariable("x",numValue).evaluate());
            } else if (numValue > 1) {
                Expression expression = new ExpressionBuilder(SizePlayer.maxFormula).variable("x").build();
                Objects.requireNonNull(target.getAttribute(Attribute.GENERIC_MAX_HEALTH))
                        .setBaseValue(expression.setVariable("x",numValue).evaluate());
            }
        } catch (IllegalArgumentException | ArithmeticException e) {
            player.sendMessage(SizePlayer.colorize(SizePlayer.getMSG("messages.formula_invalid")));
            return;
        }
        /* проверяет какое сообщение вывести об изменении роста в зависимости от команды (если в set цель является вводящим
        команду, то это будет работать как для команды own */
        if (!player.getName().equals(target.getName())) {
            player.sendMessage(SizePlayer.colorize(setPlaceholdersPlayer(SizePlayer.getMSG("messages.size_set_sender"), target)));
            target.sendMessage(SizePlayer.colorize(setPlaceholdersPlayer(SizePlayer.getMSG("messages.size_set_target"), player)));
        } else {
            player.sendMessage(SizePlayer.colorize(SizePlayer.getMSG("messages.size_set_own")));
        }


    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender,
                                                @NotNull Command command, @NotNull String s, @NotNull String[] strings) {

        List<String> tab = new ArrayList<>();
        if (strings.length == 1) {
            tab.add("own");
            tab.add("set");
            tab.add("reload");
        }
        if (strings[0].equals("set") && strings.length == 2) {
            for (Player player : Bukkit.getOnlinePlayers()) {
                tab.add(player.getName());
            }
        }
        if (strings[0].equals("own") && strings.length == 2) {
            tab.add(String.valueOf(SizePlayer.minValue));
            tab.add("1");
            tab.add(String.valueOf(SizePlayer.maxValue));
        }
        if (strings[0].equals("set") && strings.length == 3) {
            tab.add(String.valueOf(SizePlayer.minValue));
            tab.add("1");
            tab.add(String.valueOf(SizePlayer.maxValue));
        }

        return tab;
    }

}
