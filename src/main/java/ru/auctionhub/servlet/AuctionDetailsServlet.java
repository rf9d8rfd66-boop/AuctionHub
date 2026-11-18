package ru.auctionhub.servlet;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.auctionhub.model.*;

import ru.auctionhub.service.*;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@WebServlet("/auction/view")
public class AuctionDetailsServlet extends HttpServlet {

    private final AuctionService auctionService = new AuctionService();
    private final ItemService itemService = new ItemService();
    private final ItemImageService itemImageService = new ItemImageService();
    private final UserService userService = new UserService();
    private final BidService bidService = new BidService();
    private final FavoriteService favoriteService = new FavoriteService();
    private static final Logger logger = LogManager.getLogger(AuctionDetailsServlet.class);

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String auctionIdString = request.getParameter("id");

        if (auctionIdString == null || auctionIdString.isBlank()) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        try {
            long auctionId = Long.parseLong(auctionIdString);

            if (auctionId <= 0) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST);
                return;
            }

            Optional<Auction> auctionOptional =
                    auctionService.findById(auctionId);

            if (auctionOptional.isEmpty()) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
                return;
            }

            Auction auction = auctionOptional.get();

            ZoneId moscowZone = ZoneId.of("Europe/Moscow");

            DateTimeFormatter dateFormatter =
                    DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

            request.setAttribute(
                    "formattedStartTime",
                    auction.getStartTime()
                            .atZoneSameInstant(moscowZone)
                            .format(dateFormatter)
            );

            request.setAttribute(
                    "formattedEndTime",
                    auction.getEndTime()
                            .atZoneSameInstant(moscowZone)
                            .format(dateFormatter)
            );

            // Получаем товар, связанный с аукционом
            Optional<Item> itemOptional =
                    itemService.findById(auction.getItemId());

            if (itemOptional.isEmpty()) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
                return;
            }

            Item item = itemOptional.get();

            // Получаем фотографии товара
            List<ItemImage> images =
                    itemImageService.findByItemId(item.getId());

            // Получаем информацию о продавце
            Optional<User> sellerOptional =
                    userService.findById(item.getSellerId());

            if (sellerOptional.isEmpty()) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
                return;
            }

            User seller = sellerOptional.get();

            // Определяем текущего пользователя
            Long currentUserId = null;

            if (request.getSession(false) != null) {
                currentUserId = (Long) request.getSession(false)
                        .getAttribute("userId");
            }

// Получаем актуальный статус аукциона
            String effectiveStatus = auctionService.getEffectiveStatus(auction);

// Проверяем, является ли пользователь продавцом или победителем
            boolean isSeller = currentUserId != null
                    && currentUserId.longValue() == seller.getId();

            boolean isWinner = currentUserId != null
                    && auction.getWinnerId() != null
                    && currentUserId.equals(auction.getWinnerId());

// Пользователь, чьи контакты можно показать
            User contactUser = null;
            String contactRole = null;

// Контакты доступны только после завершения аукциона
            if ("COMPLETED".equals(effectiveStatus)) {

                if (isSeller && auction.getWinnerId() != null) {

                    // Продавец видит контакты победителя
                    Optional<User> winnerOptional =
                            userService.findById(auction.getWinnerId());

                    if (winnerOptional.isPresent()) {
                        contactUser = winnerOptional.get();
                        contactRole = "Победитель";
                    }

                } else if (isWinner) {

                    // Победитель видит контакты продавца
                    contactUser = seller;
                    contactRole = "Продавец";
                }
            }

            request.setAttribute("contactUser", contactUser);
            request.setAttribute("contactRole", contactRole);

            request.setAttribute("auction", auction);
            request.setAttribute(
                    "effectiveStatus",
                    effectiveStatus
            );
            request.setAttribute("item", item);
            request.setAttribute("images", images);
            request.setAttribute("seller", seller);
            List<Bid> bids = bidService.findByAuctionId(auctionId);
            request.setAttribute("bids", bids);


            // Минимальная допустимая ставка
            BigDecimal minimumBid = auction.getCurrentPrice()
                    .add(auction.getBidStep());

            request.setAttribute("minimumBid", minimumBid);

// Проверяем актуальный статус
            effectiveStatus = auctionService.getEffectiveStatus(auction);

            // Передаём время для обратного отсчёта
            if ("UPCOMING".equals(effectiveStatus)) {

                request.setAttribute(
                        "countdownTarget",
                        auction.getStartTime().toInstant().toEpochMilli()
                );

                request.setAttribute(
                        "countdownLabel",
                        "До начала торгов:"
                );

            } else if ("ACTIVE".equals(effectiveStatus)) {

                request.setAttribute(
                        "countdownTarget",
                        auction.getEndTime().toInstant().toEpochMilli()
                );

                request.setAttribute(
                        "countdownLabel",
                        "До окончания торгов:"
                );
            }

            request.setAttribute("effectiveStatus", effectiveStatus);


            boolean isFavorite = currentUserId != null
                    && favoriteService.existsByUserIdAndAuctionId(
                    currentUserId, auction.getId()
            );

            request.setAttribute("isFavorite", isFavorite);

            boolean canBid = currentUserId != null
                    && currentUserId != seller.getId()
                    && "ACTIVE".equals(effectiveStatus);

            request.setAttribute("canBid", canBid);

// Получаем сообщение о результате ставки
            if (request.getSession(false) != null) {
                var session = request.getSession(false);

                request.setAttribute("bidMessage", session.getAttribute("bidMessage"));
                request.setAttribute("bidSuccess", session.getAttribute("bidSuccess"));

                session.removeAttribute("bidMessage");
                session.removeAttribute("bidSuccess");
            }



            request.getRequestDispatcher("/template/auction-details.jsp")
                    .forward(request, response);

        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);

        } catch (SQLException e) {
            logger.error("Ошибка загрузки страницы аукциона ID={}", auctionIdString, e);            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
}