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
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.math.BigDecimal;
import java.util.*;

public class SizePlayerCMD implements CommandExecutor, TabCompleter {

    private final SizePlayer plugin;
    private final ConfigManager configManager;

    private BigDecimal sizeChange;

    private final Map<UUID, BukkitTask> changingPlayers;

    private String minFormula;
    private String maxFormula;

    private BigDecimal minValue;
    private BigDecimal maxValue;

    public SizePlayerCMD(SizePlayer plugin, ConfigManager configManager, Map<UUID, BukkitTask> changingPlayers) {
        this.plugin = plugin;
        this.configManager = configManager;
        this.changingPlayers = changingPlayers;
        sizeChange = configManager.getScaleStep();
        minValue = configManager.getMinValue();
        maxValue = configManager.getMaxValue();
        minFormula = configManager.getMinFormula();
        maxFormula = configManager.getMaxFormula();

    }


    @Override
    public boolean onCommand(@NotNull CommandSender commandSender,
                             @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        // проверка если нет аргументов
        if (strings.length < 1) {
            commandSender.sendMessage(SizePlayer.colorize(configManager.getLangMap().get("no_arguments")));
            return true;
        }

        switch (strings[0]) {
            // перезапуск конфига
            case "reload":

                if (!(commandSender.hasPermission("sizeplayer.reload"))) {
                    commandSender.sendMessage(SizePlayer.colorize(configManager.getLangMap().get("no_permissions")));
                    return true;
                }

                String oldMinFormula = minFormula;
                String oldMaxFormula = maxFormula;

                //перезапуск конфига
                configManager.reloadConfig();

                //обновление переменных
                sizeChange = configManager.getScaleStep();
                minValue = configManager.getMinValue();
                maxValue = configManager.getMaxValue();
                minFormula = configManager.getMinFormula();
                maxFormula = configManager.getMaxFormula();

                try {
                    Expression expressionMinFormula = new ExpressionBuilder(minFormula).variable("x").build();
                    expressionMinFormula.setVariable("x", 0.4).evaluate();
                } catch (IllegalArgumentException | ArithmeticException e) {
                    commandSender.sendMessage(SizePlayer.colorize(configManager.getLangMap().get("min_formula_invalid")));
                    configManager.setMinFormula(oldMinFormula);
                    minFormula = oldMinFormula;
                }
                try {
                    Expression expressionMaxFormula = new ExpressionBuilder(maxFormula).variable("x").build();
                    expressionMaxFormula.setVariable("x", 2).evaluate();
                } catch (IllegalArgumentException | ArithmeticException e) {
                    commandSender.sendMessage(SizePlayer.colorize(configManager.getLangMap().get("max_formula_invalid")));
                    configManager.setMaxFormula(oldMaxFormula);
                    maxFormula = oldMaxFormula;
                }

                commandSender.sendMessage(SizePlayer.colorize(configManager.getLangMap().get("config_reloaded")));
                return true;

            case "set":
                if (!(commandSender instanceof Player sender)) {
                    commandSender.sendMessage(SizePlayer.colorize(configManager.getLangMap().get("console_message")));
                    return true;
                }
                if (strings.length < 2) {
                    commandSender.sendMessage(SizePlayer.colorize(configManager.getLangMap().get("no_arguments")));
                    return true;
                }
                if (!(commandSender.hasPermission("sizeplayer.set"))) {
                    commandSender.sendMessage(SizePlayer.colorize(configManager.getLangMap().get("no_permissions")));
                    return true;
                }

                if (strings.length < 3) {
                    changeSize(strings[1], sender, sender);
                    return true;
                }

                if (!(commandSender.hasPermission("sizeplayer.set.names"))) {
                    commandSender.sendMessage(SizePlayer.colorize(configManager.getLangMap().get("no_permissions")));
                    return true;
                }

                Player target = Bukkit.getPlayerExact(strings[2]);
                // проверяет, онлайн ли цель
                if (target == null) {
                    commandSender.sendMessage(SizePlayer.colorize(configManager.getLangMap().get("no_online")));
                    return true;
                }
                changeSize(strings[1],target, sender);
                return true;
            default:
                commandSender.sendMessage(SizePlayer.colorize(configManager.getLangMap().get("no_command")));
                return true;
        }
    }

    private void changeSize(String arg, Player target, Player sender) {
        BigDecimal numValue;

        try {
            numValue = new BigDecimal(arg);
        } catch (NumberFormatException e) {
            sender.sendMessage(SizePlayer.colorize(configManager.getLangMap().get("no_number")));
            return;
        }

        if (numValue.compareTo(BigDecimal.valueOf(Objects.requireNonNull(target.getAttribute(Attribute.GENERIC_SCALE)).getValue())) == 0) {
            sender.sendMessage(SizePlayer.colorize(configManager.getLangMap().get("size_already_set")));
            return;
        }

        // проверка на диапазон размера
        if (numValue.compareTo(minValue) < 0 || numValue.compareTo(maxValue) > 0) {
            sender.sendMessage(SizePlayer.colorize(setPlaceholdersNoPlayer(configManager.getLangMap().get("invalid_range"))));
            return;
        }

        UUID uuidTarget = target.getUniqueId();

        if (changingPlayers.containsKey(uuidTarget)) {
            sender.sendMessage(SizePlayer.colorize(configManager.getLangMap().get("size_already_changing")));
            return;
        }

        if (configManager.isAnimationEnabled()) {
            animationSize(sender, target, numValue);
            return;
        }
        setSize(sender, target, numValue);
    }

