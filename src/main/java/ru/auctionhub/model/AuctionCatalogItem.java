package ru.auctionhub.model;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public class AuctionCatalogItem {

    private long auctionId;
    private long itemId;
    private String title;
    private String city;
    private String categoryName;
    private String imagePath;
    private BigDecimal currentPrice;
    private BigDecimal bidStep;
    private OffsetDateTime endTime;
    private String status;
    private String sellerUsername;
    private OffsetDateTime startTime;

    public OffsetDateTime getStartTime() {
        return startTime;
    }

    public AuctionCatalogItem(long auctionId, long itemId, String title,
                              String city, String categoryName, String imagePath,
                              BigDecimal currentPrice, BigDecimal bidStep,
                              OffsetDateTime atartTime, OffsetDateTime endTime, String status,
                              String sellerUsername) {
        this.auctionId = auctionId;
        this.itemId = itemId;
        this.title = title;
        this.city = city;
        this.categoryName = categoryName;
        this.imagePath = imagePath;
        this.currentPrice = currentPrice;
        this.bidStep = bidStep;
        this.endTime = endTime;
        this.status = status;
        this.sellerUsername = sellerUsername;
        this.startTime = atartTime;
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

    public String getCategoryName() {
        return categoryName;
    }

    public String getImagePath() {
        return imagePath;
    }

    public BigDecimal getCurrentPrice() {
        return currentPrice;
    }

    public BigDecimal getBidStep() {
        return bidStep;
    }

    public OffsetDateTime getEndTime() {
        return endTime;
    }

    public String getStatus() {
        return status;
    }

    public String getSellerUsername() {
        return sellerUsername;
    }

    public Long getCountdownTarget() {
        if ("UPCOMING".equals(status)) {
            return startTime.toInstant().toEpochMilli();
        }

        if ("ACTIVE".equals(status)) {
            return endTime.toInstant().toEpochMilli();
        }

        return null;
    }

    public String getFormattedEndTime() {
        return endTime
                .atZoneSameInstant(java.time.ZoneId.of("Europe/Moscow"))
                .format(java.time.format.DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"));
    }
}