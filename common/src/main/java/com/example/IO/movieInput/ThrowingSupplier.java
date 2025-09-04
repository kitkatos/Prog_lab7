package com.example.IO.movieInput;

/**
 * Функицональный интерфейс, необходимый для файбрики.
 * Главная особенность - умеет пробрасывать исключения.
 * @param <T>
 */
@FunctionalInterface
public interface ThrowingSupplier<T> {
    T get() throws Exception;
}
