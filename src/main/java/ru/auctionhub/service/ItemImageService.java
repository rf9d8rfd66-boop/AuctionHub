
package ru.auctionhub.service;

import ru.auctionhub.model.ItemImage;
import ru.auctionhub.repository.ItemImageRepository;

import java.sql.SQLException;
import java.util.List;

public class ItemImageService {

    private final ItemImageRepository itemImageRepository;

    public ItemImageService() {
        this.itemImageRepository = new ItemImageRepository();
    }

    public List<ItemImage> findByItemId(long itemId) throws SQLException {
        return itemImageRepository.findByItemId(itemId);
    }
}


