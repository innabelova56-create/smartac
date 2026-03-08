package com.smart.smartAI.gui;

import com.smart.smartAI.hologram.HologramManager;
import com.smart.smartAI.spectator.SpectatorManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * GUI для просмотра подозрительных игроков
 */
public class SuspectsGUI {
    private final HologramManager hologramManager;
    private final SpectatorManager spectatorManager;

    public SuspectsGUI(HologramManager hologramManager, SpectatorManager spectatorManager) {
        this.hologramManager = hologramManager;
        this.spectatorManager = spectatorManager;
    }

    public void openGUI(Player viewer) {
        Inventory inv = Bukkit.createInventory(null, 54, 
            ChatColor.translateAlternateColorCodes('&', "&cSmartAI &8> &7Подозрительные"));

        // Получаем всех подозрительных игроков (у кого есть данные)
        List<Player> suspects = new ArrayList<>();
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (hologramManager != null) {
                List<Double> history = hologramManager.getPlayerHistory(player.getUniqueId());
                if (history != null && !history.isEmpty()) {
                    suspects.add(player);
                }
            }
        }

        // Заполняем GUI головами игроков
        int slot = 0;
        for (Player suspect : suspects) {
            if (slot >= 45) break; // Максимум 45 слотов

            ItemStack head = new ItemStack(Material.PLAYER_HEAD);
            SkullMeta meta = (SkullMeta) head.getItemMeta();
            
            if (meta != null) {
                meta.setOwningPlayer(suspect);
                
                // Получаем реальные данные
                List<Double> history = hologramManager.getPlayerHistory(suspect.getUniqueId());
                double avgProb = hologramManager.getPlayerAverage(suspect.getUniqueId());
                
                // Название - ник с цветом и процентом
                String displayName = ChatColor.AQUA + "▶ " + suspect.getName() + " " + 
                                    getColorBar(avgProb) + " " + 
                                    ChatColor.YELLOW + String.format("%.0f%%", avgProb * 100);
                meta.setDisplayName(displayName);
                
                // Добавляем лор с информацией
                List<String> lore = new ArrayList<>();
                lore.add("");
                lore.add(ChatColor.WHITE + "Последние проверки:");
                
                // Показываем последние 21 удар (3 ряда по 7)
                if (history != null && !history.isEmpty()) {
                    int totalHits = Math.min(history.size(), 21);
                    int start = Math.max(0, history.size() - 21);
                    
                    StringBuilder row = new StringBuilder();
                    int count = 0;
                    
                    for (int i = start; i < history.size(); i++) {
                        double prob = history.get(i);
                        row.append(getColoredProb(prob)).append(" ");
                        count++;
                        
                        // Каждые 7 ударов - новая строка
                        if (count % 7 == 0) {
                            lore.add(row.toString().trim());
                            row = new StringBuilder();
                        }
                    }
                    
                    // Добавляем остаток если есть
                    if (row.length() > 0) {
                        lore.add(row.toString().trim());
                    }
                }
                
                lore.add("");
                lore.add(ChatColor.WHITE + "Средний риск:");
                lore.add(ChatColor.GOLD + "AVG " + getColoredPercent(avgProb));
                lore.add("");
                lore.add(ChatColor.AQUA + "▶ Нажмите, чтобы следить");
                
                meta.setLore(lore);
                head.setItemMeta(meta);
            }

            inv.setItem(slot++, head);
        }

        // Заполняем пустые слоты серым стеклом
        ItemStack filler = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        for (int i = 45; i < 54; i++) {
            inv.setItem(i, filler);
        }

        viewer.openInventory(inv);
    }

    private String getColorBar(double probability) {
        int filled = (int) (probability * 6); // 6 квадратиков максимум
        StringBuilder bar = new StringBuilder("[");
        
        ChatColor color;
        if (probability >= 0.8) {
            color = ChatColor.RED;
        } else if (probability >= 0.6) {
            color = ChatColor.GOLD;
        } else {
            color = ChatColor.GREEN;
        }
        
        for (int i = 0; i < 6; i++) {
            if (i < filled) {
                bar.append(color).append("■");
            } else {
                bar.append(ChatColor.GRAY).append("■");
            }
        }
        bar.append(ChatColor.WHITE).append("]");
        
        return bar.toString();
    }

    private String getColoredProb(double probability) {
        ChatColor color;
        if (probability >= 0.8) {
            color = ChatColor.RED;
        } else if (probability >= 0.6) {
            color = ChatColor.GOLD;
        } else {
            color = ChatColor.GREEN;
        }
        return color + String.format("%.4f", probability);
    }

    private String getColoredPercent(double probability) {
        ChatColor color;
        if (probability >= 0.8) {
            color = ChatColor.RED;
        } else if (probability >= 0.6) {
            color = ChatColor.GOLD;
        } else {
            color = ChatColor.GREEN;
        }
        return color + String.format("%.1f%%", probability * 100);
    }

    public void handleClick(Player viewer, Player target, boolean rightClick) {
        if (target == null || !target.isOnline()) {
            viewer.sendMessage(ChatColor.translateAlternateColorCodes('&', 
                "&cPlayer is no longer online."));
            return;
        }

        if (rightClick) {
            // ПКМ - телепорт + спектатор с кнопкой возврата
            spectatorManager.enterSpectator(viewer, target);
            viewer.sendMessage(ChatColor.translateAlternateColorCodes('&', 
                "&aTeleported to " + target.getName() + " in Spectator mode"));
        } else {
            // ЛКМ - просто телепорт
            viewer.teleport(target.getLocation());
            viewer.sendMessage(ChatColor.translateAlternateColorCodes('&', 
                "&aTeleported to " + target.getName()));
        }
        
        viewer.closeInventory();
    }

    public SpectatorManager getSpectatorManager() {
        return spectatorManager;
    }
}
