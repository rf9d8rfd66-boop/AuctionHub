package ru.auctionhub.service;

import ru.auctionhub.model.Item;
import ru.auctionhub.repository.ItemCreationRepository;
import ru.auctionhub.repository.ItemRepository;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class ItemService {

    private final ItemRepository itemRepository;

    private final ItemCreationRepository itemCreationRepository =
            new ItemCreationRepository();

    public ItemService() {
        this.itemRepository = new ItemRepository();
    }

    // Найти товар по ID
    public Optional<Item> findById(long id) throws SQLException {
        return itemRepository.findById(id);
    }

    // Получить все товары
    public List<Item> findAll() throws SQLException {
        return itemRepository.findAll();
    }

    // Получить товары конкретного продавца
    public List<Item> findBySellerId(long sellerId) throws SQLException {
        return itemRepository.findBySellerId(sellerId);
    }

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

        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("Введите название товара");
        }

        if (city == null || city.isBlank()) {
            throw new IllegalArgumentException("Укажите город");
        }

        if (startingPrice == null ||
                startingPrice.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException(
                    "Начальная цена должна быть больше нуля"
            );
        }

        if (bidStep == null ||
                bidStep.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException(
                    "Шаг ставки должен быть больше нуля"
            );
        }

        if (imagePaths != null && imagePaths.size() > 5) {
            throw new IllegalArgumentException(
                    "Можно загрузить не более 5 фотографий"
            );
        }

        return itemCreationRepository.createItemAndAuction(
                sellerId,
                categoryId,
                title.trim(),
                description,
                city.trim(),
                startingPrice,
                bidStep,
                imagePaths
        );
    }
}
