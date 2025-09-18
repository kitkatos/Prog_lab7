package com.example;


import com.example.IO.console.ConsoleInputManager;
import com.example.IO.console.ConsoleOutputManager;
import com.example.IO.interfaces.InputManager;
import com.example.IO.interfaces.OutputManager;
import com.example.UserSessionManager;
import com.example.common.network.ApplicationStatus;
import com.example.common.network.NetworkObject;
import lombok.extern.log4j.Log4j2;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

@Log4j2
public class ConsoleUserInterface implements Runnable {
    private final long userId;
    private final UserSessionManager userSessionManager;
    private final BlockingQueue<NetworkObject> responseQueue;
    private final InputManager inputManager;
    private final OutputManager outputManager;
    private final AtomicBoolean isActive = new AtomicBoolean(true);
    private final ExecutorService inputReaderExecutor = Executors.newSingleThreadExecutor();


    public ConsoleUserInterface(long userId, UserSessionManager userSessionManager, BlockingQueue<NetworkObject> responseQueue) {
        this.userId = userId;
        this.userSessionManager = userSessionManager;
        this.responseQueue = responseQueue;
        // Используем твои интерфейсы
        this.inputManager = new ConsoleInputManager();
        this.outputManager = new ConsoleOutputManager();
    }

    @Override
    public void run() {
        // Запускаем отдельный поток для чтения ввода, чтобы не блокировать основной цикл
        inputReaderExecutor.submit(this::readInputLoop);

        // Основной цикл для обработки и вывода ответов
        outputManager.printLine("Консольный интерфейс готов. Введите команду.");

        while (isActive.get()) {
            try {
                NetworkObject response = responseQueue.take(); // Ждем ответ от Client
                handleResponse(response);
            } catch (InterruptedException e) {
                log.info("Поток ConsoleUI был прерван.");
                isActive.set(false);
                Thread.currentThread().interrupt();
            }
        }
    }

    private void readInputLoop() {
        BlockingQueue<String> userCommandQueue = userSessionManager.getOrCreateInputQueue(userId);
        while (isActive.get()) {
            outputManager.printString("> ");
            String line = inputManager.readLine();
            if (line == null) { // Ctrl+D
                log.info("Ввод закрыт, завершение работы ConsoleUI.");
                isActive.set(false);
                break;
            }
            if (!line.isBlank()) {
                try {
                    userCommandQueue.put(line);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }
    }

    private void handleResponse(NetworkObject response) {
        if (response.data() != null && !response.data().isEmpty()) {
            if (response.status() == ApplicationStatus.ERROR) {
                outputManager.printError(response.data());
            } else {
                outputManager.printLine(response.data());
            }
        }

        if (response.status() == ApplicationStatus.EXIT) {
            log.info("Получен сигнал EXIT, завершаю работу ConsoleUI.");
            isActive.set(false);
            inputReaderExecutor.shutdownNow(); // Прерываем поток чтения
        }
    }
}
