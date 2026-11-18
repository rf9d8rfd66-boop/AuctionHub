package ru.auctionhub.servlet;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.auctionhub.model.Item;
import ru.auctionhub.model.ItemImage;
import ru.auctionhub.model.Auction;
import ru.auctionhub.service.ItemDeletionService;
import ru.auctionhub.service.ItemService;
import ru.auctionhub.service.ItemImageService;
import ru.auctionhub.service.AuctionService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import ru.auctionhub.util.FileStorage;

import java.io.IOException;
import java.sql.SQLException;
import java.util.*;

@WebServlet("/my-items")
public class MyItemsServlet extends HttpServlet {

    private final ItemService itemService = new ItemService();
    private final ItemImageService itemImageService = new ItemImageService();
    private final AuctionService auctionService = new AuctionService();
    private final ItemDeletionService itemDeletionService =
            new ItemDeletionService();

    private final FileStorage fileStorage = new FileStorage();
    private static final Logger logger = LogManager.getLogger(MyItemsServlet.class);



    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);

        if (session == null || session.getAttribute("userId") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        Long userId = (Long) session.getAttribute("userId");

        try {
            List<Item> items = itemService.findBySellerId(userId);

            Map<Long, List<ItemImage>> itemImages = new HashMap<>();
            Map<Long, Auction> itemAuctions = new HashMap<>();

            Map<Long, Boolean> deletableItemIds = new HashMap<>();

            for (Item item : items) {
                itemImages.put(
                        item.getId(),
                        itemImageService.findByItemId(item.getId())
                );

                Optional<Auction> auction =
                        auctionService.findByItemId(item.getId());

                auction.ifPresent(a -> {

                    itemAuctions.put(item.getId(), a);

                    if (!"ACTIVE".equals(auctionService.getEffectiveStatus(a))) {

                        deletableItemIds.put(item.getId(), true);
                    }
                });
            }

            request.setAttribute("items", items);
            request.setAttribute("itemImages", itemImages);
            request.setAttribute("itemAuctions", itemAuctions);
            request.setAttribute("deletableItemIds", deletableItemIds);

            String deleteMessage =
                    (String) session.getAttribute("itemDeleteMessage");

            if (deleteMessage != null) {
                request.setAttribute("deleteMessage", deleteMessage);
                session.removeAttribute("itemDeleteMessage");
            }

            request.getRequestDispatcher("/template/my-items.jsp")
                    .forward(request, response);

        } catch (SQLException e) {
            logger.error("Ошибка загрузки объявлений продавца ID={}", userId, e);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);

        if (session == null || !(session.getAttribute("userId") instanceof Number)) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        long sellerId = ((Number) session.getAttribute("userId")).longValue();

        try {
            long itemId = Long.parseLong(request.getParameter("itemId"));

            Optional<List<String>> deletedImages =
                    itemDeletionService.deleteItem(itemId, sellerId);

            if (deletedImages.isEmpty()) {

                session.setAttribute(
                        "itemDeleteMessage",
                        "Не удалось удалить объявление. Активный аукцион удалить нельзя."
                );

            } else {

                boolean allImagesDeleted = true;

                for (String imagePath : deletedImages.get()) {
                    try {
                        fileStorage.deleteImage(imagePath);
                    } catch (IOException e) {
                        allImagesDeleted = false;

                        logger.warn("Не удалось удалить файл с диска: {}", imagePath, e);
                    }
                }

                if (allImagesDeleted) {
                    session.setAttribute(
                            "itemDeleteMessage",
                            "Объявление успешно удалено!"
                    );
                logger.info("Продавец ID={} удалил объявление ID={}", sellerId, itemId);
                } else {
                    session.setAttribute(
                            "itemDeleteMessage",
                            "Объявление удалено, но некоторые фотографии не удалось удалить с диска."
                    );
                }
            }

        } catch (NumberFormatException e) {

            session.setAttribute(
                    "itemDeleteMessage",
                    "Некорректный идентификатор объявления."
            );

        } catch (IllegalArgumentException e) {

            session.setAttribute(
                    "itemDeleteMessage",
                    e.getMessage()
            );

        } catch (SQLException e) {

            logger.error("Ошибка БД при удалении объявления ID={}", request.getParameter("itemId"), e);
            session.setAttribute(
                    "itemDeleteMessage",
                    "Ошибка при удалении объявления. Попробуйте позже."
            );
        }

        response.sendRedirect(request.getContextPath() + "/my-items");
    }
}