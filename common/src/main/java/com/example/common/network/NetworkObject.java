package com.example.common.network;

import com.example.common.model.Movie;
import lombok.Data;

import java.io.Serializable;

/**
 * Рекорд для объекта, передаваемого по сети.
 * В основном содержит поля для отправки запроса.
 * В ответе большинство полей null.
 * @param id идентификатор
 * @param status стутс программы
 * @param userLogin логин пользователя
 * @param userPassword пароль пользователя
 * @param command название команды
 * @param args аргументы команды(айди для сравнения, строка для мэтча)
 * @param data ответ
 * @param movie фильм
 */

public record NetworkObject(Long id, ApplicationStatus status, String userLogin, String userPassword, String command, String args, String data, Movie movie) implements Serializable {
}
