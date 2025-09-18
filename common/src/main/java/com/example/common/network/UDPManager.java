package com.example.common.network;

import com.example.common.network.serializer.NetworkSerializer;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;

/**
 * Класс-обертка для управления операциями отправки и получения данных.
 */
@Log4j2
public class UDPManager {

    private final NetworkSerializer serializer;
    private final NetworkObjectBuilder builder;
    @Getter
    private final DatagramChannel channel;
    private final InetSocketAddress serverAddress;
    private final ByteBuffer buffer = ByteBuffer.allocate(65507); // Максимальный размер UDP-пакета

    /**
     * Конструктор для использования на сервере.
     * @param channel DatagramChannel, уже связанный с портом
     * @param serializer экземпляр NetworkSerializer
     * @param builder экземпляр NetworkObjectBuilder
     */
    public UDPManager(DatagramChannel channel, NetworkSerializer serializer, NetworkObjectBuilder builder) {
        this.channel = channel;
        this.serializer = serializer;
        this.builder = builder;
        this.serverAddress = null; // Не используется на сервере
    }

    /**
     * Конструктор для использования на клиенте.
     * @param channel DatagramChannel, уже связанный с портом
     * @param serializer экземпляр NetworkSerializer
     * @param builder экземпляр NetworkObjectBuilder
     * @param serverAddress адрес и порт сервера
     */
    public UDPManager(DatagramChannel channel, NetworkSerializer serializer, NetworkObjectBuilder builder, InetSocketAddress serverAddress) {
        this.channel = channel;
        this.serializer = serializer;
        this.builder = builder;
        this.serverAddress = serverAddress;
    }

    /**
     * Отправляет NetworkObject, разбивая его на пакеты.
     * Использует адрес для отправки.
     * @param data NetworkObject для отправки
     * @param targetAddress адрес получателя
     */
    public void send(NetworkObject data, InetSocketAddress targetAddress) throws IOException {
        try {
            byte[] objectBytes = serializer.serialize(data);
            long requestId = data.id();
            int packetSize = 65507 - 20; // Макс. размер пакета минус примерный размер заголовка
            int totalPackets = (int) Math.ceil((double) objectBytes.length / packetSize);

            for (int i = 0; i < totalPackets; i++) {
                int offset = i * packetSize;
                int length = Math.min(packetSize, objectBytes.length - offset);
                byte[] partData = new byte[length];
                System.arraycopy(objectBytes, offset, partData, 0, length);

                NetworkPacket packet = new NetworkPacket(requestId, totalPackets, i, partData);
                byte[] packetBytes = serializer.serialize(packet);

                ByteBuffer packetBuffer = ByteBuffer.wrap(packetBytes);
                channel.send(packetBuffer, targetAddress);
            }
            log.info("Сообщение {} успешно отправлено, разбито на {} пакетов.", requestId, totalPackets);
        } catch (IOException e) {
            log.error("Ошибка при отправке данных: {}", e.getMessage());
            throw e;
        }
    }

    /**
     * Отправляет NetworkObject на адрес, указанный при создании клиента.
     * Этот метод предназначен только для использования на клиенте.
     * @param data NetworkObject для отправки
     */
    public void send(NetworkObject data) throws IOException {
        if (serverAddress == null) {
            throw new IllegalStateException("Для отправки на сервер используйте метод send(data, serverAddress)");
        }
        send(data, serverAddress);
    }

    /**
     * Принимает пакеты и собирает из них NetworkObject.
     * @return готовый NetworkObject+InetSocketAddress или null
     */
    public ReceiveObject receive() throws IOException, ClassNotFoundException {
        buffer.clear();
        InetSocketAddress clientAddress = (InetSocketAddress) channel.receive(buffer);

        if (clientAddress != null) {
            log.info("Пакет получен");
            buffer.flip();
            byte[] data = new byte[buffer.remaining()];
            buffer.get(data);

            try {
                NetworkPacket packet = serializer.deserialize(data);
                log.info("Пакет передан сборщику объектов");
                return new ReceiveObject(builder.build(packet), clientAddress);
            } catch (IOException | ClassNotFoundException e) {
                log.error("Ошибка десериализации пакета: {}", e.getMessage());
                throw e;
            }
        }
        return null;
    }
}