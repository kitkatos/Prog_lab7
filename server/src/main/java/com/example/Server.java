package com.example;

import com.example.DB.*;
import com.example.commands.Add;
import com.example.common.network.*;
import com.example.common.command.*;
import com.example.common.network.serializer.NetworkSerializer;
import lombok.extern.log4j.Log4j2;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.DatagramChannel;
import java.sql.Connection;
import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.TreeSet;
import java.util.concurrent.*;


/**
 * Главный класс сервера
 * Реализует ключевой метод start() и многопоточную обработку данных.
 */
@Log4j2
public class Server {
    private final int port;
    private final ExecutorService readPool = Executors.newFixedThreadPool(1);
    private final ExecutorService processPool = Executors.newCachedThreadPool();
    private final ExecutorService sendPool = Executors.newFixedThreadPool(1);
    private final BlockingQueue<RequestTask> requestQueue = new LinkedBlockingQueue<>();
    private final BlockingQueue<ResponseTask> responseQueue = new LinkedBlockingQueue<>();

    private DBConnector dbConnector;
    private CommandManager commandManager;
    private TreeSetCollectionManager collectionManager;
    private UDPManager udpManager;

    public Server(int port) {
        this.port = port;
        this.dbConnector = new DBConnector();
    }

    /**
     * Центральный класс сервера, акумулирует всю логику в одном месте.
     * Содержит в себе блок для корректного завершения работы при ее прирывании.
     */
    public void start(){
        try {
            Connection connection = dbConnector.connect();
            if (connection == null) {
                log.error("Не удалось подключиться к базе данных. Сервер не может быть запущен.");
                return;
            }
            dbConnector.initializeTable();
            this.collectionManager = new TreeSetCollectionManager(new TreeSet<>(), connection);
            this.commandManager = new CommandManager(new HashMap<>(), new ArrayDeque<>());
            addCommand();

            DatagramChannel datagramChannel = DatagramChannel.open();
            datagramChannel.bind(new InetSocketAddress(port));
            datagramChannel.configureBlocking(false);
            log.info("Сервер запущен на порту {}", port);

            this.udpManager = new UDPManager(datagramChannel, new NetworkSerializer(), new NetworkObjectBuilder());

            readPool.submit(this::readRequests);
            processPool.submit(this::processRequests);
            sendPool.submit(this::sendResponses);

            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                log.info("Остановка сервера...");
                readPool.shutdown();
                processPool.shutdown();
                sendPool.shutdown();
                dbConnector.disconnect();
                try {
                    if (datagramChannel.isOpen()) {
                        datagramChannel.close();
                    }
                } catch (IOException e) {
                    log.error("Ошибка при закрытии канала: {}", e.getMessage());
                }
            }));
        } catch (IOException e) {
            log.error("Ошибка при запуске сервера: {}", e.getMessage());
        }
    }

    /**
     * Поток для чтения запросов. Читает UDP пакеты через UDPManager,
     * собирает их и помещает готовые NetworkObject в очередь.
     */
    private void readRequests() {
        try {
            while (!Thread.currentThread().isInterrupted()) {
                ReceiveObject request = udpManager.receive();
                if (request != null) {
                    requestQueue.put(new RequestTask(request.object(), request.address()));
                }
            }
        } catch (IOException | ClassNotFoundException | InterruptedException e) {
            log.error("Ошибка в потоке чтения запросов: {}", e.getMessage());
        }
    }

    /**
     * Поток для обработки запросов. Обрабатывает NetworkObject и создаёт ответ.
     */
    private void processRequests() {
        try {
            while (!Thread.currentThread().isInterrupted()) {
                RequestTask task = requestQueue.take();
                NetworkObject response = commandManager.executeCommand(task.request());
                responseQueue.put(new ResponseTask(response, task.clientAddress()));
            }
        } catch (InterruptedException e) {
            log.error("Поток обработки запросов был прерван.");
        }
    }

    /**
     * Поток для отправки ответов. Берёт готовый ответ и отправляет его клиенту через UDPManager.
     */
    private void sendResponses() {
        try {
            while (!Thread.currentThread().isInterrupted()) {
                ResponseTask task = responseQueue.take();
                udpManager.send(task.response(), task.clientAddress());
            }
        } catch (InterruptedException | IOException e) {
            log.error("Ошибка в потоке отправки ответов: {}", e.getMessage());
        }
    }

    private void addCommand() {
        commandManager.addCommand(new Add(this.collectionManager));
    }

    private record RequestTask(NetworkObject request, InetSocketAddress clientAddress) {}
    private record ResponseTask(NetworkObject response, InetSocketAddress clientAddress) {}
}