package com.example;

import com.example.commands.*;
import com.example.common.command.Command;
import com.example.common.command.CommandManager;
import com.example.common.network.NetworkObject;
import com.example.common.network.NetworkObjectBuilder;
import com.example.common.network.ReceiveObject;
import com.example.common.network.UDPManager;
import com.example.common.network.serializer.NetworkSerializer;
import lombok.extern.log4j.Log4j2;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.DatagramChannel;
import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

@Log4j2
public class Client {
    // ID для нашего единственного "пользователя" - консоли
    public static final long CONSOLE_USER_ID = 0L;

    private final UDPManager udpManager;
    private final DatagramChannel channel;
    private final UserSessionManager userSessionManager;
    private final ExecutorService threadPool = Executors.newCachedThreadPool();
    private final AtomicBoolean isRunning = new AtomicBoolean(true);

    // Очередь для отправки на сервер
    private final BlockingQueue<NetworkObject> toServerQueue = new LinkedBlockingQueue<>();
    // Карта для маршрутизации ответов от сервера к конкретному пользователю
    // Ключ - ID пользователя, значение - его персональная очередь для ответов
    private final Map<Long, BlockingQueue<NetworkObject>> responseQueues = new ConcurrentHashMap<>();
    // Карта для отслеживания, какой пользователь отправил какой запрос
    // Ключ - ID запроса, значение - ID пользователя
    private final Map<Long, Long> pendingRequests = new ConcurrentHashMap<>();

    public Client(String serverHost, int serverPort) throws IOException {
        // 1. Инициализация сети
        channel = DatagramChannel.open().bind(null);
        channel.configureBlocking(false);
        InetSocketAddress serverAddress = new InetSocketAddress(serverHost, serverPort);
        udpManager = new UDPManager(channel, new NetworkSerializer(), new NetworkObjectBuilder(), serverAddress);

        // 2. Инициализация менеджеров
        CommandManager clientCommandManager = addCommands();
        UserManager userManager = new UserManager();
        userSessionManager = new UserSessionManager(toServerQueue, userManager, clientCommandManager);
    }

    private CommandManager addCommands() {
        Map<String, Command> commands = new HashMap<>();
        CommandManager manager = new CommandManager(commands, new ArrayDeque<>());

        manager.addCommand(new Add());
        manager.addCommand(new Show());
        manager.addCommand(new Authenticate());
        return manager;
    }

    public void run() {
        log.info("Клиент запущен. Адрес сервера: {}", udpManager.getChannel().socket().getRemoteSocketAddress());
        Runtime.getRuntime().addShutdownHook(new Thread(this::shutdown));

        // Запускаем сетевые потоки
        threadPool.submit(this::startNetworkListener);
        threadPool.submit(this::startNetworkSender);

        // Запускаем UI для консоли
        startConsoleUserInterface();
    }

    private void startConsoleUserInterface() {
        // Создаем персональную очередь для ответов консольному пользователю
        BlockingQueue<NetworkObject> consoleResponseQueue = new LinkedBlockingQueue<>();
        responseQueues.put(CONSOLE_USER_ID, consoleResponseQueue);

        // Создаем и запускаем UI консоли в отдельном потоке
        ConsoleUserInterface consoleUI = new ConsoleUserInterface(CONSOLE_USER_ID, userSessionManager, consoleResponseQueue);
        threadPool.submit(consoleUI);
    }

    private void startNetworkSender() {
        while (isRunning.get()) {
            try {
                NetworkObject request = toServerQueue.take();
                // Находим, из какой сессии пришел запрос, чтобы запомнить ID пользователя
                // ВАЖНО: В UserSession необходимо будет указывать userLogin, чтобы здесь его найти.
                // Для простоты, пока будем считать, что все запросы от CONSOLE_USER_ID
                pendingRequests.put(request.id(), CONSOLE_USER_ID);
                udpManager.send(request);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } catch (IOException e) {
                log.error("Ошибка отправки данных: {}", e.getMessage());
            }
        }
    }

    private void startNetworkListener() {
        while (isRunning.get()) {
            try {
                ReceiveObject received = udpManager.receive();
                if (received != null && received.object() != null) {
                    NetworkObject response = received.object();
                    // Ищем, какому пользователю предназначается ответ
                    Long userId = pendingRequests.remove(response.id());
                    if (userId != null) {
                        BlockingQueue<NetworkObject> userQueue = responseQueues.get(userId);
                        if (userQueue != null) {
                            userQueue.put(response); // Кладем ответ в персональную очередь пользователя
                        }
                    }
                }
                Thread.sleep(10);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } catch (Exception e) {
                log.error("Ошибка получения данных: {}", e.getMessage());
            }
        }
    }

    public void shutdown() {
        if (isRunning.getAndSet(false)) {
            log.info("Завершение работы клиента...");
            threadPool.shutdownNow();
            try {
                if (channel.isOpen()) channel.close();
            } catch (IOException e) {
                log.error("Ошибка при закрытии канала: {}", e.getMessage());
            }
            log.info("Клиент остановлен.");
        }
    }

    public static void main(String[] args) {
        // ... (main метод остается без изменений)
        if (args.length != 2) {
            System.out.println("Использование: java -jar client.jar <host> <port>");
            return;
        }
        try {
            Client client = new Client(args[0], Integer.parseInt(args[1]));
            client.run();
        } catch (IOException | NumberFormatException e) {
            log.error("Ошибка при запуске клиента: {}", e.getMessage());
        }
    }
}