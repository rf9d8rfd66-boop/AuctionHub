package ru.auctionhub.service;

import ru.auctionhub.model.Favorite;
import ru.auctionhub.model.FavoriteAuction;
import ru.auctionhub.repository.FavoriteRepository;

import java.sql.SQLException;
import java.util.List;

public class FavoriteService {

    private final FavoriteRepository favoriteRepository;

    public FavoriteService() {
        this.favoriteRepository = new FavoriteRepository();
    }

    public List<Favorite> findByUserId(long userId) throws SQLException {
        return favoriteRepository.findByUserId(userId);
    }

    public boolean existsByUserIdAndAuctionId(long userId, long auctionId)
            throws SQLException {

        return favoriteRepository.existsByUserIdAndAuctionId(userId, auctionId);
    }

    public void add(long userId, long auctionId) throws SQLException {
        favoriteRepository.add(userId, auctionId);
    }

    public void remove(long userId, long auctionId) throws SQLException {
        favoriteRepository.remove(userId, auctionId);
    }

    public List<FavoriteAuction> findAuctionsByUserId(long userId)
            throws SQLException {

        return favoriteRepository.findAuctionsByUserId(userId);
    }

}