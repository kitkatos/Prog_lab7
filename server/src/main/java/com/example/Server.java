package com.example;

import com.example.DB.*;
import com.example.command.*;
import com.example.command.commands.*;
import com.example.model.*;
import com.example.network.*;
import com.example.network.serializer.*;
import lombok.extern.log4j.Log4j2;

import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.TreeSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Гланый класс сверера, аккумулирующий всю логику в одном месте.
 * Инициализирует:
 * <ol>
 *     <li> Менеджер команд</li>
 *     <li> Сериализатор для объектов сети</li>
 *     <li> Менеджер сетевого обмена по протоколу UDP</li>
 *     <li> Класс для подключеняи к бдшке</li>
 *     <li> Менеджер базы данных и коллекции</li>
 * </ol>
 */
@Log4j2
public class Server {
    CommandManager cmManager = new CommandManager(new HashMap<String, Command>(), new ArrayDeque<String>());
    NetworkSerializer serializer = new NetworkSerializer();
    UDPManager server = new UDPManager(1112, "lokalhost", serializer);
    DBConnector connector = new DBConnector();
    TreeSetCollectionManager clManager = new TreeSetCollectionManager(new TreeSet<Movie>(), connector.connect());

    private final ExecutorService requestPool = Executors.newCachedThreadPool();
    private final ExecutorService commandPool = Executors.newFixedThreadPool(4);
    private final ExecutorService responsePool = Executors.newFixedThreadPool(2);

    /**
     * Главный метод сервера.
     * Добавляет команды в мапу, подключается к серверу, слушает запрос, выполняет команду, отправляет ответ.
     * Под каждый из последних трех этапов есть свой пул потоков.
     */
    public void start() {
        try {
            addCommands();
            server.connect();

            while (true) {
                requestPool.submit(() -> {
                    try {
                        NetworkObject request = server.receiver();

                        commandPool.submit(() -> {
                            try {
                                NetworkObject response = cmManager.executeCommand(request);

                                responsePool.submit(() -> {
                                    try {
                                        server.send(response);
                                    } catch (Exception e) {
                                        log.error("Ошибка отправки ответа: {}", e.getMessage());
                                    }
                                });

                            } catch (Exception e) {
                                log.error("Ошибка выполнения команды: {}", e.getMessage());
                            }
                        });

                    } catch (Exception e) {
                        log.error("Ошибка при получении запроса: {}", e.getMessage());
                    }
                });
            }

        } catch (Exception e) {
            log.error("Ошибка запуска сервера: {}", e.getMessage());
        }
    }

    /**
     * Добавляет команды в пул команд, чтобы их можно было вызывать по имени
     */
    private void addCommands() {
        cmManager.addCommand(new Add(clManager));
    }


}
