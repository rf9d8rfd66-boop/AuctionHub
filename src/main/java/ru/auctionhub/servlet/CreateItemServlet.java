package ru.auctionhub.servlet;

import jakarta.servlet.http.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.auctionhub.model.Category;
import ru.auctionhub.service.CategoryService;
import ru.auctionhub.service.ItemService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import ru.auctionhub.util.FileStorage;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@WebServlet("/item/create")
@MultipartConfig(maxFileSize = 5 * 1024 * 1024,
maxRequestSize = 26*1024*1024)
public class CreateItemServlet extends HttpServlet {

    private final CategoryService categoryService = new CategoryService();
    private final ItemService itemService = new ItemService();
    private final FileStorage fileStorage = new FileStorage();
    private static final Logger logger = LogManager.getLogger(CreateItemServlet.class);


    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            List<Category> categories = categoryService.findAll();
            request.setAttribute("categories", categories);

            if ("true".equals(request.getParameter("success"))) {
                request.setAttribute("success", "Объявление успешно создано!");
            }

            request.getRequestDispatcher("/template/create-item.jsp")
                    .forward(request, response);

        } catch (SQLException e) {
            logger.error("Ошибка загрузки категорий при открытии формы", e);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        List<String> savedImagePaths = new ArrayList<>();
        boolean creationSuccessful = false;

        String title = request.getParameter("title");
        String categoryIdString = request.getParameter("categoryId");
        String description = request.getParameter("description");
        String city = request.getParameter("city");
        String startingPriceString = request.getParameter("startingPrice");
        String bidStepString = request.getParameter("bidStep");

        // Убираем лишние пробелы
        title = title != null ? title.trim() : "";
        city = city != null ? city.trim() : "";
        description = description != null ? description.trim() : "";

        // Сохраняем введённые данные, чтобы не потерять их при ошибке
        request.setAttribute("title", title);
        request.setAttribute("categoryId", categoryIdString);
        request.setAttribute("description", description);
        request.setAttribute("city", city);
        request.setAttribute("startingPrice", startingPriceString);
        request.setAttribute("bidStep", bidStepString);

        long sellerId = 0;
        try {

            Collection<Part> parts = request.getParts();

            List<Part> imageParts = parts.stream()
                    .filter(part -> "images".equals(part.getName()))
                    .filter(part -> part.getSize() > 0)
                    .toList();

            if (imageParts.size() > 5) {
                throw new IllegalArgumentException(
                        "Можно загрузить не более 5 фотографий"
                );
            }

            // Проверяем обязательные поля
            if (title.isEmpty() || categoryIdString == null
                    || city.isEmpty() || startingPriceString == null
                    || bidStepString == null) {

                throw new IllegalArgumentException("Заполните все обязательные поля");
            }

            if (title.length() > 150) {
                throw new IllegalArgumentException("Название не должно превышать 150 символов");
            }

            if (city.length() > 100) {
                throw new IllegalArgumentException("Название города слишком длинное");
            }


            // Преобразуем значения из формы в нужные типы
            long categoryId = Long.parseLong(categoryIdString);
            BigDecimal startingPrice = new BigDecimal(startingPriceString);
            BigDecimal bidStep = new BigDecimal(bidStepString);

            if (categoryId <= 0) {
                throw new IllegalArgumentException("Выберите категорию");
            }

            if (startingPrice.compareTo(BigDecimal.ZERO) <= 0) {
                throw new IllegalArgumentException("Начальная цена должна быть больше нуля");
            }

            if (bidStep.compareTo(BigDecimal.ZERO) <= 0) {
                throw new IllegalArgumentException("Шаг ставки должен быть больше нуля");
            }

            // Получаем ID авторизованного пользователя
            HttpSession session = request.getSession(false);

            if (session == null || !(session.getAttribute("userId") instanceof Number)) {
                response.sendRedirect(request.getContextPath() + "/login");
                return;
            }

            sellerId = ((Number) session.getAttribute("userId")).longValue();

            // Сохраняем фотографии на компьютере
            for (Part imagePart : imageParts) {
                String imagePath = fileStorage.saveImage(imagePart);

                savedImagePaths.add(imagePath);
            }

            // Создаём товар и связанный с ним аукцион
            long auctionId = itemService.createItemAndAuction(
                    sellerId,
                    categoryId,
                    title,
                    description,
                    city,
                    startingPrice,
                    bidStep,
                    savedImagePaths
            );

            creationSuccessful = true;
            logger.info("Пользователь ID={} создал аукцион ID={} (товар: '{}')", sellerId, auctionId, title);

            // Перенаправляем сразу на страницу созданного аукциона
            response.sendRedirect(
                    request.getContextPath() + "/auction/view?id=" + auctionId
            );

        } catch (NumberFormatException e) {
            showError(request, response, "Проверьте правильность введённых чисел");

        } catch (IllegalArgumentException e) {
            showError(request, response, e.getMessage());

        } catch (SQLException e) {
            logger.error("Ошибка создания объявления пользователем ID={}", sellerId, e);
            showError(request, response, "Не удалось создать объявление. Попробуйте позже");
        } finally {
            if (!creationSuccessful) {
                for (String imagePath : savedImagePaths) {
                    try {
                        fileStorage.deleteImage(imagePath);
                    } catch (IOException e) {
                        logger.warn("Не удалось удалить временную фотографию: {}", imagePath, e);
                    }
                }
            }
        }

    }

    private void showError(
            HttpServletRequest request,
            HttpServletResponse response,
            String message
    ) throws ServletException, IOException {

        request.setAttribute("error", message);

        try {
            List<Category> categories = categoryService.findAll();
            request.setAttribute("categories", categories);

        } catch (SQLException e) {
            logger.error("Ошибка загрузки категорий при выводе ошибки", e);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            return;
        }

        request.getRequestDispatcher("/template/create-item.jsp")
                .forward(request, response);
    }
}