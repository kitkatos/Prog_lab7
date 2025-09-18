package com.example.network;

import com.example.network.serializer.NetworkSerializer;
import lombok.extern.log4j.Log4j2;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;

/**
 * Главный класс для обмена данным.
 * Хранит в себе адрес подключения и порт.
 * Реализует методы для подключения, отправки и получения информации.
 */
@Log4j2
public class UDPManager {
    private final int port; /* будет 1112 */
    private final String address; /* имя или адрес хоста */
    private final NetworkSerializer serializer;
    ByteBuffer buffer;
    DatagramChannel channel;
    SocketAddress addr;

    /**
     * Конструктор для выбора порта и адреса
     * @param port 1112
     * @param address может быть lokalhost или какоето имя или IP адресс
     * @param serializer новый сериализатор
     */
    public UDPManager(int port, String address, NetworkSerializer serializer){
        this.port = port;
        this.address = address;
        this.serializer = serializer;
        SocketAddress addr = new InetSocketAddress(address, port);
    }

    /**
     * Создает канал данных в неблокирующем режиме и буфер максимального размера
     */
    public void connect() {
        try {
            ByteBuffer buffer = ByteBuffer.allocate(65117);
            DatagramChannel channel = DatagramChannel.open();
            channel.configureBlocking(false);
            log.info("DatagramChannel успешно открыт");
        } catch (IOException e) {
            log.error("Проблемы с открытием DatagramChannel");
        }
    }

    /**
     * Слушает канал и собирает NetworkObject из пакетов.
     * Так как UDP пакеты огранчиены по размеру, сначала получает количесво пакетов,
     * а затем поочередно составные части сообщения.
     * Все они собираются в один массив байт, который целиком десериализуется.
     * @return NetworkObject или Null
     */
    public NetworkObject receiver() {
        try {
            buffer.clear();
            SocketAddress addr1 = channel.receive(buffer);
            log.info("Сервер пытается получить данные");

            if (addr1 != null) {
                log.info("Ненулевые данные прочитаны");
                buffer.flip();

                byte[] packagesCountByte = new byte[buffer.remaining()];
                buffer.get(packagesCountByte);
                log.info("Количество блоко записано в виде байтов в массив");

                int packagesCount = serializer.deserialize(packagesCountByte);
                log.info("Количество блоков успешно десериализировано");

                byte[] deserBytes = new byte[packagesCount * 65117];
                int offset = 0;
                for (int i = 1; i <= packagesCount; i++) {
                    buffer.clear();
                    channel.receive(buffer);
                    log.info("Блок {} получен от клиента и записан в буфер", i);
                    buffer.flip();
                    int len = buffer.remaining();
                    buffer.get(deserBytes, offset, len);
                    offset += len;
                    log.info("Блок {} успешно дописан в массив", i);
                }
                log.info("Поблочное получение запроса завершено");
                NetworkObject ans = serializer.deserialize(deserBytes);
                log.info("NetworkObject получен");
                return ans;
            }
            log.info("Получены нулевые данные");
            return null;
        } catch (IOException e) {
            log.error("Возникли проблемы при получении количества пакетов или считывания одного из пакетов сообщения");
            return null;
        }
    }

    /**
     * Отправляет пакеты.
     * Так как UDP пакеты огранчиены по размеру, сначала отправляет количесво пакетов,
     * а затем поочередно составные части сообщения.
     * Подразумевается, что проблем с сериализацией и отправкой не будет.
     * @param data NetworkObject
     */
    public void send(NetworkObject data) {
        buffer.clear();
        try{
            byte[] bytes = serializer.serialize(data);
            log.info("Ответ сереализован");
            int count = (int) Math.ceil((double) bytes.length / 65117);
            buffer.put(serializer.serialize(count));
            log.info("Количество блоков записано в буфер");
            channel.send(buffer, addr);
            log.info("Данные о количестве блоков отправлены на клиента");

            for (int i = 1; i <= count; i++) {
                buffer = ByteBuffer.wrap(bytes, 65117 * i, Math.min(65117, bytes.length - 65117 * i));
                log.info("Блок {} записан в буфер", i+1);
                channel.send(buffer, addr);
                log.info("Блок {} отправлен серверу", i+1);
            }
            log.info("Поблочная отправка запроса завершена");
        } catch (Exception e) {
            log.error("ошибка в упаковке или отправке данных", e);
        }

    }
}
