package ru.auctionhub.repository;

import ru.auctionhub.model.Category;
import ru.auctionhub.util.DbConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CategoryRepository {

    private Connection getConnection() throws SQLException {
        try {
            return DbConnection.getConnection();
        } catch (ClassNotFoundException e) {
            throw new SQLException(
                    "Не удалось загрузить драйвер базы данных", e
            );
        }
    }

    public List<Category> findAll() throws SQLException {

        String sql = """
                SELECT id, name
                FROM categories
                ORDER BY id
                """;

        List<Category> categories = new ArrayList<>();

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                categories.add(new Category(
                        resultSet.getLong("id"),
                        resultSet.getString("name")
                ));
            }
        }

        return categories;
    }

    public Optional<Category> findById(long id) throws SQLException {

        String sql = """
                SELECT id, name
                FROM categories
                WHERE id = ?
                """;

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setLong(1, id);

            try (ResultSet resultSet = statement.executeQuery()) {

                if (resultSet.next()) {
                    return Optional.of(new Category(
                            resultSet.getLong("id"),
                            resultSet.getString("name")
                    ));
                }
            }
        }

        return Optional.empty();
    }

    // Добавление категории
    public Category add(String name) throws SQLException {

        String sql = """
                INSERT INTO categories (name)
                VALUES (?)
                RETURNING id, name
                """;

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, name);

            try (ResultSet resultSet = statement.executeQuery()) {

                if (resultSet.next()) {
                    return new Category(
                            resultSet.getLong("id"),
                            resultSet.getString("name")
                    );
                }
            }
        }

        throw new SQLException("Не удалось добавить категорию");
    }

    // Удаление категории, если к ней не привязаны товары
    public boolean deleteIfUnused(long id) throws SQLException {

        String sql = """
                DELETE FROM categories c
                WHERE c.id = ?
                  AND NOT EXISTS (
                      SELECT 1
                      FROM items i
                      WHERE i.category_id = c.id
                  )
                """;

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setLong(1, id);

            return statement.executeUpdate() > 0;
        }
    }

    public boolean update(long id, String name) throws SQLException {
        String sql = "UPDATE categories SET name = ? WHERE id = ?";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, name);
            statement.setLong(2, id);
            return statement.executeUpdate() > 0;
        }
    }
}