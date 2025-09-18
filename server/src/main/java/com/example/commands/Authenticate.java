package com.example.commands;

import com.example.DB.TreeSetCollectionManager;
import com.example.common.command.Command;
import com.example.common.network.ApplicationStatus;
import com.example.common.network.NetworkObject;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class Authenticate extends Command {

    private final TreeSetCollectionManager collectionManager;

    public Authenticate(TreeSetCollectionManager collectionManager) {
        super("authenticate", "Аутентифицировать пользователя.");
        this.collectionManager = collectionManager;
    }

    @Override
    public NetworkObject execute(NetworkObject request) {
        String login = request.userLogin();
        String password = request.userPassword();

        if (login == null || login.isEmpty() || password == null || password.isEmpty()) {
            return new NetworkObject(request.id(), ApplicationStatus.ERROR, null, null, null, null, "Необходимо указать логин и пароль.", null);
        }

        log.info("Пользователь {} пытается аутентифицироваться.", login);

        boolean success = collectionManager.authenticate(login, password);

        if (success) {
            log.info("Пользователь {} успешно аутентифицирован или зарегистрирован.", login);
            return new NetworkObject(request.id(), ApplicationStatus.RUNNING, login, password, null, null, "Вы успешно аутентифицированы.", null);
        } else {
            log.warn("Неудачная попытка аутентификации для пользователя {}. Неверный логин или пароль.", login);
            return new NetworkObject(request.id(), ApplicationStatus.ERROR, null, null, null, null, "Неверный логин или пароль.", null);
        }
    }
}
