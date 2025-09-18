package com.example.IO.exception;

/**
 * Вводится, когда поле принимает недопустимое значение по диапазону.
 */
public class OutOfRangeException extends Exception{
    public OutOfRangeException(String message) {
        super(message);
    }
}
