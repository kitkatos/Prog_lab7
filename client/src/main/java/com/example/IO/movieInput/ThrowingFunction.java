package com.example.common.IO.movieInput;

/**
 * Функицональный интерфейс, необходимый для файбрики.
 * Главная особенность - умеет пробрасывать исключения.
 * @param <T>
 * @param <R>
 */
@FunctionalInterface
public interface ThrowingFunction<T, R> {
    R apply(T t) throws Exception;
}
