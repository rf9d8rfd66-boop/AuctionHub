package ru.auctionhub.service;

import ru.auctionhub.model.Category;
import ru.auctionhub.repository.CategoryRepository;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryService() {
        this.categoryRepository = new CategoryRepository();
    }

    public List<Category> findAll() throws SQLException {
        return categoryRepository.findAll();
    }

    public Optional<Category> findById(long id) throws SQLException {
        return categoryRepository.findById(id);
    }

    public Category add(String name) throws SQLException {

        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException(
                    "Введите название категории"
            );
        }

        name = name.trim();

        if (name.length() > 100) {
            throw new IllegalArgumentException(
                    "Название категории не должно превышать 100 символов"
            );
        }

        return categoryRepository.add(name);
    }

    public boolean deleteIfUnused(long id) throws SQLException {

        if (id <= 0) {
            throw new IllegalArgumentException("Некорректный ID категории");
        }

        return categoryRepository.deleteIfUnused(id);
    }

    public boolean update(long id, String name) throws SQLException {
        if (id <= 0) throw new IllegalArgumentException("Некорректный ID");
        if (name == null || name.isBlank() || name.trim().length() > 100) {
            throw new IllegalArgumentException("Некорректное название категории");
        }
        return categoryRepository.update(id, name.trim());
    }
}