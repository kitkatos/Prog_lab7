package com.example;

import com.example.common.command.CommandManager;
import com.example.common.network.NetworkObject;
import lombok.extern.log4j.Log4j2;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

@Log4j2
public class UserSessionManager {

    private final Map<Long, BlockingQueue<String>> userInputQueues = new ConcurrentHashMap<>();
    private final BlockingQueue<NetworkObject> outputQueue;
    private final Map<Long, Thread> activeUserSessions = new ConcurrentHashMap<>();
    private final UserManager userManager;
    private final CommandManager commandManager;

    public UserSessionManager(BlockingQueue<NetworkObject> outputQueue, UserManager userManager, CommandManager commandManager) {
        this.outputQueue = outputQueue;
        this.userManager = userManager;
        this.commandManager = commandManager;
    }

    /**
     * Возвращает очередь ввода для указанного пользователя.
     * Создает новую сессию, если ее еще нет.
     * @param userId ID пользователя.
     * @return Очередь ввода для пользователя.
     */
    public BlockingQueue<String> getOrCreateInputQueue(long userId) {
        return userInputQueues.computeIfAbsent(userId, k -> {
            log.info("Создается новая очередь ввода для пользователя с ID: {}", userId);
            LinkedBlockingQueue<String> queue = new LinkedBlockingQueue<>();
            // Создаем и запускаем новый поток для сессии пользователя
            UserSession userSession = new UserSession(userId, queue, outputQueue, userManager, commandManager );
            Thread sessionThread = new Thread(userSession, "UserSession-" + userId);
            activeUserSessions.put(userId, sessionThread);
            sessionThread.start();
            log.info("Новая сессия и поток запущены для пользователя с ID: {}", userId);
            return queue;
        });
    }

    /**
     * Проверяет, активна ли сессия для данного пользователя.
     * @param userId ID пользователя.
     * @return true, если сессия активна, иначе false.
     */
    public boolean isSessionActive(long userId) {
        return activeUserSessions.containsKey(userId);
    }

    /**
     * Завершает сессию для указанного пользователя.
     * Этот метод можно вызвать, когда сессия завершилась (например, пользователь ввел 'exit').
     * @param userId ID пользователя.
     */
    public void terminateSession(long userId) {
        Thread sessionThread = activeUserSessions.get(userId);
        if (sessionThread != null) {
            sessionThread.interrupt(); // Отправляем сигнал прерывания потоку
            activeUserSessions.remove(userId);
            userInputQueues.remove(userId);
            log.info("Сессия и очередь для пользователя {} завершены.", userId);
        }
    }
}