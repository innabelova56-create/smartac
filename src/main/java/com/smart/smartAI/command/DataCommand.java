package com.smart.smartAI.command;

import com.smart.smartAI.SmartAI;
import com.smart.smartAI.collector.DataCollector;
import com.smart.smartAI.gui.SuspectsGUI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Команда для управления сбором данных и AI
 */
public class DataCommand implements CommandExecutor {
    private final SmartAI plugin;
    private final DataCollector collector;
    private final Map<UUID, Boolean> alertsEnabled;
    private final Map<UUID, UUID> trackingPlayers; // кто кого отслеживает
    private FileConfiguration messages;
    private SuspectsGUI suspectsGUI;

    public DataCommand(SmartAI plugin, DataCollector collector) {
        this.plugin = plugin;
        this.collector = collector;
        this.alertsEnabled = new HashMap<>();
        this.trackingPlayers = new HashMap<>();
        loadMessages();
    }

    public void setSuspectsGUI(SuspectsGUI suspectsGUI) {
        this.suspectsGUI = suspectsGUI;
    }

    private void loadMessages() {
        File messagesFile = new File(plugin.getDataFolder(), "messages.yml");
        if (!messagesFile.exists()) {
            plugin.saveResource("messages.yml", false);
        }
        messages = YamlConfiguration.loadConfiguration(messagesFile);
    }

    private String getMessage(String key) {
        String prefix = messages.getString("prefix", "&6[SmartAI] &r");
        String msg = messages.getString(key, "&cMessage not found: " + key);
        return ChatColor.translateAlternateColorCodes('&', prefix + msg);
    }

    private String getMessageNoPrefix(String key) {
        String msg = messages.getString(key, "&cMessage not found: " + key);
        return ChatColor.translateAlternateColorCodes('&', msg);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(getMessage("players-only"));
            return true;
        }
        
        Player player = (Player) sender;

        if (!player.hasPermission("smartai.admin")) {
            player.sendMessage(getMessage("no-permission"));
            return true;
        }

        if (args.length == 0) {
            sendHelp(player);
            return true;
        }

        String subCommand = args[0].toLowerCase();
        
        switch (subCommand) {
            case "start":
                handleStart(player, args);
                break;
            case "stop":
                handleStop(player, args);
                break;
            case "alerts":
                handleAlerts(player);
                break;
            case "prob":
                handleProb(player, args);
                break;
            case "datastatus":
                handleDataStatus(player);
                break;
            case "reload":
                handleReload(player);
                break;
            case "suspects":
                handleSuspects(player);
                break;
            case "punish":
                handlePunish(player, args);
                break;
            case "exitspectator":
                handleExitSpectator(player);
                break;
            default:
                player.sendMessage(getMessage("unknown-command").replace("{ARGS}", subCommand));
                sendHelp(player);
                break;
        }

