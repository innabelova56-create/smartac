package com.smart.smartAI.alert;

import com.smart.smartAI.command.DataCommand;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

/**
 * Менеджер алертов для админов
 */
public class AlertManager {
    private final double alertThreshold;
    private DataCommand dataCommand;

    public AlertManager(double alertThreshold) {
        this.alertThreshold = alertThreshold;
    }

    public void setDataCommand(DataCommand dataCommand) {
        this.dataCommand = dataCommand;
    }

    public void sendAlert(String playerName, double probability, int hitNumber) {
        // Показываем все оранжевые (>= 0.6) и красные (>= 0.8)
        if (probability < 0.6) {
            return;
        }

        String message = formatAlert(playerName, probability, hitNumber);
        
        // Отправка всем с правами и включенными алертами
        for (Player admin : Bukkit.getOnlinePlayers()) {
            if (admin.hasPermission("smartai.alerts")) {
                // Проверяем включены ли алерты у этого админа
                if (dataCommand == null || dataCommand.hasAlertsEnabled(admin.getUniqueId())) {
                    admin.sendMessage(message);
                }
            }
        }

        // В консоль только красные (>= 0.8)
        if (probability >= 0.8) {
            Bukkit.getLogger().warning("[SmartAI] " + ChatColor.stripColor(message));
        }
    }

    private String formatAlert(String playerName, double probability, int hitNumber) {
        // Определяем цвет процента по вероятности
        ChatColor percentColor;
        
        if (probability >= 0.8) {
            percentColor = ChatColor.RED;
        } else if (probability >= 0.6) {
            percentColor = ChatColor.GOLD;
        } else {
            percentColor = ChatColor.GREEN;
        }

        // Градиент HAC | AC (используем упрощенную версию без hex для 1.16.5)
        String hacGradient = ChatColor.RED + "" + ChatColor.BOLD + "H" +
                            ChatColor.RED + "" + ChatColor.BOLD + "A" +
                            ChatColor.RED + "" + ChatColor.BOLD + "C " +
                            ChatColor.RED + "" + ChatColor.BOLD + "| " +
                            ChatColor.DARK_RED + "" + ChatColor.BOLD + "A" +
                            ChatColor.DARK_RED + "" + ChatColor.BOLD + "C";

        // Формат: HAC | AC player провалил AimCheck (процент%)
        return String.format(
            "%s %s%s %sпровалил %sAimCheck %s(%.1f%%)",
            hacGradient,
            ChatColor.WHITE,
            playerName,
            ChatColor.AQUA,
            ChatColor.WHITE,
            percentColor,
            probability * 100
        );
    }

    public boolean shouldAlert(double probability) {
        return probability >= alertThreshold;
    }
}
