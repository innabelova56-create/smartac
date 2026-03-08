package com.smart.smartAI.spectator;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Управление режимом спектатора
 */
public class SpectatorManager {
    private final Map<UUID, SpectatorData> spectators = new HashMap<>();

    public void enterSpectator(Player viewer, Player target) {
        // Сохраняем текущее состояние
        SpectatorData data = new SpectatorData(
            viewer.getLocation().clone(),
            viewer.getGameMode()
        );
        spectators.put(viewer.getUniqueId(), data);

        // Телепортируем и включаем спектатор
        viewer.teleport(target.getLocation());
        viewer.setGameMode(GameMode.SPECTATOR);

        // Отправляем сообщение с кнопкой выхода
        sendExitButton(viewer);
    }

    public void exitSpectator(Player viewer) {
        SpectatorData data = spectators.remove(viewer.getUniqueId());
        
        if (data != null) {
            // Возвращаем на прежнее место
            viewer.teleport(data.previousLocation);
            viewer.setGameMode(data.previousGameMode);
            
            viewer.sendMessage(ChatColor.GREEN + "Вы вышли из режима наблюдения");
        } else {
            viewer.sendMessage(ChatColor.RED + "Вы не в режиме наблюдения");
        }
    }

    public boolean isSpectating(UUID playerId) {
        return spectators.containsKey(playerId);
    }

    private void sendExitButton(Player viewer) {
        // Создаем кликабельное сообщение
        TextComponent message = new TextComponent(ChatColor.GRAY + "[" + ChatColor.RED + "✖" + ChatColor.GRAY + "] ");
        TextComponent button = new TextComponent(ChatColor.YELLOW + "" + ChatColor.BOLD + "Выйти из режима наблюдения");
        
        button.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/smartai exitspectator"));
        button.setHoverEvent(new HoverEvent(
            HoverEvent.Action.SHOW_TEXT,
            new ComponentBuilder(ChatColor.GREEN + "Нажмите чтобы вернуться").create()
        ));
        
        message.addExtra(button);
        viewer.spigot().sendMessage(message);
    }

    private static class SpectatorData {
        final Location previousLocation;
        final GameMode previousGameMode;

        SpectatorData(Location previousLocation, GameMode previousGameMode) {
            this.previousLocation = previousLocation;
            this.previousGameMode = previousGameMode;
        }
    }
}
