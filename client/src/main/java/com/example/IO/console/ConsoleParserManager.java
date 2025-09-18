package com.example.IO.console;

import com.example.IO.interfaces.ParserManager;
import com.example.common.network.ApplicationStatus;
import com.example.common.network.NetworkObject;
import lombok.extern.log4j.Log4j2;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Класс для парсинга строки из консоли.
 * Берёт первое слово как команду, а всю оставшуюся строку — как аргумент.
 */
@Log4j2
public class ConsoleParserManager implements ParserManager {

    // Статический счётчик для генерации уникальных ID запросов
    private static final AtomicLong requestIdCounter = new AtomicLong(0);

    /**
     * Преобразует строку в NetworkObject.
     * @param input введенная строка
     * @return NetworkObject
     */
    public NetworkObject getNetworkObject(String input) {
        log.trace("Начало метода getNetworkObject()");

        Long id = requestIdCounter.incrementAndGet();

        // Проверяем, что входная строка не пустая
        String trimmedInput = input.trim();
        if (trimmedInput.isEmpty()) {
            log.debug("Пустая строка, возвращён пустой NetworkObject.");
            return new NetworkObject(id, ApplicationStatus.RUNNING, null, null, null, null, null, null);
        }

        // Разделяем строку на первое слово и остаток
        String[] parts = trimmedInput.split("\\s+", 2);
        String commandName = parts[0];

        // Используем substring для получения всей оставшейся части строки.
        // Находим индекс, с которого начинается остаток, и берём подстроку до конца.
        String args = "";
        int firstSpaceIndex = trimmedInput.indexOf(' ');
        if (firstSpaceIndex != -1) {
            args = trimmedInput.substring(firstSpaceIndex + 1);
        }

        // Создаём NetworkObject с логином и паролем null, так как эту логику
        // будет обрабатывать UserSession
        NetworkObject result = new NetworkObject(
                id,
                ApplicationStatus.RUNNING,
                null, // userLogin будет добавлен в UserSession
                null, // userPassword будет добавлен в UserSession
                commandName,
                args,
                null,
                null
        );

        log.debug("Парсинг завершён. Команда: '{}', Аргументы: '{}'", commandName, args);
        log.trace("Завершение метода getNetworkObject()");
        return result;
    }
}