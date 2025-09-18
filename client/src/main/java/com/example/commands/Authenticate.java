package com.example.commands;

import com.example.common.command.Command;
import com.example.common.network.ApplicationStatus;
import com.example.common.network.NetworkObject;
import com.example.common.network.PasswordHasher;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class Authenticate extends Command {

    public Authenticate() {
        super("authenticate", "Авторизация пользователя. Пример: authenticate <логин> <пароль>");
    }

    @Override
    public NetworkObject execute(NetworkObject request) {
        String[] args = request.args().split(" ");
        if (args.length != 2) {
            return new NetworkObject(request.id(), ApplicationStatus.ERROR, null, null, null, "Неверное количество аргументов. Требуется: логин и пароль.", null, null);
        }

        String login = args[0];
        String password = args[1];

        // Хэшируем пароль перед отправкой на сервер.
        String hashedPassword = PasswordHasher.sha1(password);

        log.info("Клиент: Создание запроса на авторизацию для пользователя {}. Пароль хэширован.", login);

        // Отправляем на сервер уже хэшированный пароль.
        return new NetworkObject(request.id(), ApplicationStatus.RUNNING, login, hashedPassword, getName(), null, null, null);
    }
}