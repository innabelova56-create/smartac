package com.smart.smartAI.listener;

import com.github.retrooper.packetevents.event.PacketListenerAbstract;
import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientPlayerFlying;
import com.smart.smartAI.ai.AIChecker;
import com.smart.smartAI.collector.DataCollector;
import org.bukkit.entity.Player;

/**
 * Слушатель пакетов ротации
 */
public class PacketListener extends PacketListenerAbstract {
    private final DataCollector collector;
    private final AIChecker aiChecker;

    public PacketListener(DataCollector collector, AIChecker aiChecker) {
        this.collector = collector;
        this.aiChecker = aiChecker;
    }

    @Override
    public void onPacketReceive(PacketReceiveEvent event) {
        if (event.getPacketType() == PacketType.Play.Client.PLAYER_ROTATION ||
            event.getPacketType() == PacketType.Play.Client.PLAYER_POSITION_AND_ROTATION) {
            
            Player player = event.getPlayer();
            if (player == null) {
                return;
            }

            WrapperPlayClientPlayerFlying packet = new WrapperPlayClientPlayerFlying(event);
            if (packet.hasRotationChanged()) {
                float yaw = packet.getLocation().getYaw();
                float pitch = packet.getLocation().getPitch();
                
                // Сбор данных
                if (collector.hasSession(player.getUniqueId())) {
                    collector.recordTick(player.getUniqueId(), yaw, pitch);
                }
                
                // AI проверка
                if (aiChecker != null) {
                    aiChecker.processTick(player, yaw, pitch);
                }
            }
        }
    }
}
