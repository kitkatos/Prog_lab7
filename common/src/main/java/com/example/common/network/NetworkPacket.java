package com.example.common.network;

import java.io.Serializable;

/**
 * Класс для передачи NetworkObject по составным пакетам
 * @param requestId номер сообщения
 * @param totalPackets число составных частей
 * @param packetIndex номер составной части
 * @param dataBytes информация в байтах
 */
public record NetworkPacket(long requestId, int totalPackets, int packetIndex, byte[] dataBytes) implements Serializable {
}
