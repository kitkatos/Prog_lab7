package com.example.common.IO.exception;

/**
 * Выбрасывается, когда поле вводится с неверным форматом или типом данных.
 */
public class InvalidFormatException extends NumberFormatException {
    public InvalidFormatException(String message) {
        super(message);
    }
}
