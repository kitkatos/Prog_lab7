package com.example.commands;

import com.example.common.command.Command;
import com.example.common.network.ApplicationStatus;
import com.example.common.network.NetworkObject;
import com.example.common.network.PasswordHasher;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class Show extends Command {

    public Show() {
        super("show", "Выводит список фильмов из базы данных");
    }

    @Override
    public NetworkObject execute(NetworkObject request) {
        String[] args = request.args().split(" ");
        if (!args.equals("")) {
            return new NetworkObject(request.id(), ApplicationStatus.ERROR, null, null, null, "Неверное количество аргументов. Требуется: логин и пароль.", null, null);
        }

        log.info("Клиент: Создание запроса на получение содержимого коллекции");
        return new NetworkObject(request.id(), ApplicationStatus.RUNNING, request.userLogin(), request.userPassword(), getName(), null, null, null);
    }
}
