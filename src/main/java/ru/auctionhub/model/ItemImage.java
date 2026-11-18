package ru.auctionhub.model;

public class ItemImage {

    private long id;
    private long itemId;
    private String imagePath;

    public ItemImage(long id, long itemId, String imagePath) {
        this.id = id;
        this.itemId = itemId;
        this.imagePath = imagePath;
    }

    public long getId() {
        return id;
    }

    public long getItemId() {
        return itemId;
    }

    public String getImagePath() {
        return imagePath;
    }
}
