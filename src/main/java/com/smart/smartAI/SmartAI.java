package com.smart.smartAI;

import com.github.retrooper.packetevents.PacketEvents;
import io.github.retrooper.packetevents.factory.spigot.SpigotPacketEventsBuilder;
import com.smart.smartAI.ai.AIChecker;
import com.smart.smartAI.ai.AIClient;
import com.smart.smartAI.alert.AlertManager;
import com.smart.smartAI.collector.DataCollector;
import com.smart.smartAI.command.DataCommand;
import com.smart.smartAI.hologram.HologramManager;
import com.smart.smartAI.listener.CombatListener;
import com.smart.smartAI.listener.PacketListener;
import com.smart.smartAI.violation.ViolationTracker;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public final class SmartAI extends JavaPlugin implements Listener {
    private DataCollector dataCollector;
    private AIClient aiClient;
    private AIChecker aiChecker;
    private AlertManager alertManager;
    private HologramManager hologramManager;
    private ViolationTracker violationTracker;

    @Override
    public void onLoad() {
        PacketEvents.setAPI(SpigotPacketEventsBuilder.build(this));
        PacketEvents.getAPI().load();
    }

    @Override
    public void onEnable() {
        // Сохранение конфига по умолчанию
        saveDefaultConfig();
        FileConfiguration config = getConfig();

        // Инициализация PacketEvents
        PacketEvents.getAPI().init();

        // Создание папки для данных
        File dataFolder = new File(getDataFolder(), "datasets");
        dataCollector = new DataCollector(dataFolder);

        // Инициализация голограмм
        boolean hologramsEnabled = config.getBoolean("holograms.enabled", true);
        hologramManager = new HologramManager(hologramsEnabled);
        if (hologramsEnabled) {
            hologramManager.start(this);
            getLogger().info("[Голограммы] Включены!");
        }

        // Инициализация AI
        boolean aiEnabled = config.getBoolean("detection.enabled", false);
        if (aiEnabled) {
            String apiUrl = config.getString("detection.endpoint", "http://localhost:5000");
            aiClient = new AIClient(apiUrl, getLogger());
            
            // Проверка подключения
            if (aiClient.checkHealth()) {
                getLogger().info("[AI] Подключено к API: " + apiUrl);
                
                int sequenceLength = config.getInt("detection.sample-size", 40);
                aiChecker = new AIChecker(aiClient, sequenceLength, hologramManager);
                
                double alertThreshold = config.getDouble("alerts.threshold", 0.75);
                alertManager = new AlertManager(alertThreshold);
                
                // Инициализация ViolationTracker
                double vlThreshold = config.getDouble("alerts.threshold", 0.75);
                double multiplier = config.getDouble("violation.multiplier", 100.0);
                double decay = config.getDouble("violation.decay", 0.35);
                violationTracker = new ViolationTracker(vlThreshold, multiplier, decay);
                
                getLogger().info("[AI] AI проверка включена!");
                getLogger().info("[AI] Sample size: " + sequenceLength + " ticks");
            } else {
                getLogger().warning("[AI] Не удалось подключиться к API: " + apiUrl);
                getLogger().warning("[AI] AI проверка отключена. Запустите: python api_server.py");
                aiClient.setEnabled(false);
            }
        } else {
            getLogger().info("[AI] AI проверка отключена в конфиге");
        }

        // Регистрация слушателей
        PacketListener packetListener = new PacketListener(dataCollector, aiChecker);
        PacketEvents.getAPI().getEventManager().registerListener(packetListener);
        
        int sampleInterval = config.getInt("detection.sample-interval", 10);
        CombatListener combatListener = new CombatListener(dataCollector, aiChecker, alertManager, sampleInterval, violationTracker);
        getServer().getPluginManager().registerEvents(combatListener, this);
        getServer().getPluginManager().registerEvents(this, this);

        // Регистрация команд
        DataCommand dataCommand = new DataCommand(this, dataCollector);
        getCommand("smartai").setExecutor(dataCommand);
        
        // Инициализация GUI
        if (hologramManager != null) {
            com.smart.smartAI.spectator.SpectatorManager spectatorManager = new com.smart.smartAI.spectator.SpectatorManager();
            com.smart.smartAI.gui.SuspectsGUI suspectsGUI = new com.smart.smartAI.gui.SuspectsGUI(hologramManager, spectatorManager);
            dataCommand.setSuspectsGUI(suspectsGUI);
            
            // Регистрация listener для GUI
            com.smart.smartAI.listener.GUIListener guiListener = new com.smart.smartAI.listener.GUIListener(suspectsGUI);
            getServer().getPluginManager().registerEvents(guiListener, this);
        }
        
        // Связываем AlertManager с DataCommand для управления алертами
        if (alertManager != null) {
            alertManager.setDataCommand(dataCommand);
        }
        
        // Связываем CombatListener с DataCommand для вывода в чат
        combatListener.setDataCommand(dataCommand);

        getLogger().info("SmartAI включен! Используйте /smartai для сбора данных.");
        if (aiEnabled && aiClient != null && aiClient.isEnabled()) {
            getLogger().info("AI детекция активна! Админы получат алерты при подозрительной активности.");
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        if (hologramManager != null) {
            hologramManager.handlePlayerQuit(event.getPlayer());
        }
    }

    @Override
    public void onDisable() {
        if (hologramManager != null) {
            hologramManager.stop();
        }
        if (dataCollector != null) {
            dataCollector.shutdown();
        }
        PacketEvents.getAPI().terminate();
        getLogger().info("SmartAI выключен!");
    }

    public AIChecker getAIChecker() {
        return aiChecker;
    }

    public AlertManager getAlertManager() {
        return alertManager;
    }

    public HologramManager getHologramManager() {
        return hologramManager;
    }
}
