package com.example.IO.interfaces;

/**
 * Интерфейс для всех классов вывода данных.
 * Нужен для того, чтобы в рамках программы выывод через тг и консоль не отличались.
 */
public interface OutputManager {

    /**
     * Выводит строку.
     * Добавляет переход на новую строку в конце.
     * @param str любая строка
     */
    public void printLine(String str);

    /**
     * Выводит строку.
     * Не добавляет переход на новую строку.
     * @param str людая строка
     */
    public void printString(String str);

    /**
     * Выводит строку с подпись, что это ошибка.
     * @param str любая строка
     */
    public void printError(String str);
}
