package com.example.IO.interfaces;

/**
 * Интерфейс для всех классов ввода данных.
 * Нужен для того, чтобы в рамках программы ввод через тг и консоль не отличались.
 */
public interface InputManager {
    /**
     * Читает строку через чтото.
     * @return прочитанная строка
     */
    public String readLine();
}
