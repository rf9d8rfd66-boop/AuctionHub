package ru.auctionhub.repository;

import ru.auctionhub.util.DbConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ItemDeletionRepository {

    private Connection getConnection() throws SQLException {
        try {
            return DbConnection.getConnection();
        } catch (ClassNotFoundException e) {
            throw new SQLException(
                    "Не удалось загрузить драйвер базы данных", e
            );
        }
    }

    public Optional<List<String>> deleteItem(
            long itemId,
            long sellerId
    ) throws SQLException {

        try (Connection connection = getConnection()) {

            try {
                connection.setAutoCommit(false);

                // 1. Блокируем объявление и связанный аукцион.
                // Проверяем, что объявление принадлежит продавцу.

                String lockSql = """
                        SELECT a.id
                        FROM items i
                        JOIN auctions a ON a.item_id = i.id
                        WHERE i.id = ?
                          AND i.seller_id = ?
                        FOR UPDATE OF i, a
                        """;

                long auctionId;

                try (PreparedStatement statement =
                             connection.prepareStatement(lockSql)) {

                    statement.setLong(1, itemId);
                    statement.setLong(2, sellerId);

                    try (ResultSet resultSet = statement.executeQuery()) {

                        if (!resultSet.next()) {
                            connection.rollback();
                            return Optional.empty();
                        }

                        auctionId = resultSet.getLong("id");
                    }
                }

                // 2. Проверяем, что аукцион НЕ активен прямо сейчас.
                // Активным считается аукцион, который не отменён, уже начался и ещё не закончился.
                String checkSql = """
                    SELECT NOT (
                        status <> 'CANCELLED'
                        AND start_time <= clock_timestamp()
                        AND end_time > clock_timestamp()
                    ) AS can_delete
                    FROM auctions
                    WHERE id = ?
                """;
                boolean canDelete;
                try (PreparedStatement statement = connection.prepareStatement(checkSql)) {
                    statement.setLong(1, auctionId);
                    try (ResultSet resultSet = statement.executeQuery()) {
                        if (!resultSet.next()) {
                            connection.rollback();
                            return Optional.empty();
                        }
                        canDelete = resultSet.getBoolean("can_delete");
                    }
                }
                if (!canDelete) {
                    connection.rollback();
                    return Optional.empty();
                }

                // 3. Получаем пути фотографий.
                // После успешного удаления БД удалим сами файлы.

                List<String> imagePaths = new ArrayList<>();

                String imagesSql = """
                        SELECT image_path
                        FROM item_images


WHERE item_id = ?
                        """;

                try (PreparedStatement statement =
                             connection.prepareStatement(imagesSql)) {

                    statement.setLong(1, itemId);

                    try (ResultSet resultSet = statement.executeQuery()) {

                        while (resultSet.next()) {
                            imagePaths.add(
                                    resultSet.getString("image_path")
                            );
                        }
                    }
                }

                // 4. Удаляем связанные записи из избранного.

                String favoritesSql = """
                        DELETE FROM favorites
                        WHERE auction_id = ?
                        """;

                try (PreparedStatement statement =
                             connection.prepareStatement(favoritesSql)) {

                    statement.setLong(1, auctionId);
                    statement.executeUpdate();
                }

                // 4.5. Удаляем связанные ставки (для завершённых/отменённых аукционов).
                String bidsSql = """
                    DELETE FROM bids
                    WHERE auction_id = ?
                """;
                try (PreparedStatement statement = connection.prepareStatement(bidsSql)) {
                    statement.setLong(1, auctionId);
                    statement.executeUpdate();
                }

                // 5. Удаляем записи о фотографиях.

                String imagesDeleteSql = """
                        DELETE FROM item_images
                        WHERE item_id = ?
                        """;

                try (PreparedStatement statement =
                             connection.prepareStatement(imagesDeleteSql)) {

                    statement.setLong(1, itemId);
                    statement.executeUpdate();
                }

                // 6. Удаляем аукцион.

                String auctionDeleteSql = """
                        DELETE FROM auctions
                        WHERE id = ?
                        """;

                try (PreparedStatement statement =
                             connection.prepareStatement(auctionDeleteSql)) {

                    statement.setLong(1, auctionId);
                    statement.executeUpdate();
                }

                // 7. Удаляем само объявление.

                String itemDeleteSql = """
                        DELETE FROM items
                        WHERE id = ?
                          AND seller_id = ?
                        """;

                try (PreparedStatement statement =
                             connection.prepareStatement(itemDeleteSql)) {

                    statement.setLong(1, itemId);
                    statement.setLong(2, sellerId);

                    int deletedRows = statement.executeUpdate();

                    if (deletedRows != 1) {
                        throw new SQLException(
                                "Не удалось удалить объявление"
                        );
                    }
                }

                // 8. Подтверждаем удаление.

                connection.commit();

                return Optional.of(imagePaths);

            } catch (SQLException | RuntimeException e) {

                connection.rollback();
                throw e;

            } finally {
                connection.setAutoCommit(true);
            }
        }
    }
}