package ru.auctionhub.service;

import ru.auctionhub.model.Bid;
import ru.auctionhub.repository.BidRepository;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class BidService {

    private final BidRepository bidRepository;

    public BidService() {
        this.bidRepository = new BidRepository();
    }

    public Optional<Bid> findById(long id) throws SQLException {
        return bidRepository.findById(id);
    }

    public List<Bid> findByAuctionId(long auctionId) throws SQLException {
        return bidRepository.findByAuctionId(auctionId);
    }

    public List<Bid> findByUserId(long userId) throws SQLException {
        return bidRepository.findByUserId(userId);
    }

    public void placeBid(long auctionId, long userId, BigDecimal amount)
            throws SQLException {

        if (amount == null || amount.signum() <= 0) {
            throw new IllegalArgumentException("Сумма ставки должна быть положительной");
        }

        if (amount.scale() > 2) {
            throw new IllegalArgumentException(
                    "Сумма ставки не может содержать больше двух знаков после запятой");
        }

        bidRepository.placeBid(auctionId, userId, amount);
    }
}