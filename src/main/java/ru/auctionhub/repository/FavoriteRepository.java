package ru.auctionhub.repository;

import ru.auctionhub.model.Favorite;
import ru.auctionhub.model.FavoriteAuction;
import ru.auctionhub.util.DbConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

public class FavoriteRepository {

    private Connection getConnection() throws SQLException {
        try {
            return DbConnection.getConnection();
        } catch (ClassNotFoundException e) {
            throw new SQLException("Не удалось загрузить драйвер базы данных", e);
        }
    }

    public List<Favorite> findByUserId(long userId) throws SQLException {

        String sql = """
                SELECT user_id, auction_id
                FROM favorites
                WHERE user_id = ?
                ORDER BY auction_id
                """;

        List<Favorite> favorites = new ArrayList<>();

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setLong(1, userId);

            try (ResultSet resultSet = statement.executeQuery()) {

                while (resultSet.next()) {
                    favorites.add(mapRow(resultSet));
                }
            }
        }

        return favorites;
    }

    public boolean existsByUserIdAndAuctionId(long userId, long auctionId)
            throws SQLException {

        String sql = """
                SELECT EXISTS (
                    SELECT 1
                    FROM favorites
                    WHERE user_id = ? AND auction_id = ?
                )
                """;

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setLong(1, userId);
            statement.setLong(2, auctionId);

            try (ResultSet resultSet = statement.executeQuery()) {

                if (resultSet.next()) {
                    return resultSet.getBoolean(1);
                }
            }
        }

        return false;
    }

    private Favorite mapRow(ResultSet resultSet) throws SQLException {

        return new Favorite(
                resultSet.getLong("user_id"),
                resultSet.getLong("auction_id")
        );
    }

    public void add(long userId, long auctionId) throws SQLException {
        String sql = """
            INSERT INTO favorites (user_id, auction_id)
            VALUES (?, ?)
            ON CONFLICT (user_id, auction_id) DO NOTHING
            """;

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setLong(1, userId);
            statement.setLong(2, auctionId);
            statement.executeUpdate();
        }
    }

    public void remove(long userId, long auctionId) throws SQLException {
        String sql = """
            DELETE FROM favorites
            WHERE user_id = ? AND auction_id = ?
            """;

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setLong(1, userId);
            statement.setLong(2, auctionId);
            statement.executeUpdate();
        }
    }

    public List<FavoriteAuction> findAuctionsByUserId(long userId)
            throws SQLException {

        String sql = """
            SELECT a.id AS auction_id,
                   i.id AS item_id,
                   i.title,
                   i.city,
                   a.current_price,
                   a.end_time,
                   CASE
                       WHEN a.status = 'CANCELLED' THEN 'CANCELLED'
                       WHEN CURRENT_TIMESTAMP < a.start_time THEN 'UPCOMING'
                       WHEN CURRENT_TIMESTAMP < a.end_time THEN 'ACTIVE'
                       ELSE 'COMPLETED'
                   END AS effective_status,
                   img.image_path
            FROM favorites f
            JOIN auctions a ON f.auction_id = a.id
            JOIN items i ON a.item_id = i.id
            LEFT JOIN LATERAL (
                SELECT image_path
                FROM item_images
                WHERE item_id = i.id
                ORDER BY id
                LIMIT 1
            ) img ON TRUE
            WHERE f.user_id = ?
            ORDER BY a.id DESC
            """;

        List<FavoriteAuction> favorites = new ArrayList<>();

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setLong(1, userId);

            try (ResultSet resultSet = statement.executeQuery()) {

                while (resultSet.next()) {

                    favorites.add(new FavoriteAuction(
                            resultSet.getLong("auction_id"),
                            resultSet.getLong("item_id"),
                            resultSet.getString("title"),
                            resultSet.getString("city"),
                            resultSet.getBigDecimal("current_price"),
                            resultSet.getObject("end_time", OffsetDateTime.class),
                            resultSet.getString("effective_status"),
                            resultSet.getString("image_path")
                    ));
                }
            }
        }

        return favorites;
    }
}