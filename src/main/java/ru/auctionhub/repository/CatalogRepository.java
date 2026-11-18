package ru.auctionhub.repository;

import ru.auctionhub.model.AuctionCatalogItem;
import ru.auctionhub.model.CatalogFilter;
import ru.auctionhub.util.DbConnection;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CatalogRepository {

    private Connection getConnection() throws SQLException {
        try {
            return DbConnection.getConnection();
        } catch (ClassNotFoundException e) {
            throw new SQLException("Не удалось загрузить драйвер базы данных", e);
        }
    }

    public List<AuctionCatalogItem> findAll() throws SQLException {
        return findAll(new CatalogFilter());
    }

    public List<AuctionCatalogItem> findAll(String title) throws SQLException {
        CatalogFilter filter = new CatalogFilter();
        filter.setTitle(title);

        return findAll(filter);
    }

    public List<AuctionCatalogItem> findAll(CatalogFilter filter) throws SQLException {

        List<AuctionCatalogItem> catalogItems = new ArrayList<>();
        List<Object> parameters = new ArrayList<>();

        String statusExpression = """
                CASE
                    WHEN a.status = 'CANCELLED' THEN 'CANCELLED'
                    WHEN CURRENT_TIMESTAMP < a.start_time THEN 'UPCOMING'
                    WHEN CURRENT_TIMESTAMP < a.end_time THEN 'ACTIVE'
                    ELSE 'COMPLETED'
                END
                """;

        StringBuilder sql = new StringBuilder("""
                SELECT
                    a.id AS auction_id,
                    i.id AS item_id,
                    i.title,
                    i.city,
                    c.name AS category_name,
                    img.image_path,
                    a.current_price,
                    a.bid_step,
                    a.start_time,
                    a.end_time,
                """);

        sql.append(statusExpression).append("""
                    AS status,
                    u.username AS seller_username
                FROM auctions a
                JOIN items i ON a.item_id = i.id
                JOIN categories c ON i.category_id = c.id
                JOIN users u ON i.seller_id = u.id
                LEFT JOIN LATERAL (
                    SELECT image_path
                    FROM item_images
                    WHERE item_id = i.id
                    ORDER BY id
                    LIMIT 1
                ) img ON TRUE
                WHERE 1 = 1
                """);

        if (filter != null) {

            // Поиск по названию
            if (filter.getTitle() != null && !filter.getTitle().isBlank()) {
                sql.append(" AND i.title ILIKE ? ");
                parameters.add("%" + filter.getTitle().trim() + "%");
            }

            // Фильтр по категории
            if (filter.getCategoryId() != null) {
                sql.append(" AND i.category_id = ? ");
                parameters.add(filter.getCategoryId());
            }

            // Фильтр по городу
            if (filter.getCity() != null && !filter.getCity().isBlank()) {
                sql.append(" AND i.city ILIKE ? ");
                parameters.add("%" + filter.getCity().trim() + "%");
            }

            // Минимальная цена
            if (filter.getMinPrice() != null) {
                sql.append(" AND a.current_price >= ? ");
                parameters.add(filter.getMinPrice());
            }

            // Максимальная цена
            if (filter.getMaxPrice() != null) {
                sql.append(" AND a.current_price <= ? ");
                parameters.add(filter.getMaxPrice());
            }

            // Фильтр по статусу
            if (filter.getStatus() != null && !filter.getStatus().isBlank()) {
                sql.append(" AND (").append(statusExpression).append(") = ? ");
                parameters.add(filter.getStatus());
            }
        }

        // Сортировка
        String sort = filter != null ? filter.getSort() : null;

        switch (sort == null ? "newest" : sort) {
            case "ending_soon" ->
                    sql.append(" ORDER BY a.end_time ASC ");

            case "price_asc" ->
                    sql.append(" ORDER BY a.current_price ASC ");

            case "price_desc" ->
                    sql.append(" ORDER BY a.current_price DESC ");

            default ->
                    sql.append(" ORDER BY a.created_at DESC ");
        }

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql.toString())) {

            for (int i = 0; i < parameters.size(); i++) {
                statement.setObject(i + 1, parameters.get(i));
            }

            try (ResultSet resultSet = statement.executeQuery()) {

                while (resultSet.next()) {

                    AuctionCatalogItem item = new AuctionCatalogItem(
                            resultSet.getLong("auction_id"),
                            resultSet.getLong("item_id"),
                            resultSet.getString("title"),
                            resultSet.getString("city"),
                            resultSet.getString("category_name"),
                            resultSet.getString("image_path"),
                            resultSet.getBigDecimal("current_price"),
                            resultSet.getBigDecimal("bid_step"),
                            resultSet.getObject("start_time",
                                    java.time.OffsetDateTime.class),
                            resultSet.getObject("end_time",
                                    java.time.OffsetDateTime.class),
                            resultSet.getString("status"),
                            resultSet.getString("seller_username")
                    );

                    catalogItems.add(item);
                }
            }
        }

        return catalogItems;
    }
}