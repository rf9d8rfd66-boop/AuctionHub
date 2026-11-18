package ru.auctionhub.model;

public class AdminAuctionView {

    private final Auction auction;
    private final String title;
    private final String city;
    private final String sellerUsername;
    private final String effectiveStatus;
    private final String formattedStartTime;
    private final String formattedEndTime;

    public AdminAuctionView(
            Auction auction,
            String title,
            String city,
            String sellerUsername,
            String effectiveStatus,
            String formattedStartTime,
            String formattedEndTime
    ) {
        this.auction = auction;
        this.title = title;
        this.city = city;
        this.sellerUsername = sellerUsername;
        this.effectiveStatus = effectiveStatus;
        this.formattedStartTime = formattedStartTime;
        this.formattedEndTime = formattedEndTime;
    }

    public Auction getAuction() {
        return auction;
    }

    public String getTitle() {
        return title;
    }

    public String getCity() {
        return city;
    }

    public String getSellerUsername() {
        return sellerUsername;
    }

    public String getEffectiveStatus() {
        return effectiveStatus;
    }

    public String getFormattedStartTime() {
        return formattedStartTime;
    }

    public String getFormattedEndTime() {
        return formattedEndTime;
    }
}