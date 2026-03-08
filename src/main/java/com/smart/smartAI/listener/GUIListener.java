package com.smart.smartAI.listener;

import com.smart.smartAI.gui.SuspectsGUI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

/**
 * Обработчик кликов в GUI
 */
public class GUIListener implements Listener {
    private final SuspectsGUI suspectsGUI;

    public GUIListener(SuspectsGUI suspectsGUI) {
        this.suspectsGUI = suspectsGUI;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        String title = ChatColor.stripColor(event.getView().getTitle());
        
        if (!title.contains("SmartAI") || !title.contains("Suspects")) {
            return;
        }

        event.setCancelled(true);

        if (!(event.getWhoClicked() instanceof Player)) {
            return;
        }

        Player viewer = (Player) event.getWhoClicked();
        ItemStack clicked = event.getCurrentItem();

        if (clicked == null || !clicked.hasItemMeta()) {
            return;
        }

        // Проверяем что это голова игрока
        if (clicked.getItemMeta() instanceof SkullMeta) {
            SkullMeta meta = (SkullMeta) clicked.getItemMeta();
            
            if (meta.getOwningPlayer() != null) {
                String targetName = ChatColor.stripColor(meta.getDisplayName());
                Player target = Bukkit.getPlayer(targetName);
                
                boolean rightClick = event.isRightClick();
                suspectsGUI.handleClick(viewer, target, rightClick);
            }
        }
    }
}
