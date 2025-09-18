package com.example.common.network.serializer;

/**
 * Выбрасывается в сериализаторе, если не получается восстановить объект T из Object.
 */
public class SerializeException extends RuntimeException {
    public SerializeException(String message) {
        super(message);
    }
}
