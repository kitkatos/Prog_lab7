package com.example;

import com.example.common.network.ApplicationStatus;
import com.example.common.network.NetworkObject;
import com.example.common.command.CommandManager;
import lombok.extern.log4j.Log4j2;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

@Log4j2
public class UserSession implements Runnable {

    private final long userId;
    private final BlockingQueue<String> inputQueue;
    private final BlockingQueue<NetworkObject> outputQueue;
    private final UserManager userManager;
    private final CommandManager clientCommandManager; // Менеджер команд на клиенте

    public UserSession(long userId, BlockingQueue<String> inputQueue,
                       BlockingQueue<NetworkObject> outputQueue, UserManager userManager,
                       CommandManager clientCommandManager) {
        this.userId = userId;
        this.inputQueue = inputQueue;
        this.outputQueue = outputQueue;
        this.userManager = userManager;
        this.clientCommandManager = clientCommandManager;
    }

    @Override
    public void run() {
        log.info("Сессия для пользователя с ID: {} запущена.", userId);
        try {
            while (!Thread.currentThread().isInterrupted()) {
                // Блокирующее ожидание сообщения от пользователя.
                // Добавляем таймаут, чтобы была возможность завершить поток по interrupt().
                String message = inputQueue.poll(500, TimeUnit.MILLISECONDS);

                if (message != null) {
                    log.info("Получено сообщение от пользователя {}: {}", userId, message);
                    handleMessage(message);
                }
            }
        } catch (InterruptedException e) {
            log.info("Сессия для пользователя с ID: {} была прервана.", userId);
            Thread.currentThread().interrupt(); // Восстанавливаем флаг прерывания.
        } finally {
            log.info("Сессия для пользователя с ID: {} завершена.", userId);
            // Здесь можно добавить логику для уведомления UserSessionManager о завершении.
            // Например, вызвать метод-коллбэк.
        }
    }

    private void handleMessage(String message) {
        // Логика обработки команд
        String[] parts = message.trim().split("\\s+", 2);
        String commandName = parts[0].toLowerCase();
        String args = parts.length > 1 ? parts[1] : "";

        // Проверка авторизации. Логика аутентификации будет реализована в AuthenticateCommand.
        UserData userData = userManager.getUserData(userId);
        String userLogin = (userData != null) ? userData.login() : null;
        String userPassword = (userData != null) ? userData.password() : null;

        // Создаем NetworkObject для отправки на сервер
        NetworkObject request = new NetworkObject(
                System.nanoTime(),
                ApplicationStatus.RUNNING,
                userLogin,
                userPassword,
                commandName,
                args,
                null,
                null
        );

        // Исполнение команды через CommandManager
        NetworkObject response = clientCommandManager.executeCommand(request);

        // Отправка ответа в общую исходящую очередь.
        try {
            outputQueue.put(response);
        } catch (InterruptedException e) {
            log.error("Ошибка при добавлении ответа в исходящую очередь: {}", e.getMessage());
            Thread.currentThread().interrupt();
        }
    }
}
