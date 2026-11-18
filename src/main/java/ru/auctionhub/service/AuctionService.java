package ru.auctionhub.service;

import ru.auctionhub.model.Auction;
import ru.auctionhub.repository.AuctionRepository;

import java.sql.SQLException;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

public class AuctionService {

    private final AuctionRepository auctionRepository;

    public AuctionService() {
        this.auctionRepository = new AuctionRepository();
    }

    public Optional<Auction> findById(long id) throws SQLException {
        return auctionRepository.findById(id);
    }

    public List<Auction> findAll() throws SQLException {
        return auctionRepository.findAll();
    }

    public Optional<Auction> findByItemId(long itemId) throws SQLException {
        return auctionRepository.findByItemId(itemId);
    }

    public String getEffectiveStatus(Auction auction) {

        if ("CANCELLED".equals(auction.getStatus())) {
            return "CANCELLED";
        }

        OffsetDateTime now = OffsetDateTime.now();

        if (now.isBefore(auction.getStartTime())) {
            return "UPCOMING";
        }

        if (now.isBefore(auction.getEndTime())) {
            return "ACTIVE";
        }

        return "COMPLETED";
    }

    public boolean cancelIfActiveOrUpcoming(long auctionId)
            throws SQLException {

        if (auctionId <= 0) {
            throw new IllegalArgumentException("Некорректный ID аукциона");
        }

        return auctionRepository.cancelIfActiveOrUpcoming(auctionId);
    }
}
