package ru.auctionhub.repository;

import ru.auctionhub.util.DbConnection;
import ru.auctionhub.model.ItemImage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ItemImageRepository {

    private Connection getConnection() throws SQLException {
        try {
            return DbConnection.getConnection();
        } catch (ClassNotFoundException e) {
            throw new SQLException("Не удалось загрузить драйвер базы данных", e);
        }
    }

    public List<ItemImage> findByItemId(long itemId) throws SQLException {

        String sql = """
                SELECT id, item_id, image_path
                FROM item_images
                WHERE item_id = ?
                ORDER BY id
                """;

        List<ItemImage> images = new ArrayList<>();

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setLong(1, itemId);

            try (ResultSet resultSet = statement.executeQuery()) {

                while (resultSet.next()) {
                    images.add(mapRow(resultSet));
                }
            }
        }

        return images;
    }

    private ItemImage mapRow(ResultSet resultSet) throws SQLException {

        return new ItemImage(
                resultSet.getLong("id"),
                resultSet.getLong("item_id"),
                resultSet.getString("image_path")
        );
    }
}

