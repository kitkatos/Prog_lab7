package com.example.network;

import com.example.ApplicationStatus;
import com.example.model.Movie;

import java.io.Serializable;

/**
 * Рекорд для объекта, передаваемого по сети.
 * В основном содержит поля для отправки запроса.
 * В ответе большинство полей null.
 * @param status стутс программы
 * @param userLogin логин пользователя
 * @param userPassword пароль пользователя
 * @param command название команды
 * @param args аргументы команды(айди для сравнения, строка для мэтча)
 * @param data строчные данные: фильм и тп
 * @param movie фильм
 */
public record NetworkObject(ApplicationStatus status, String userLogin, String userPassword, String command, String args, String data, Movie movie) implements Serializable {
}
