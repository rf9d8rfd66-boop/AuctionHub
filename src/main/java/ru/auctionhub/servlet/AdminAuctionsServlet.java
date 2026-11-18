package ru.auctionhub.servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import jakarta.servlet.http.HttpSession;
import ru.auctionhub.model.AdminAuctionView;
import ru.auctionhub.model.Auction;
import ru.auctionhub.model.Item;
import ru.auctionhub.model.User;
import ru.auctionhub.service.AuctionService;
import ru.auctionhub.service.ItemService;
import ru.auctionhub.service.UserService;

import java.io.IOException;
import java.sql.SQLException;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@WebServlet("/admin/auctions")
public class AdminAuctionsServlet extends HttpServlet {

    private final AuctionService auctionService = new AuctionService();
    private final ItemService itemService = new ItemService();
    private final UserService userService = new UserService();

    private static final DateTimeFormatter DATE_FORMATTER =
            DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

    private static final ZoneId MOSCOW_ZONE =
            ZoneId.of("Europe/Moscow");

    @Override
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
            throws ServletException, IOException {

        String selectedStatus = request.getParameter("status");

        if (selectedStatus == null || selectedStatus.isBlank()) {
            selectedStatus = "ALL";
        }

        List<AdminAuctionView> auctionViews = new ArrayList<>();

        try {
            List<Auction> auctions = auctionService.findAll();

            for (Auction auction : auctions) {

                String effectiveStatus =
                        auctionService.getEffectiveStatus(auction);

                if (!"ALL".equals(selectedStatus)
                        && !selectedStatus.equals(effectiveStatus)) {
                    continue;
                }

                Optional<Item> itemOptional =
                        itemService.findById(auction.getItemId());

                if (itemOptional.isEmpty()) {
                    continue;
                }

                Item item = itemOptional.get();

                Optional<User> sellerOptional =
                        userService.findById(item.getSellerId());

                String sellerUsername = sellerOptional
                        .map(User::getUsername)
                        .orElse("Неизвестен");

                String formattedStartTime = auction.getStartTime()
                        .atZoneSameInstant(MOSCOW_ZONE)
                        .format(DATE_FORMATTER);

                String formattedEndTime = auction.getEndTime()
                        .atZoneSameInstant(MOSCOW_ZONE)
                        .format(DATE_FORMATTER);

                auctionViews.add(new AdminAuctionView(
                        auction,
                        item.getTitle(),
                        item.getCity(),
                        sellerUsername,
                        effectiveStatus,
                        formattedStartTime,
                        formattedEndTime
                ));
            }

        } catch (SQLException e) {
            throw new ServletException(
                    "Ошибка при загрузке аукционов", e
            );
        }

        request.setAttribute("auctions", auctionViews);
        request.setAttribute("selectedStatus", selectedStatus);

        HttpSession session = request.getSession();

        String message = (String) session.getAttribute("auctionMessage");
        String messageType = (String) session.getAttribute("auctionMessageType");

        request.setAttribute("auctionMessage", message);
        request.setAttribute("auctionMessageType", messageType);

        session.removeAttribute("auctionMessage");
        session.removeAttribute("auctionMessageType");

        request.getRequestDispatcher("/template/admin-auctions.jsp")
                .forward(request, response);


    }

    @Override
    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response)
            throws IOException {

        HttpSession session = request.getSession();

        String action = request.getParameter("action");

        try {
            if ("cancel".equals(action)) {

                long auctionId = Long.parseLong(
                        request.getParameter("auctionId")
                );

                boolean cancelled =
                        auctionService.cancelIfActiveOrUpcoming(auctionId);

                if (cancelled) {
                    session.setAttribute(
                            "auctionMessage",
                            "Аукцион успешно отменён."
                    );
                    session.setAttribute("auctionMessageType", "success");
                } else {
                    session.setAttribute(
                            "auctionMessage",
                            "Не удалось отменить аукцион. Возможно, он уже завершён или отменён."
                    );
                    session.setAttribute("auctionMessageType", "error");
                }

            } else {
                session.setAttribute(
                        "auctionMessage",
                        "Неизвестное действие."
                );
                session.setAttribute("auctionMessageType", "error");
            }

        } catch (IllegalArgumentException e) {
            session.setAttribute(
                    "auctionMessage",
                    "Некорректный ID аукциона."
            );
            session.setAttribute("auctionMessageType", "error");

        } catch (SQLException e) {
            session.setAttribute(
                    "auctionMessage",
                    "Ошибка при отмене аукциона."
            );
            session.setAttribute("auctionMessageType", "error");
        }

        response.sendRedirect(
                request.getContextPath() + "/admin/auctions"
        );
    }
}