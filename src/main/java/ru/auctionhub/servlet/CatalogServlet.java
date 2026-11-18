package ru.auctionhub.servlet;

import ru.auctionhub.model.AuctionCatalogItem;
import ru.auctionhub.model.CatalogFilter;
import ru.auctionhub.service.CatalogService;
import ru.auctionhub.service.CategoryService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;
import java.util.Set;

@WebServlet("/catalog")
public class CatalogServlet extends HttpServlet {

    private final CatalogService catalogService = new CatalogService();
    private final CategoryService categoryService = new CategoryService();

    private static final Set<String> ALLOWED_STATUSES =
            Set.of("UPCOMING", "ACTIVE", "COMPLETED", "CANCELLED");

    private static final Set<String> ALLOWED_SORTS =
            Set.of("newest", "ending_soon", "price_asc", "price_desc");

    @Override
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
            throws ServletException, IOException {

        try {
            CatalogFilter filter = new CatalogFilter();

            // Поиск по названию
            filter.setTitle(request.getParameter("q"));

            // Фильтр по категории
            String categoryId = request.getParameter("categoryId");

            if (categoryId != null && !categoryId.isBlank()) {
                try {
                    long id = Long.parseLong(categoryId);

                    if (id > 0) {
                        filter.setCategoryId(id);
                    }
                } catch (NumberFormatException e) {
                    request.setAttribute("filterError",
                            "Некорректный идентификатор категории");
                }
            }

            // Фильтр по городу
            filter.setCity(request.getParameter("city"));

            // Минимальная цена
            String minPrice = request.getParameter("minPrice");

            if (minPrice != null && !minPrice.isBlank()) {
                try {
                    BigDecimal price = new BigDecimal(minPrice);

                    if (price.signum() >= 0) {
                        filter.setMinPrice(price);
                    } else {
                        request.setAttribute("filterError",
                                "Цена не может быть отрицательной");
                    }
                } catch (NumberFormatException e) {
                    request.setAttribute("filterError",
                            "Некорректная минимальная цена");
                }
            }

            // Максимальная цена
            String maxPrice = request.getParameter("maxPrice");

            if (maxPrice != null && !maxPrice.isBlank()) {
                try {
                    BigDecimal price = new BigDecimal(maxPrice);

                    if (price.signum() >= 0) {
                        filter.setMaxPrice(price);
                    } else {
                        request.setAttribute("filterError",
                                "Цена не может быть отрицательной");
                    }
                } catch (NumberFormatException e) {
                    request.setAttribute("filterError",
                            "Некорректная максимальная цена");
                }
            }

            // Проверяем диапазон цен
            if (filter.getMinPrice() != null
                    && filter.getMaxPrice() != null
                    && filter.getMinPrice().compareTo(filter.getMaxPrice()) > 0) {

                request.setAttribute("filterError",
                        "Минимальная цена не может быть больше максимальной");

                filter.setMinPrice(null);
                filter.setMaxPrice(null);
            }

            // Фильтр по статусу
            String status = request.getParameter("status");

            if (status != null && ALLOWED_STATUSES.contains(status)) {
                filter.setStatus(status);
            }

            // Сортировка
            String sort = request.getParameter("sort");

            if (sort != null && ALLOWED_SORTS.contains(sort)) {
                filter.setSort(sort);
            } else {
                filter.setSort("newest");
            }

            // Получаем объявления
            List<AuctionCatalogItem> catalogItems =
                    catalogService.findAll(filter);

            // Получаем категории для выпадающего списка
            request.setAttribute("categories", categoryService.findAll());

            request.setAttribute("catalogItems", catalogItems);
            request.setAttribute("filter", filter);

            // Для совместимости с существующей поисковой формой
            request.setAttribute("searchQuery", filter.getTitle());

            request.getRequestDispatcher("/template/catalog.jsp")
                    .forward(request, response);

        } catch (SQLException e) {
            throw new ServletException("Ошибка при загрузке каталога", e);
        }
    }
}



//<c:if test="${not empty auction.countdownTarget}">
//                    <p class="auction-countdown"
//data-target="${auction.countdownTarget}">
//Загрузка таймера...
//                    </p>
//                </c:if>
//
//                <p>
//Окончание:
//                    <c:out value="${auction.formattedEndTime}"/>
//        </p>
//
//        !categoryId.isBlank()
//
//BigDecimal че за тип особенности
//
//if (price.signum() >= 0) че за проверка
//
//request.setAttribute("searchQuery", filter.getTitle()); как это работает