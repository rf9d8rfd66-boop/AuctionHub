package ru.auctionhub.repository;

import ru.auctionhub.model.Item;
import ru.auctionhub.util.DbConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ItemRepository {

    // Получение соединения с обработкой ClassNotFoundException
    private Connection getConnection() throws SQLException {
        try {
            return DbConnection.getConnection();
        } catch (ClassNotFoundException e) {
            throw new SQLException("Не удалось загрузить драйвер базы данных", e);
        }
    }

    // Найти товар по ID
    public Optional<Item> findById(long id) throws SQLException {

        String sql = """
                SELECT *
                FROM items
                WHERE id = ?
                """;

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setLong(1, id);

            try (ResultSet resultSet = statement.executeQuery()) {

                if (resultSet.next()) {
                    return Optional.of(mapRow(resultSet));
                }
            }
        }

        return Optional.empty();
    }

    // Получить все товары
    public List<Item> findAll() throws SQLException {

        String sql = """
                SELECT *
                FROM items
                ORDER BY created_at DESC
                """;

        List<Item> items = new ArrayList<>();

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                items.add(mapRow(resultSet));
            }
        }

        return items;
    }

    // Получить товары конкретного продавца
    public List<Item> findBySellerId(long sellerId) throws SQLException {

        String sql = """
                SELECT *
                FROM items
                WHERE seller_id = ?
                ORDER BY created_at DESC
                """;

        List<Item> items = new ArrayList<>();

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setLong(1, sellerId);

            try (ResultSet resultSet = statement.executeQuery()) {

                while (resultSet.next()) {
                    items.add(mapRow(resultSet));
                }
            }
        }

        return items;
    }

    // Преобразование строки БД в объект Item
    private Item mapRow(ResultSet resultSet) throws SQLException {

        Timestamp timestamp = resultSet.getTimestamp("created_at");

        return new Item(
                resultSet.getLong("id"),
                resultSet.getLong("seller_id"),
                resultSet.getLong("category_id"),
                resultSet.getString("title"),
                resultSet.getString("description"),
                resultSet.getString("city"),
                timestamp != null ? timestamp.toLocalDateTime() : null
        );
    }
}