package ru.auctionhub.service;

import ru.auctionhub.model.AuctionCatalogItem;
import ru.auctionhub.model.CatalogFilter;
import ru.auctionhub.repository.CatalogRepository;

import java.sql.SQLException;
import java.util.List;

public class CatalogService {

    private final CatalogRepository catalogRepository = new CatalogRepository();

    public List<AuctionCatalogItem> findAll(String title) throws SQLException {
        return catalogRepository.findAll(title);
    }

    public List<AuctionCatalogItem> findAll(CatalogFilter filter) throws SQLException {
        return catalogRepository.findAll(filter);
    }

    public List<AuctionCatalogItem> findAll() throws SQLException {
        return catalogRepository.findAll();
    }
}
