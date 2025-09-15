package com.example.DB;

import lombok.extern.log4j.Log4j2;

import java.sql.*;

/**
 * Класс для подключения к базе и создания таблиц.
 * Подразумевает работу на гелиосе.
 */

@Log4j2
public class DBConnector {
    private static final String DB_URL = "jdbc:postgresql://pg:5432/studs";
    private Connection connection;

    /**
     * Создает объект Connection для взаимодействия с бдшкой
     * @return объект Connection
     */
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

    /**
     * Инициализирует таблицы
     */
    public void initializeTable() {
        String[] script = {
                """
            CREATE TABLE IF NOT EXISTS users (
                  login VARCHAR(255) PRIMARY KEY,
                  password VARCHAR(255) NOT NULL,
              );
            """,
                """
              CREATE TYPE IF NOT EXISTS movie_genre AS ENUM (
                  'ACTION', 'DRAMA', 'MUSICAL', 'THRILLER', 'FANTASY'
              );
            """,
                """
              CREATE TYPE IF NOT EXISTS mpaa_rating AS ENUM (
                  'G', 'PG', 'R', 'NC_17'
              );
            """,
                """
              CREATE TABLE IF NOT EXISTS movies (
                  id BIGSERIAL PRIMARY KEY,
                  name VARCHAR(255) NOT NULL CHECK (length(name) > 0),
                  
                  coordinate_x FLOAT NOT NULL CHECK (coordinate_x > -382),
                  coordinate_y DOUBLE PRECISION NOT NULL,
                 
                  creation_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                  oscars_count INTEGER NOT NULL CHECK (oscars_count > 0),
                  usa_box_office FLOAT NOT NULL CHECK (usa_box_office > 0),
                  genre MOVIE_GENRE,
                  mpaa_rating MPAA_RATING,
                
                  director_name VARCHAR(255),
                  director_birthday TIMESTAMP,
                  director_height FLOAT CHECK (director_height > 0),
                  director_weight INTEGER CHECK (director_weight > 0),
                  director_passport_id VARCHAR(47),
                 
                  owner_login VARCHAR(255) NOT NULL REFERENCES users(login)
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

    /**
     * Отключается от бдшки
     * Превращает Connection в null
     */
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




