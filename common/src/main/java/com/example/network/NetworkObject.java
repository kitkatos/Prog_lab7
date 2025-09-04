package com.example.network;

import java.io.Serializable;

/**
 * Рекорд для объекта, передаваемого по сети.
 * Струкура data:
 * @param userID идентификатор пользователя
 * @param data все данные, ключая команду, объекты, исформационные сообщения
 */
public record NetworkObject(String userID, String data) implements Serializable {
}
