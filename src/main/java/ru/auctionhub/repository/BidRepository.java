package ru.auctionhub.repository;

import ru.auctionhub.util.DbConnection;
import ru.auctionhub.model.Bid;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.OffsetDateTime;

public class BidRepository {

    private Connection getConnection() throws SQLException {
        try {
            return DbConnection.getConnection();
        } catch (ClassNotFoundException e) {
            throw new SQLException("Не удалось загрузить драйвер базы данных", e);
        }
    }

    public Optional<Bid> findById(long id) throws SQLException {

        String sql = """
                        SELECT b.id,
                               b.auction_id,
                               b.user_id,
                               b.amount,
                               b.created_at,
                               u.username
                        FROM bids b
                        JOIN users u ON b.user_id = u.id
                        WHERE b.id = ?
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

    public List<Bid> findByAuctionId(long auctionId) throws SQLException {

        String sql = """
                SELECT b.id,
                       b.auction_id,
                       b.user_id,
                       b.amount,
                       b.created_at,
                       u.username
                FROM bids b
                JOIN users u ON b.user_id = u.id
                WHERE b.auction_id = ?
                ORDER BY b.amount DESC, b.created_at DESC
                """;

        List<Bid> bids = new ArrayList<>();

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setLong(1, auctionId);

            try (ResultSet resultSet = statement.executeQuery()) {

                while (resultSet.next()) {
                    bids.add(mapRow(resultSet));
                }
            }
        }

        return bids;
    }

    public List<Bid> findByUserId(long userId) throws SQLException {

        String sql = """
                        SELECT b.id,
                               b.auction_id,
                               b.user_id,
                               b.amount,
                               b.created_at,
                               u.username
                        FROM bids b
                        JOIN users u ON b.user_id = u.id
                        WHERE b.user_id = ?
                        ORDER BY b.created_at DESC
                """;

        List<Bid> bids = new ArrayList<>();

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setLong(1, userId);

            try (ResultSet resultSet = statement.executeQuery()) {

                while (resultSet.next()) {
                    bids.add(mapRow(resultSet));
                }
            }
        }

        return bids;
    }

    private Bid mapRow(ResultSet resultSet) throws SQLException {

        return new Bid(
                resultSet.getLong("id"),
                resultSet.getLong("auction_id"),
                resultSet.getLong("user_id"),
                resultSet.getBigDecimal("amount"),
                resultSet.getObject("created_at", OffsetDateTime.class),
                resultSet.getString("username")
        );
    }

    public void placeBid(long auctionId, long userId, BigDecimal amount)
            throws SQLException {

        try (Connection connection = getConnection()) {

            connection.setAutoCommit(false);

            try {
                BigDecimal currentPrice;
                BigDecimal bidStep;
                OffsetDateTime startTime;
                OffsetDateTime endTime;
                String status;
                long sellerId;

                // Блокируем строку аукциона на время транзакции
                String selectSql = """
                    SELECT
                        a.current_price,
                        a.bid_step,
                        a.start_time,
                        a.end_time,
                        a.status,
                        i.seller_id
                    FROM auctions a
                    JOIN items i ON a.item_id = i.id
                    WHERE a.id = ?
                    FOR UPDATE OF a
                    """;

                try (PreparedStatement statement =
                             connection.prepareStatement(selectSql)) {

                    statement.setLong(1, auctionId);

                    try (ResultSet resultSet = statement.executeQuery()) {

                        if (!resultSet.next()) {
                            throw new SQLException("Аукцион не найден");
                        }

                        currentPrice = resultSet.getBigDecimal("current_price");
                        bidStep = resultSet.getBigDecimal("bid_step");
                        startTime = resultSet.getObject(
                                "start_time", OffsetDateTime.class);
                        endTime = resultSet.getObject(
                                "end_time", OffsetDateTime.class);
                        status = resultSet.getString("status");
                        sellerId = resultSet.getLong("seller_id");
                    }
                }

                // Проверяем, что пользователь не продавец
                if (sellerId == userId) {
                    throw new SQLException(
                            "Нельзя делать ставки на собственный товар");
                }

                // Проверяем отмену аукциона
                if ("CANCELLED".equals(status)) {
                    throw new SQLException("Аукцион отменён");
                }

                // Получаем текущее время непосредственно из PostgreSQL
                OffsetDateTime now;

                try (PreparedStatement statement =
                             connection.prepareStatement(
                                     "SELECT clock_timestamp()")) {

                    try (ResultSet resultSet = statement.executeQuery()) {
                        resultSet.next();
                        now = resultSet.getObject(1, OffsetDateTime.class);
                    }
                }

                // Проверяем время начала
                if (now.isBefore(startTime)) {
                    throw new SQLException("Аукцион ещё не начался");
                }

                // Проверяем время окончания
                if (!now.isBefore(endTime)) {
                    throw new SQLException("Аукцион уже завершён");
                }

                // Проверяем сумму ставки
                BigDecimal minimumBid = currentPrice.add(bidStep);

                if (amount == null || amount.compareTo(minimumBid) < 0) {
                    throw new SQLException(
                            "Минимальная ставка: " + minimumBid);
                }

                // Сохраняем ставку
                String insertSql = """
                    INSERT INTO bids (auction_id, user_id, amount)
                    VALUES (?, ?, ?)
                    """;

                try (PreparedStatement statement =
                             connection.prepareStatement(insertSql)) {

                    statement.setLong(1, auctionId);
                    statement.setLong(2, userId);
                    statement.setBigDecimal(3, amount);

                    statement.executeUpdate();
                }

                // Обновляем цену и победителя
                String updateSql = """
                    UPDATE auctions
                    SET current_price = ?,
                        winner_id = ?
                    WHERE id = ?
                    """;


                try (PreparedStatement statement =
                             connection.prepareStatement(updateSql)) {

                    statement.setBigDecimal(1, amount);
                    statement.setLong(2, userId);
                    statement.setLong(3, auctionId);

                    statement.executeUpdate();
                }

                connection.commit();

            } catch (SQLException | RuntimeException e) {

                try {
                    connection.rollback();
                } catch (SQLException rollbackException) {
                    e.addSuppressed(rollbackException);
                }

                throw e;

            } finally {
                connection.setAutoCommit(true);
            }
        }
    }
}