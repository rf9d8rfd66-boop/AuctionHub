package ru.auctionhub.model;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public class FavoriteAuction {

    private long auctionId;
    private long itemId;
    private String title;
    private String city;
    private BigDecimal currentPrice;
    private OffsetDateTime endTime;
    private String status;
    private String imagePath;

    public FavoriteAuction(long auctionId, long itemId, String title,
                           String city, BigDecimal currentPrice,
                           OffsetDateTime endTime, String status,
                           String imagePath) {
        this.auctionId = auctionId;
        this.itemId = itemId;
        this.title = title;
        this.city = city;
        this.currentPrice = currentPrice;
        this.endTime = endTime;
        this.status = status;
        this.imagePath = imagePath;
    }

    public long getAuctionId() {
        return auctionId;
    }

    public long getItemId() {
        return itemId;
    }

    public String getTitle() {
        return title;
    }

    public String getCity() {
        return city;
    }

    public BigDecimal getCurrentPrice() {
        return currentPrice;
    }

    public OffsetDateTime getEndTime() {
        return endTime;
    }

    public String getStatus() {
        return status;
    }

    public String getImagePath() {
        return imagePath;
    }
}
