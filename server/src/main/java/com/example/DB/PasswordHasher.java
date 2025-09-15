package com.example.DB;

import lombok.extern.log4j.Log4j2;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

@Log4j2
public class PasswordHasher {
    public static String sha1(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-1");
            byte[] hashBytes = md.digest(input.getBytes(StandardCharsets.UTF_8));
            log.info("Строка превращена в байты хешированием");

            StringBuilder sb = new StringBuilder();
            for (byte b : hashBytes) {
                sb.append(String.format("%02x", b));
            }
            log.info("Пароль отхэширован");

            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            log.error("SHA-1 алгоритм не поддерживается {}", e.getMessage());
            throw new RuntimeException("SHA-1 алгоритм не поддерживается", e);
        }
    }
}

