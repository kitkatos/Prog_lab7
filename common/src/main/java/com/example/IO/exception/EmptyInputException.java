package com.example.IO.exception;

import java.io.IOException;

/**
 * Выбрасывается, когда обязательное поле вводится пустым.
 */
public class EmptyInputException extends IOException {
    public EmptyInputException(String message) {
        super(message);
    }
}
