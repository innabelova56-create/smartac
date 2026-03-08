package com.smart.smartAI.hologram;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Менеджер голограмм над игроками
 */
public class HologramManager {
    private final Map<UUID, PlayerHologram> holograms;
    private final Map<UUID, List<Double>> playerProbabilities;
    private BukkitTask updateTask;
    private final boolean enabled;

    public HologramManager(boolean enabled) {
        this.holograms = new ConcurrentHashMap<>();
        this.playerProbabilities = new ConcurrentHashMap<>();
        this.enabled = enabled;
    }

    public void start(org.bukkit.plugin.Plugin plugin) {
        if (!enabled) return;

        updateTask = new BukkitRunnable() {
            @Override
            public void run() {
                updateAllHolograms();
            }
        }.runTaskTimer(plugin, 20L, 1L); // Обновление каждый тик для плавности
    }

    public void stop() {
        if (updateTask != null) {
            updateTask.cancel();
        }
        
        // Удаление всех голограмм
        for (PlayerHologram hologram : holograms.values()) {
            hologram.remove();
        }
        holograms.clear();
        playerProbabilities.clear();
    }

    public void addProbability(UUID playerId, double probability) {
        if (!enabled) return;

        List<Double> probs = playerProbabilities.computeIfAbsent(playerId, k -> new ArrayList<>());
        probs.add(probability);
        
        // Храним последние 21 для GUI (3 ряда по 7), показываем последние 5 в голограмме
        if (probs.size() > 21) {
            probs.remove(0);
        }
    }

    private void updateAllHolograms() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            updateHologram(player);
        }
    }

    private void updateHologram(Player player) {
        List<Double> probs = playerProbabilities.get(player.getUniqueId());
        
        // Показываем голограмму только если есть данные
        if (probs == null || probs.isEmpty()) {
            removeHologram(player.getUniqueId());
            return;
        }

        // Вычисляем среднюю вероятность
        double avgProb = probs.stream().mapToDouble(Double::doubleValue).average().orElse(0.0);
        
        // Создаем или обновляем голограмму
        PlayerHologram hologram = holograms.computeIfAbsent(
            player.getUniqueId(),
            uuid -> new PlayerHologram(player)
        );
        
        // Берем последние 5 для отображения
        List<Double> lastFive = probs.size() > 5 
            ? probs.subList(probs.size() - 5, probs.size()) 
            : probs;
        
        // Обновляем позицию и текст
        hologram.updatePosition(player);
        hologram.updateText(avgProb, lastFive);
    }

    public void removeHologram(UUID playerId) {
        PlayerHologram hologram = holograms.remove(playerId);
        if (hologram != null) {
            hologram.remove();
        }
        playerProbabilities.remove(playerId);
    }

    public void handlePlayerQuit(Player player) {
        removeHologram(player.getUniqueId());
    }

    public List<Double> getPlayerHistory(UUID playerId) {
        return playerProbabilities.get(playerId);
    }

    public double getPlayerAverage(UUID playerId) {
        List<Double> probs = playerProbabilities.get(playerId);
        if (probs == null || probs.isEmpty()) {
            return 0.0;
        }
        return probs.stream().mapToDouble(Double::doubleValue).average().orElse(0.0);
    }

    private static class PlayerHologram {
        private final UUID playerId;
        private ArmorStand topLine;    // Верхняя строка - последние 5 ударов
        private ArmorStand bottomLine; // Нижняя строка - AVG
        private ArmorStand spacer;     // Невидимая проставка для высоты
        private Location lastLocation; // Последняя позиция игрока

        public PlayerHologram(Player player) {
            this.playerId = player.getUniqueId();
            this.lastLocation = player.getLocation().clone();
            createArmorStands(player);
        }

        private void createArmorStands(Player player) {
            Location loc = player.getLocation();
            
            // Создаем невидимую проставку для высоты
            spacer = (ArmorStand) player.getWorld().spawnEntity(loc, EntityType.ARMOR_STAND);
            spacer.setVisible(false);
            spacer.setGravity(false);
            spacer.setCustomNameVisible(false);
            spacer.setMarker(true);
            spacer.setInvulnerable(true);
            spacer.setSmall(true);
            
            // Нижняя строка (AVG)
            bottomLine = (ArmorStand) player.getWorld().spawnEntity(loc, EntityType.ARMOR_STAND);
            bottomLine.setVisible(false);
            bottomLine.setGravity(false);
            bottomLine.setCustomNameVisible(true);
            bottomLine.setMarker(true);
            bottomLine.setInvulnerable(true);
            bottomLine.setSmall(true);
            
            // Верхняя строка (история)
            topLine = (ArmorStand) player.getWorld().spawnEntity(loc, EntityType.ARMOR_STAND);
            topLine.setVisible(false);
            topLine.setGravity(false);
            topLine.setCustomNameVisible(true);
            topLine.setMarker(true);
            topLine.setInvulnerable(true);
            topLine.setSmall(true);
            
            // Делаем стек пассажиров: игрок -> spacer -> bottomLine -> topLine
            player.addPassenger(spacer);
            spacer.addPassenger(bottomLine);
            bottomLine.addPassenger(topLine);
        }

        public void updatePosition(Player player) {
            if (player == null || !player.isOnline()) {
                remove();
                return;
            }

            // Создаем ArmorStands если их нет или они невалидны
            if (topLine == null || !topLine.isValid() || bottomLine == null || !bottomLine.isValid() || spacer == null || !spacer.isValid()) {
                createArmorStands(player);
                return;
            }
            
            // Проверяем что они все еще пассажиры
            if (!player.getPassengers().contains(spacer)) {
                player.addPassenger(spacer);
            }
            if (!spacer.getPassengers().contains(bottomLine)) {
                spacer.addPassenger(bottomLine);
            }
            if (!bottomLine.getPassengers().contains(topLine)) {
                bottomLine.addPassenger(topLine);
            }
            
            // Позиция обновляется автоматически через систему пассажиров!
        }

        public void updateText(double avgProb, List<Double> history) {
            if (topLine == null || !topLine.isValid() || bottomLine == null || !bottomLine.isValid()) {
                return;
            }
            
            // Верхняя строка - последние 5 ударов с пробелами между ними
            StringBuilder topText = new StringBuilder();
            for (int i = 0; i < history.size(); i++) {
                Double prob = history.get(i);
                ChatColor color = getColor(prob);
                topText.append(color).append(String.format("%.4f", prob));
                if (i < history.size() - 1) {
                    topText.append(" "); // Просто пробел между значениями
                }
            }
            topLine.setCustomName(topText.toString());
            
            // Нижняя строка - "AVG: " белым, значение цветом
            ChatColor avgColor = getColor(avgProb);
            String bottomText = ChatColor.WHITE + "AVG: " + avgColor + String.format("%.4f", avgProb);
            bottomLine.setCustomName(bottomText);
        }

        private ChatColor getColor(double probability) {
            if (probability >= 0.8) {
                return ChatColor.RED; // Читер
            } else if (probability >= 0.6) {
                return ChatColor.GOLD; // Средний
            } else {
                return ChatColor.GREEN; // Легит
            }
        }

        public void remove() {
            if (spacer != null && spacer.isValid()) {
                spacer.remove();
            }
            if (topLine != null && topLine.isValid()) {
                topLine.remove();
            }
            if (bottomLine != null && bottomLine.isValid()) {
                bottomLine.remove();
            }
        }
    }
}
