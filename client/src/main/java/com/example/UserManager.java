package com.example;

import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Менеджер, управляющий учётными данными пользователей на клиенте.
 * Позволяет связывать Telegram ID с логином и паролем.
 * Хранит данные консольного пользователя.
 */
@Log4j2
@Getter
public class UserManager {
    private final Map<Long, UserData> userDataMap = new ConcurrentHashMap<>();
    /**
     * Возвращает учетные данные для указанного Telegram ID.
     * @param telegramId ID пользователя Telegram
     * @return Учетные данные пользователя, или null, если не найдены
     */
    public UserData getUserData(long telegramId) {
        return userDataMap.get(telegramId);
    }

    /**
     * Сохраняет учетные данные для указанного Telegram ID.
     * @param telegramId ID пользователя Telegram
     * @param login логин пользователя
     * @param password пароль пользователя
     */
    public void saveUserData(long telegramId, String login, String password) {
        UserData userData = new UserData(login, password);
        userDataMap.put(telegramId, userData);
        log.info("Учетные данные для ID {} сохранены", telegramId);
    }

    /**
     * Проверяет, авторизован ли пользователь.
     * @param telegramId ID пользователя Telegram
     * @return true, если учетные данные существуют, иначе false
     */
    public boolean isAuthenticated(long telegramId) {
        return userDataMap.containsKey(telegramId);
    }

}
