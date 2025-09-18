package com.example.common.network;

import com.example.common.network.serializer.NetworkSerializer;
import lombok.extern.log4j.Log4j2;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListMap;

/**
 * Класс для многопоточной сборки NetworkObject из NetworkPacket.
 */
@Log4j2
public class NetworkObjectBuilder {
    private final Map<Long, Map<Integer, byte[]>> packets = new ConcurrentHashMap<>();
    private final NetworkSerializer serializer = new NetworkSerializer();

    /**
     * Собирает пакеты в единое сообщение.
     * Добавляет в общую мапу пакетов все пакеты.
     * Когда все составные части какого-то сообщения на месте, возвращает этот NetworkObject.
     * В противном случае возвращает null.
     * @param packet составная част пакета
     * @return готовый NetworkObject или null
     */
    public NetworkObject build(NetworkPacket packet) throws IOException, ClassNotFoundException {
        long requestId = packet.requestId();
        int packetIndex = packet.packetIndex();
        int totalPackets = packet.totalPackets();

        Map<Integer, byte[]> parts = packets.computeIfAbsent(requestId, k -> new ConcurrentSkipListMap<>());
        parts.put(packetIndex, packet.dataBytes());
        log.info("Пакет {}:{} успешно добавлен в мапу", packet.requestId(), packet.packetIndex());

        if (parts.size() == totalPackets) {
            int totalLength = parts.values().stream().mapToInt(b -> b.length).sum();
            byte[] data = new byte[totalLength];
            int current = 0;
            for (byte[] partData : parts.values()) {
                System.arraycopy(partData, 0, data, current, partData.length);
                current += partData.length;
            }
            log.info("Сообщение {} полностью получено в виде байт", packet.requestId());

            packets.remove(requestId);
            log.info("Пакеты сообщения {} удалены из мапы", packet.requestId());
            return serializer.deserialize(data);
        }
        log.info("Никакое сообещение не собрано полностью, возвращен null");
        return null;
    }
}