        return true;
    }

    private void handleStart(Player player, String[] args) {
        if (args.length < 3) {
            player.sendMessage(ChatColor.RED + "Использование: /smartai start <player|global> <cheat|legit> [customName]");
            return;
        }

        String target = args[1].toLowerCase();
        String type = args[2].toLowerCase();
        boolean isCheating = type.equals("cheat");
        String customName = args.length >= 4 ? args[3] : null;

        if (!type.equals("cheat") && !type.equals("legit")) {
            player.sendMessage(getMessage("invalid-label").replace("{LABEL}", type));
            player.sendMessage(getMessage("valid-labels"));
            return;
        }

        int count = 0;
        if (target.equals("global")) {
            for (Player p : Bukkit.getOnlinePlayers()) {
                if (customName != null) {
                    collector.startSession(p, isCheating, customName + "_" + p.getName());
                } else {
                    collector.startSession(p, isCheating);
                }
                count++;
            }
        } else {
            Player targetPlayer = Bukkit.getPlayer(target);
            if (targetPlayer == null) {
                player.sendMessage(getMessage("player-not-found").replace("{PLAYER}", target));
                return;
            }
            if (customName != null) {
                collector.startSession(targetPlayer, isCheating, customName);
            } else {
                collector.startSession(targetPlayer, isCheating);
            }
            count = 1;
        }

        player.sendMessage(getMessage("session-started")
                .replace("{LABEL}", type.toUpperCase())
                .replace("{COUNT}", String.valueOf(count)));
    }

    private void handleStop(Player player, String[] args) {
        if (args.length < 2) {
            try {
                collector.stopSession(player);
                player.sendMessage(getMessage("session-stopped").replace("{PLAYER}", player.getName()));
            } catch (IOException e) {
                player.sendMessage(ChatColor.RED + "Ошибка: " + e.getMessage());
            }
            return;
        }

        String target = args[1].toLowerCase();
        if (target.equals("global")) {
            int count = 0;
            for (Player p : Bukkit.getOnlinePlayers()) {
                if (collector.hasSession(p.getUniqueId())) {
                    try {
                        collector.stopSession(p);
                        count++;
                    } catch (IOException e) {
                        player.sendMessage(ChatColor.RED + "Ошибка для " + p.getName() + ": " + e.getMessage());
                    }
                }
            }
            if (count > 0) {
                player.sendMessage(getMessage("all-sessions-stopped").replace("{COUNT}", String.valueOf(count)));
            } else {
                player.sendMessage(getMessage("no-sessions-to-stop"));
            }
        } else {
            Player targetPlayer = Bukkit.getPlayer(target);
            if (targetPlayer == null) {
                player.sendMessage(getMessage("player-not-found").replace("{PLAYER}", target));
                return;
            }
            try {
                collector.stopSession(targetPlayer);
                player.sendMessage(getMessage("session-stopped").replace("{PLAYER}", targetPlayer.getName()));
            } catch (IOException e) {
                player.sendMessage(ChatColor.RED + "Ошибка: " + e.getMessage());
            }
        }
    }

    private void handleAlerts(Player player) {
        UUID uuid = player.getUniqueId();
        boolean current = alertsEnabled.getOrDefault(uuid, true);
        alertsEnabled.put(uuid, !current);
        
        if (!current) {
            player.sendMessage(getMessage("alerts-enabled"));
        } else {
            player.sendMessage(getMessage("alerts-disabled"));
        }
    }

    private void handleProb(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage(getMessage("prob-usage"));
            return;
        }

        Player target = Bukkit.getPlayer(args[1]);
        if (target == null) {
            player.sendMessage(getMessage("player-not-found").replace("{PLAYER}", args[1]));
            return;
        }

        UUID trackerUUID = player.getUniqueId();
        if (trackingPlayers.containsKey(trackerUUID)) {
            trackingPlayers.remove(trackerUUID);
            player.sendMessage(getMessage("tracking-stopped"));
        } else {
            trackingPlayers.put(trackerUUID, target.getUniqueId());
            player.sendMessage(getMessage("tracking-started").replace("{PLAYER}", target.getName()));
            player.sendMessage(ChatColor.GRAY + "Вывод в чат каждый удар включен");
        }
    }

    // Метод для отправки информации об ударе в чат
    public void sendProbToChat(String playerName, double probability, int hitNumber) {
        // Определяем цвет
        ChatColor color;
        if (probability >= 0.8) {
            color = ChatColor.RED;
        } else if (probability >= 0.6) {
            color = ChatColor.GOLD;
        } else {
            color = ChatColor.GREEN;
        }

        String message = String.format(
            "%s%s %sУдар #%d: %s%.2f%%",
            ChatColor.WHITE,
            playerName,
            ChatColor.GRAY,
            hitNumber,
            color,
            probability * 100
        );

        // Отправляем всем кто отслеживает этого игрока
        for (Player tracker : Bukkit.getOnlinePlayers()) {
            UUID targetUUID = trackingPlayers.get(tracker.getUniqueId());
            if (targetUUID != null) {
                Player target = Bukkit.getPlayer(targetUUID);
                if (target != null && target.getName().equals(playerName)) {
                    tracker.sendMessage(message);
                }
            }
        }
    }

    private void handleDataStatus(Player player) {
        player.sendMessage(getMessageNoPrefix("data-status-header"));
        
        int count = 0;
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (collector.hasSession(p.getUniqueId())) {
                count++;
            }
        }
        
        if (count > 0) {
            player.sendMessage(getMessage("active-sessions").replace("{COUNT}", String.valueOf(count)));
        } else {
            player.sendMessage(getMessage("no-active-sessions"));
            player.sendMessage(getMessage("start-hint"));
        }
    }

    private void handleReload(Player player) {
        plugin.reloadConfig();
        loadMessages();
        player.sendMessage(getMessage("config-reloaded"));
    }

    private void handleSuspects(Player player) {
        if (suspectsGUI != null) {
            suspectsGUI.openGUI(player);
        } else {
            player.sendMessage(ChatColor.RED + "GUI не инициализирован");
        }
    }

    private void handlePunish(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage(ChatColor.RED + "Использование: /smartai punish <player>");
            return;
        }

        Player target = Bukkit.getPlayer(args[1]);
        if (target == null) {
            player.sendMessage(getMessage("player-not-found").replace("{PLAYER}", args[1]));
            return;
        }

        // TODO: Выполнить максимальное наказание из конфига
        player.sendMessage(ChatColor.YELLOW + "Система наказаний в разработке");
    }

    private void handleExitSpectator(Player player) {
        if (suspectsGUI != null) {
            suspectsGUI.getSpectatorManager().exitSpectator(player);
        } else {
            player.sendMessage(ChatColor.RED + "SpectatorManager не инициализирован");
        }
    }

    private void sendHelp(Player player) {
        player.sendMessage(getMessageNoPrefix("usage-header"));
        player.sendMessage(getMessageNoPrefix("usage-start"));
        player.sendMessage(getMessageNoPrefix("usage-stop"));
        player.sendMessage(getMessageNoPrefix("usage-alerts"));
        player.sendMessage(getMessageNoPrefix("usage-prob"));
        player.sendMessage(getMessageNoPrefix("usage-datastatus"));
        player.sendMessage(getMessageNoPrefix("usage-reload"));
        player.sendMessage(getMessageNoPrefix("usage-suspects"));
        player.sendMessage(getMessageNoPrefix("usage-punish"));
    }

    public boolean hasAlertsEnabled(UUID playerId) {
        return alertsEnabled.getOrDefault(playerId, true);
    }

    public Map<UUID, UUID> getTrackingPlayers() {
        return trackingPlayers;
    }
}
