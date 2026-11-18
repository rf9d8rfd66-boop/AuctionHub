package ru.auctionhub.model;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public class Auction {

    private long id;
    private long itemId;
    private BigDecimal startingPrice;
    private BigDecimal currentPrice;
    private BigDecimal bidStep;
    private OffsetDateTime createdAt;
    private OffsetDateTime startTime;
    private OffsetDateTime endTime;
    private Long winnerId;
    private String status;

    public Auction(
            long id,
            long itemId,
            BigDecimal startingPrice,
            BigDecimal currentPrice,
            BigDecimal bidStep,
            OffsetDateTime createdAt,
            OffsetDateTime startTime,
            OffsetDateTime endTime,
            Long winnerId,
            String status
    ) {
        this.id = id;
        this.itemId = itemId;
        this.startingPrice = startingPrice;
        this.currentPrice = currentPrice;
        this.bidStep = bidStep;
        this.createdAt = createdAt;
        this.startTime = startTime;
        this.endTime = endTime;
        this.winnerId = winnerId;
        this.status = status;
    }

    public long getId() {
        return id;
    }

    public long getItemId() {
        return itemId;
    }

    public BigDecimal getStartingPrice() {
        return startingPrice;
    }

    public BigDecimal getCurrentPrice() {
        return currentPrice;
    }

    public BigDecimal getBidStep() {
        return bidStep;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public OffsetDateTime getStartTime() {
        return startTime;
    }

    public OffsetDateTime getEndTime() {
        return endTime;
    }

    public Long getWinnerId() {
        return winnerId;
    }

    public String getStatus() {
        return status;
    }
}
