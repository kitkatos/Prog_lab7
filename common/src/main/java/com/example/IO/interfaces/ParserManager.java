package com.example.IO.interfaces;

import com.example.IO.InputArgs;

/**
 * Интерфейс для всех классов парсинга введенных данных.
 * Нужен для того, чтобы в рамках программы парсинг для тг и консоль не отличались.
 */
public interface ParserManager {
    /**
     * Преобразует ввод в специализированный рекорд.
     * @param input введенная строка
     * @return команда, параметры и пользователь
     */
    public InputArgs getInputArgs(String input);
}
