package ru.auctionhub.model;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public class Bid {

    private long id;
    private long auctionId;
    private long userId;
    private BigDecimal amount;
    private OffsetDateTime createdAt;
    private String username;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Bid(
            long id,
            long auctionId,
            long userId,
            BigDecimal amount,
            OffsetDateTime createdAt,
            String username
    ) {
        this.id = id;
        this.auctionId = auctionId;
        this.userId = userId;
        this.amount = amount;
        this.createdAt = createdAt;
        this.username = username;
    }

    public long getId() {
        return id;
    }

    public long getAuctionId() {
        return auctionId;
    }

    public long getUserId() {
        return userId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }
}
