package ru.auctionhub.repository;

import ru.auctionhub.util.DbConnection;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class ItemCreationRepository {

    public long createItemAndAuction(
            long sellerId,
            long categoryId,
            String title,
            String description,
            String city,
            BigDecimal startingPrice,
            BigDecimal bidStep,
            List<String> imagePaths
    ) throws SQLException {

        try (Connection connection = getConnection()) {

            try {
                // Отключаем автоматическое подтверждение запросов
                connection.setAutoCommit(false);

                long itemId;

                // 1. Создаём товар
                String itemSql = """
                        INSERT INTO items (
                            seller_id,
                            category_id,
                            title,
                            description,
                            city
                        )
                        VALUES (?, ?, ?, ?, ?)
                        RETURNING id
                        """;

                try (PreparedStatement statement =
                             connection.prepareStatement(itemSql)) {

                    statement.setLong(1, sellerId);
                    statement.setLong(2, categoryId);
                    statement.setString(3, title);
                    statement.setString(4, description);
                    statement.setString(5, city);

                    try (ResultSet resultSet = statement.executeQuery()) {
                        if (!resultSet.next()) {
                            throw new SQLException("Не удалось создать товар");
                        }

                        itemId = resultSet.getLong("id");
                    }
                }

                // 2. Создаём аукцион для товара
                long auctionId;
                String auctionSql = """
                    INSERT INTO auctions (
                        item_id,
                        starting_price,
                        current_price,
                        bid_step,
                        start_time,
                        end_time,
                        status
                    )
                    VALUES (
                        ?,
                        ?,
                        ?,
                        ?,
                        CURRENT_TIMESTAMP + INTERVAL '24 hours',
                        CURRENT_TIMESTAMP + INTERVAL '48 hours',
                        'UPCOMING'
                    )
                    RETURNING id
                """;
                try (PreparedStatement statement = connection.prepareStatement(auctionSql)) {
                    statement.setLong(1, itemId);
                    statement.setBigDecimal(2, startingPrice);
                    statement.setBigDecimal(3, startingPrice);
                    statement.setBigDecimal(4, bidStep);
                    try (ResultSet resultSet = statement.executeQuery()) {
                        if (!resultSet.next()) {
                            throw new SQLException("Не удалось создать аукцион");
                        }
                        auctionId = resultSet.getLong("id");
                    }
                }

                // 3. Сохраняем пути фотографий
                if (imagePaths != null && !imagePaths.isEmpty()) {

                    String imageSql = """
                    INSERT INTO item_images (item_id, image_path)
                    VALUES (?, ?)
                    """;

                    try (PreparedStatement statement =
                                 connection.prepareStatement(imageSql)) {

                        for (String imagePath : imagePaths) {
                            statement.setLong(1, itemId);
                            statement.setString(2, imagePath);
                            statement.addBatch();
                        }

                        statement.executeBatch();
                    }
                }

                // Подтверждаем все операции
                connection.commit();

                return itemId;

            } catch (SQLException e) {

                // Если что-то пошло не так, отменяем обе операции
                connection.rollback();
                throw e;

            } finally {
                connection.setAutoCommit(true);
            }
        }
    }

    private Connection getConnection() throws SQLException {
        try {
            return DbConnection.getConnection();
        } catch (ClassNotFoundException e) {
            throw new SQLException(
                    "Не удалось загрузить драйвер базы данных", e
            );
        }
    }
}