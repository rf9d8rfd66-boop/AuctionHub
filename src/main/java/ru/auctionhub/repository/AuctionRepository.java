package ru.auctionhub.repository;

import ru.auctionhub.util.DbConnection;
import ru.auctionhub.model.Auction;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class AuctionRepository {

    private Connection getConnection() throws SQLException {
        try {
            return DbConnection.getConnection();
        } catch (ClassNotFoundException e) {
            throw new SQLException("Не удалось загрузить драйвер базы данных", e);
        }
    }

    public Optional<Auction> findById(long id) throws SQLException {

        String sql = """
                SELECT id, item_id, starting_price, current_price,
                       bid_step, created_at, start_time, end_time,
                       winner_id, status
                FROM auctions
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

    public List<Auction> findAll() throws SQLException {

        String sql = """
                SELECT id, item_id, starting_price, current_price,
                       bid_step, created_at, start_time, end_time,
                       winner_id, status
                FROM auctions
                ORDER BY created_at DESC
                """;

        List<Auction> auctions = new ArrayList<>();

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                auctions.add(mapRow(resultSet));
            }
        }

        return auctions;
    }

    public Optional<Auction> findByItemId(long itemId) throws SQLException {

        String sql = """
                SELECT id, item_id, starting_price, current_price,
                       bid_step, created_at, start_time, end_time,
                       winner_id, status
                FROM auctions
                WHERE item_id = ?
                """;

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setLong(1, itemId);

            try (ResultSet resultSet = statement.executeQuery()) {

                if (resultSet.next()) {
                    return Optional.of(mapRow(resultSet));
                }
            }
        }

        return Optional.empty();
    }

    private Auction mapRow(ResultSet resultSet) throws SQLException {

        return new Auction(
                resultSet.getLong("id"),
                resultSet.getLong("item_id"),
                resultSet.getBigDecimal("starting_price"),
                resultSet.getBigDecimal("current_price"),
                resultSet.getBigDecimal("bid_step"),
                resultSet.getObject("created_at", OffsetDateTime.class),
                resultSet.getObject("start_time", OffsetDateTime.class),
                resultSet.getObject("end_time", OffsetDateTime.class),
                resultSet.getObject("winner_id", Long.class),
                resultSet.getString("status")
        );
    }

    public boolean cancelIfActiveOrUpcoming(long auctionId)
            throws SQLException {

        String sql = """
            UPDATE auctions
            SET status = 'CANCELLED'
            WHERE id = ?
              AND status <> 'CANCELLED'
              AND end_time > CURRENT_TIMESTAMP
            """;

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setLong(1, auctionId);

            return statement.executeUpdate() > 0;
        }
    }
}