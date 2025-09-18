package com.example.commands;

import com.example.common.command.Command;
import com.example.common.model.Movie;
import com.example.common.network.ApplicationStatus;
import com.example.common.network.NetworkObject;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class Add extends Command {

    public Add() {
        super("add", "Добавить новый фильм в коллекцию.");
    }

    @Override
    public NetworkObject execute(NetworkObject request) {
        if (request.args() == null || request.args().isEmpty()) {
            return new NetworkObject(request.id(), ApplicationStatus.ERROR, null, null, null, null, "Отсутствуют аргументы. Укажите строковое представление фильма.", null);
        }

        try {
            // Преобразуем строковое представление фильма в объект Movie.
            Movie movie = Movie.parseFromString(request.args());
            log.info("Объект фильма успешно получен из строки");

            // Проводим валидацию фильма с помощью метода Movie.validate().
            if (!movie.validate()) {
                return new NetworkObject(request.id(), ApplicationStatus.ERROR, null, null, null, null, "Введенные данные некорректны. Пожалуйста, проверьте поля.", null);
            }

            // Помещаем созданный и валидированный объект Movie в NetworkObject.
            // Статус SEND указывает, что этот NetworkObject нужно отправить на сервер.
            return new NetworkObject(request.id(), ApplicationStatus.SEND, request.userLogin(), request.userPassword(), "add", null, null, movie);

        } catch (IllegalArgumentException e) {
            log.error("Ошибка парсинга фильма: {}", e.getMessage());
            return new NetworkObject(request.id(), ApplicationStatus.ERROR, null, null, null, null, "Ошибка при парсинге фильма: " + e.getMessage(), null);
        }
    }
}