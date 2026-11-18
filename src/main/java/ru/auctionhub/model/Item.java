package ru.auctionhub.model;

import java.time.LocalDateTime;

public class Item {

    private long id;
    private long sellerId;
    private long categoryId;
    private String title;
    private String description;
    private String city;
    private LocalDateTime createdAt;

    public Item(long id, long sellerId, long categoryId, String title,
                String description, String city, LocalDateTime createdAt) {
        this.id = id;
        this.sellerId = sellerId;
        this.categoryId = categoryId;
        this.title = title;
        this.description = description;
        this.city = city;
        this.createdAt = createdAt;
    }

    public long getId() {
        return id;
    }

    public long getSellerId() {
        return sellerId;
    }

    public long getCategoryId() {
        return categoryId;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getCity() {
        return city;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}