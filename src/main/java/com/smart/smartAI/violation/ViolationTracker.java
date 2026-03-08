package com.smart.smartAI.violation;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Отслеживание нарушений игроков с накоплением
 */
public class ViolationTracker {
    private final Map<UUID, PlayerViolation> violations = new HashMap<>();
    private final double threshold;
    private final double multiplier;
    private final double decay;

    public ViolationTracker(double threshold, double multiplier, double decay) {
        this.threshold = threshold;
        this.multiplier = multiplier;
        this.decay = decay;
    }

    public double processHit(UUID playerId, double probability) {
        PlayerViolation vl = violations.computeIfAbsent(playerId, k -> new PlayerViolation());
        
        if (probability > threshold) {
            // Читерская активность - увеличиваем буфер
            double increase = (probability - threshold) * multiplier;
            vl.buffer += increase;
            
            // Буфер влияет на отображаемую вероятность
            // Чем больше буфер, тем выше показываем вероятность
            double bufferBonus = Math.min(vl.buffer / 100.0, 0.3); // Максимум +0.3
            return Math.min(probability + bufferBonus, 1.0);
        } else if (probability < 0.3) {
            // Легитная активность - уменьшаем буфер и вероятность
            vl.buffer = Math.max(0, vl.buffer - decay);
            
            // Агрессивное снижение для легитных игроков
            // Чем ниже вероятность, тем сильнее снижаем
            double reductionFactor = 1.0 - (probability / 0.3); // 0.0 при prob=0.3, 1.0 при prob=0.0
            double maxReduction = 0.25; // Максимум -25%
            double reduction = maxReduction * reductionFactor;
            
            return Math.max(probability - reduction, 0.0);
        }
        
        // Средняя активность (0.3-0.75) - буфер не меняется
        return probability;
    }

    public double getBuffer(UUID playerId) {
        PlayerViolation vl = violations.get(playerId);
        return vl != null ? vl.buffer : 0.0;
    }

    public void reset(UUID playerId) {
        violations.remove(playerId);
    }

    private static class PlayerViolation {
        double buffer = 0.0;
    }
}
