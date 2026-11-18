package ru.auctionhub.service;

import ru.auctionhub.repository.ItemDeletionRepository;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class ItemDeletionService {

    private final ItemDeletionRepository itemDeletionRepository;

    public ItemDeletionService() {
        this.itemDeletionRepository = new ItemDeletionRepository();
    }

    public Optional<List<String>> deleteItem(
            long itemId,
            long sellerId
    ) throws SQLException {

        if (itemId <= 0 || sellerId <= 0) {
            throw new IllegalArgumentException(
                    "Некорректный идентификатор"
            );
        }

        return itemDeletionRepository.deleteItem(
                itemId,
                sellerId
        );
    }
}