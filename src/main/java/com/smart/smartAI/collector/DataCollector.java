package com.smart.smartAI.collector;

import com.smart.smartAI.data.TickData;
import com.smart.smartAI.processor.AimProcessor;
import org.bukkit.entity.Player;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Сборщик данных для обучения AI
 */
public class DataCollector {
    private final File dataFolder;
    private final Map<UUID, SessionData> sessions = new ConcurrentHashMap<>();
    
    public DataCollector(File dataFolder) {
        this.dataFolder = dataFolder;
        if (!dataFolder.exists()) {
            dataFolder.mkdirs();
        }
    }

    public void startSession(Player player, boolean isCheating) {
        startSession(player, isCheating, null);
    }

    public void startSession(Player player, boolean isCheating, String customName) {
        UUID uuid = player.getUniqueId();
        String sessionName = customName != null ? customName : player.getName();
        sessions.put(uuid, new SessionData(sessionName, isCheating));
    }

    public void stopSession(Player player) throws IOException {
        UUID uuid = player.getUniqueId();
        SessionData session = sessions.remove(uuid);
        if (session != null && !session.ticks.isEmpty()) {
            saveSession(session);
        }
    }

    public boolean hasSession(UUID uuid) {
        return sessions.containsKey(uuid);
    }

    public void recordTick(UUID uuid, float yaw, float pitch) {
        SessionData session = sessions.get(uuid);
        if (session != null) {
            TickData tick = session.processor.process(yaw, pitch);
            session.ticks.add(tick);
        }
    }

    public void onAttack(UUID uuid) {
        SessionData session = sessions.get(uuid);
        if (session != null) {
            session.ticksSinceAttack = 0;
        }
    }

    public void tick(UUID uuid) {
        SessionData session = sessions.get(uuid);
        if (session != null) {
            session.ticksSinceAttack++;
        }
    }

    private void saveSession(SessionData session) throws IOException {
        String timestamp = new SimpleDateFormat("yyyyMMdd-HHmmss").format(new Date());
        String label = session.isCheating ? "CHEAT" : "LEGIT";
        String filename = String.format("%s_%s_%s.csv", label, session.playerName, timestamp);
        
        File file = new File(dataFolder, filename);
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.write(TickData.getHeader());
            writer.newLine();
            for (TickData tick : session.ticks) {
                writer.write(tick.toCsv(session.isCheating));
                writer.newLine();
            }
        }
    }

    public void shutdown() {
        for (Map.Entry<UUID, SessionData> entry : sessions.entrySet()) {
            try {
                if (!entry.getValue().ticks.isEmpty()) {
                    saveSession(entry.getValue());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        sessions.clear();
    }

    private static class SessionData {
        final String playerName;
        final boolean isCheating;
        final AimProcessor processor = new AimProcessor();
        final List<TickData> ticks = new ArrayList<>();
        int ticksSinceAttack = 100;

        SessionData(String playerName, boolean isCheating) {
            this.playerName = playerName;
            this.isCheating = isCheating;
        }
    }
}
