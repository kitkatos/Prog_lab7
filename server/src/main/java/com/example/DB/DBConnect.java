package com.example.DB;

import lombok.extern.log4j.Log4j2;

import java.sql.*;

@Log4j2
public class DBConnect {
    private static final String DB_URL = "jdbc:postgresql://pg:5432/studs";
    private Connection connection;

    public Connection connect() {
        try {
            Class.forName("org.postgresql.Driver");

            connection = DriverManager.getConnection(DB_URL);

            if (connection != null && !connection.isClosed()) {
                log.info("Успешное подключение к БД");
            }

            return connection;

        } catch (ClassNotFoundException e) {
            log.error("Драйвер PostgreSQL не найден");
        } catch (SQLException e) {
            log.error("Ошибка подключения к БД: {}", e.getMessage());
        }
        return null;
    }

    public void initializeTable() {
        String[] script = {
                "CREATE SEQUENCE IF NOT EXISTS users_seq START 1 INCREMENT 1",
                """
            CREATE TABLE IF NOT EXISTS users (
                id users_seq PRIMARY KEY,
                login VARCHAR(255) UNIQUE NOT NULL,
                password VARCHAR(255) NOT NULL,
            );
            """,
            """
            CREATE TABLE IF NOT EXISTS coordinates (
                id SERIAL PRIMARY KEY,
                x FLOAT NOT NULL CHECK (x > -382),
                y DOUBLE PRECISION NOT NULL
            );
            """,
            """
            CREATE TABLE IF NOT EXISTS persons (
                id SERIAL PRIMARY KEY,
                name VARCHAR(255) NOT NULL CHECK (length(name) > 0),
                birthday DATE NOT NULL,
                height FLOAT NOT NULL CHECK (height > 0),
                weight INTEGER NOT NULL CHECK (weight > 0),
                passport_id VARCHAR(47) NOT NULL CHECK (length(passport_id) BETWEEN 6 AND 47)
            );
            """,
            """
            CREATE TYPE IF NOT EXISTS movie_genre AS ENUM (
                'ACTION',
                'DRAMA',
                'MUSICAL',
                'THRILLER',
                'FANTASY'
            );
            """,
            """
            CREATE TYPE IF NOT EXISTS mpaa_rating AS ENUM (
                'G',
                'PG',
                'R',
                'NC_17'
            );
            """,
            """
            CREATE TABLE IF NOT EXISTS movies (
                id SERIAL PRIMARY KEY CHECK (id > 0),
                name VARCHAR(255) NOT NULL CHECK (length(name) > 0),
                coordinates_id BIGINT NOT NULL REFERENCES coordinates(id),
                creation_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                oscars_count INTEGER NOT NULL CHECK (oscars_count > 0),
                usa_box_office FLOAT NOT NULL CHECK (usa_box_office > 0),
                genre MOVIE_GENRE,
                mpaa_rating MPAA_RATING,
                director_id BIGINT REFERENCES persons(id),
                owner_id BIGINT NOT NULL REFERENCES users(id)
            );
            """
        };
        try (Statement stmt = connection.createStatement()) {
            for (String command : script) {
                stmt.execute(command);
            }
            log.info("Таблицы успешно созданы/проверены");
        } catch (SQLException e) {
            log.error("Ошибка создания таблиц: {}", e.getMessage());
        }
    }

    public void disconnect() {
        if (connection != null) {
            try {
                if (!connection.isClosed()) {
                    connection.close();
                    log.info("Соединение с БД успешно закрыто");
                } else {
                    log.info("ℹСоединение с БД уже было закрыто");
                }
            } catch (SQLException e) {
                log.error("Ошибка при закрытии соединения: {}", e.getMessage());
            } finally {
                connection = null;
            }
        } else {
            log.info("Соединение с БД уже было null");
        }
    }
}




