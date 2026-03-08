package com.smart.smartAI.listener;

import com.smart.smartAI.ai.AIChecker;
import com.smart.smartAI.alert.AlertManager;
import com.smart.smartAI.collector.DataCollector;
import com.smart.smartAI.command.DataCommand;
import com.smart.smartAI.violation.ViolationTracker;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Слушатель событий атаки для отслеживания боя
 */
public class CombatListener implements Listener {
    private final DataCollector collector;
    private final AIChecker aiChecker;
    private final AlertManager alertManager;
    private final int sampleInterval;
    private final Map<UUID, Integer> hitCounters;
    private final ViolationTracker violationTracker;
    private DataCommand dataCommand;

    public CombatListener(DataCollector collector, AIChecker aiChecker, AlertManager alertManager, 
                         int sampleInterval, ViolationTracker violationTracker) {
        this.collector = collector;
        this.aiChecker = aiChecker;
        this.alertManager = alertManager;
        this.sampleInterval = sampleInterval;
        this.hitCounters = new HashMap<>();
        this.violationTracker = violationTracker;
    }

    public void setDataCommand(DataCommand dataCommand) {
        this.dataCommand = dataCommand;
    }

    @EventHandler
    public void onAttack(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player) {
            Player player = (Player) event.getDamager();
            UUID uuid = player.getUniqueId();
            
            // Сбор данных
            if (collector.hasSession(uuid)) {
                collector.onAttack(uuid);
            }
            
            // AI проверка с интервалом
            if (aiChecker != null && alertManager != null && violationTracker != null) {
                aiChecker.onAttack(player);
                
                // Увеличиваем счетчик ударов
                int hits = hitCounters.getOrDefault(uuid, 0) + 1;
                hitCounters.put(uuid, hits);
                
                // Проверяем только каждые N ударов (sample-interval)
                if (hits % sampleInterval == 0) {
                    aiChecker.checkPlayer(player, (p, response, hitNumber) -> {
                        if (response != null && !response.hasError()) {
                            // Обрабатываем вероятность через ViolationTracker
                            double adjustedProb = violationTracker.processHit(uuid, response.getProbability());
                            
                            // Обновляем голограмму со скорректированной вероятностью
                            aiChecker.updateHologramProbability(uuid, adjustedProb);
                            
                            // Отправляем в чат для отслеживающих
                            if (dataCommand != null) {
                                dataCommand.sendProbToChat(p.getName(), adjustedProb, hitNumber);
                            }
                            
                            // Отправляем алерт с скорректированной вероятностью
                            alertManager.sendAlert(p.getName(), adjustedProb, hitNumber);
                        }
                    });
                }
            }
        }
    }
}
