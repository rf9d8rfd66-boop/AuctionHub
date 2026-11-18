package ru.auctionhub.model;

public class Favorite {

    private long userId;
    private long auctionId;

    public Favorite(long userId, long auctionId) {
        this.userId = userId;
        this.auctionId = auctionId;
    }

    public long getUserId() {
        return userId;
    }

    public long getAuctionId() {
        return auctionId;
    }
}