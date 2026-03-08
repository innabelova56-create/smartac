package com.smart.smartAI.ai;

import com.smart.smartAI.data.TickData;
import com.smart.smartAI.hologram.HologramManager;
import com.smart.smartAI.processor.AimProcessor;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Проверка игроков через AI
 */
public class AIChecker {
    private final AIClient client;
    private final int sequenceLength;
    private final Map<UUID, PlayerCheckData> playerData;
    private final HologramManager hologramManager;
    private final Map<UUID, Integer> hitCounters;

    public AIChecker(AIClient client, int sequenceLength, HologramManager hologramManager) {
        this.client = client;
        this.sequenceLength = sequenceLength;
        this.playerData = new ConcurrentHashMap<>();
        this.hologramManager = hologramManager;
        this.hitCounters = new ConcurrentHashMap<>();
    }

    public void processTick(Player player, float yaw, float pitch) {
        PlayerCheckData data = playerData.computeIfAbsent(
            player.getUniqueId(),
            uuid -> new PlayerCheckData(sequenceLength)
        );

        data.processTick(yaw, pitch);
    }

    public void onAttack(Player player) {
        PlayerCheckData data = playerData.get(player.getUniqueId());
        if (data != null) {
            data.onAttack();
        }
        
        // Увеличиваем счетчик ударов
        hitCounters.merge(player.getUniqueId(), 1, Integer::sum);
    }

    public void checkPlayer(Player player, CheckCallback callback) {
        PlayerCheckData data = playerData.get(player.getUniqueId());
        if (data == null || data.getBufferSize() < sequenceLength) {
            callback.onResult(player, null, 0);
            return;
        }

        List<TickData> ticks = data.getTickBuffer();
        int hitNumber = hitCounters.getOrDefault(player.getUniqueId(), 0);
        
        client.predict(ticks).thenAccept(response -> {
            // НЕ добавляем вероятность здесь - она будет добавлена после коррекции
            callback.onResult(player, response, hitNumber);
        });
    }

    public void updateHologramProbability(UUID playerId, double probability) {
        if (hologramManager != null) {
            hologramManager.addProbability(playerId, probability);
        }
    }

    public void removePlayer(UUID uuid) {
        playerData.remove(uuid);
        hitCounters.remove(uuid);
        if (hologramManager != null) {
            hologramManager.removeHologram(uuid);
        }
    }

    public PlayerCheckData getPlayerData(UUID uuid) {
        return playerData.get(uuid);
    }

    public interface CheckCallback {
        void onResult(Player player, AIResponse response, int hitNumber);
    }

    private static class PlayerCheckData {
        private final AimProcessor processor;
        private final Deque<TickData> tickBuffer;
        private final int sequenceLength;
        private int ticksSinceAttack;

        public PlayerCheckData(int sequenceLength) {
            this.processor = new AimProcessor();
            this.tickBuffer = new ArrayDeque<>(sequenceLength);
            this.sequenceLength = sequenceLength;
            this.ticksSinceAttack = 100;
        }

        public void processTick(float yaw, float pitch) {
            TickData tick = processor.process(yaw, pitch);
            
            ticksSinceAttack++;
            
            // Сохраняем только тики в бою
            if (ticksSinceAttack <= sequenceLength) {
                if (tickBuffer.size() >= sequenceLength) {
                    tickBuffer.pollFirst();
                }
                tickBuffer.addLast(tick);
            }
        }

        public void onAttack() {
            ticksSinceAttack = 0;
        }

        public List<TickData> getTickBuffer() {
            return new ArrayList<>(tickBuffer);
        }

        public int getBufferSize() {
            return tickBuffer.size();
        }
    }
}