    private void animationSize(Player sender, Player target, BigDecimal size) {

        UUID targetId = target.getUniqueId();

        BukkitTask task = new BukkitRunnable() {

            @Override
            public void run() {
                BigDecimal playerSize = BigDecimal.valueOf(Objects.requireNonNull(target.getAttribute(Attribute.GENERIC_SCALE)).getValue());


                if (playerSize.compareTo(size) < 0) {

                    BigDecimal newSize = playerSize.add(sizeChange);

                    if (newSize.compareTo(size) >= 0) {
                        setSize(sender, target, size);
                        cancel();
                        return;
                    }
                    Objects.requireNonNull(target.getAttribute(Attribute.GENERIC_SCALE)).setBaseValue(newSize.doubleValue());
                }

                if (playerSize.compareTo(size) > 0) {

                    BigDecimal newSize = playerSize.subtract(sizeChange);

                    if (newSize.compareTo(size) <= 0) {
                        setSize(sender, target, size);
                        cancel();
                        return;
                    }
                    Objects.requireNonNull(target.getAttribute(Attribute.GENERIC_SCALE)).setBaseValue(newSize.doubleValue());
                }

            }

        }.runTaskTimer(plugin, 1L, configManager.getAnimationInterval());

        changingPlayers.put(targetId, task);

    }
    private void setSize(Player sender, Player target, BigDecimal size) {

        double sizeDouble = size.doubleValue();

        Objects.requireNonNull(target.getAttribute(Attribute.GENERIC_SCALE)).setBaseValue(sizeDouble);
        Objects.requireNonNull(target.getAttribute(Attribute.GENERIC_MAX_HEALTH)).setBaseValue(20);

        // проверяет, к какой формуле относится размер и в связи с этим применяет формулу хп
        try {
            if (sizeDouble < 1) {
                Expression expression = new ExpressionBuilder(minFormula).variable("x").build();
                Objects.requireNonNull(target.getAttribute(Attribute.GENERIC_MAX_HEALTH))
                        .setBaseValue(expression.setVariable("x",sizeDouble).evaluate());
            }
            else if (sizeDouble > 1) {
                Expression expression = new ExpressionBuilder(maxFormula).variable("x").build();
                Objects.requireNonNull(target.getAttribute(Attribute.GENERIC_MAX_HEALTH))
                        .setBaseValue(expression.setVariable("x",sizeDouble).evaluate());
            }
        } catch (IllegalArgumentException | ArithmeticException e) {
            sender.sendMessage(SizePlayer.colorize(configManager.getLangMap().get("formula_invalid")));
            changingPlayers.remove(target.getUniqueId());
            return;
        }

        changingPlayers.remove(target.getUniqueId());

        /* проверяет какое сообщение вывести об изменении роста в зависимости от команды (если в set цель является вводящим
        команду, то это будет работать как для команды own */
        if (!sender.getUniqueId().equals(target.getUniqueId())) {
            sender.sendMessage(SizePlayer.colorize(setPlaceholdersPlayer(configManager.getLangMap().get("size_set_sender"), target)));
            target.sendMessage(SizePlayer.colorize(setPlaceholdersPlayer(configManager.getLangMap().get("size_set_target"), sender)));
        } else {
            sender.sendMessage(SizePlayer.colorize(configManager.getLangMap().get("size_set_own")));
        }
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender,
                                                @NotNull Command command, @NotNull String s, @NotNull String[] strings) {

        List<String> tab = new ArrayList<>();
        if (strings.length == 1) {
            tab.add("set");
            tab.add("reload");
        }

        if (strings[0].equals("set") && strings.length == 2) {
            tab.add(String.valueOf(minValue));
            tab.add("1");
            tab.add(String.valueOf(maxValue));
        }

        if (commandSender.hasPermission("sizeplayer.set.names")) {
            if (strings[0].equals("set") && strings.length == 3) {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    tab.add(player.getName());
                }
            }
        }

        return tab;
    }

    // замена плейсхолдеров в некоторых сообщениях
    public String setPlaceholdersPlayer(String message, Player player) {
        return message
                .replace("%player_target%", player.getName())
                .replace("%player_sender%", player.getName());
    }
    public String setPlaceholdersNoPlayer(String message) {
        return message
                .replace("%min_value%", String.valueOf(minValue))
                .replace("%max_value%", String.valueOf(maxValue));
    }

}
