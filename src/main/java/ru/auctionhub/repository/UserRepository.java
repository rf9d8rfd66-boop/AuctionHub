package ru.auctionhub.repository;

import ru.auctionhub.model.User;
import ru.auctionhub.util.DbConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UserRepository {

    private Connection getConnection() throws SQLException {
        try {
            return DbConnection.getConnection();
        } catch (ClassNotFoundException e) {
            throw new SQLException("Не удалось загрузить драйвер базы данных", e);
        }
    }

    public Optional<User> findById(long id) throws SQLException {

        String sql = """
                SELECT id, username, email, phone, password, role_id, created_at
                FROM users
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

    public Optional<User> findByUsername(String username) throws SQLException {

        String sql = """
                SELECT id, username, email, phone, password, role_id, created_at
                FROM users
                WHERE username = ?
                """;

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, username);

            try (ResultSet resultSet = statement.executeQuery()) {

                if (resultSet.next()) {
                    return Optional.of(mapRow(resultSet));
                }
            }
        }

        return Optional.empty();
    }

    public boolean existsByUsername(String username) throws SQLException {

        String sql = """
                SELECT EXISTS (
                    SELECT 1 FROM users WHERE username = ?
                )
                """;

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, username);

            try (ResultSet resultSet = statement.executeQuery()) {

                return resultSet.next() && resultSet.getBoolean(1);
            }
        }
    }

    public boolean existsByEmail(String email) throws SQLException {

        String sql = """
                SELECT EXISTS (
                    SELECT 1 FROM users WHERE email = ?
                )
                """;

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, email);

            try (ResultSet resultSet = statement.executeQuery()) {

                return resultSet.next() && resultSet.getBoolean(1);
            }
        }
    }

    public User save(User user) throws SQLException {

        String sql = """
                INSERT INTO users (username, email, phone, password, role_id)
                VALUES (?, ?, ?, ?, ?)
                RETURNING id, username, email, phone, password, role_id, created_at
                """;

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, user.getUsername());
            statement.setString(2, user.getEmail());
            statement.setString(3, user.getPhone());
            statement.setString(4, user.getPassword());
            statement.setLong(5, user.getRoleId());

            try (ResultSet resultSet = statement.executeQuery()) {

                if (resultSet.next()) {
                    return mapRow(resultSet);
                }
            }
        }

        throw new SQLException("Не удалось сохранить пользователя");
    }


    public List<User> findAll() throws SQLException {

        String sql = """
            SELECT id, username, email, phone,
                   NULL::VARCHAR AS password,
                   role_id, created_at
            FROM users
            ORDER BY id
            """;

        List<User> users = new ArrayList<>();

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                users.add(mapRow(resultSet));
            }
        }

        return users;
    }


    private User mapRow(ResultSet resultSet) throws SQLException {

        Timestamp timestamp = resultSet.getTimestamp("created_at");

        return new User(
                resultSet.getLong("id"),
                resultSet.getString("username"),
                resultSet.getString("email"),
                resultSet.getString("phone"),
                resultSet.getString("password"),
                resultSet.getLong("role_id"),
                timestamp != null ? timestamp.toLocalDateTime() : null
        );
    }

    public boolean updateRole(long userId, long roleId)
            throws SQLException {

        String sql = """
            UPDATE users
            SET role_id = ?
            WHERE id = ?
              AND EXISTS (
                  SELECT 1 FROM roles WHERE id = ?
              )
            """;

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setLong(1, roleId);
            statement.setLong(2, userId);
            statement.setLong(3, roleId);

            return statement.executeUpdate() > 0;
        }
    }
}