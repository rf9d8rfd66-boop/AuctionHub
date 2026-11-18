package ru.auctionhub.servlet;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.auctionhub.model.FavoriteAuction;
import ru.auctionhub.service.FavoriteService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

@WebServlet("/my-favorites")
public class MyFavoritesServlet extends HttpServlet {

    private final FavoriteService favoriteService = new FavoriteService();
    private static final Logger logger = LogManager.getLogger(MyFavoritesServlet.class);


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
            List<FavoriteAuction> favoriteAuctions =
                    favoriteService.findAuctionsByUserId(userId);

            request.setAttribute("favoriteAuctions", favoriteAuctions);

            request.getRequestDispatcher("/template/my-favorites.jsp")
                    .forward(request, response);

        } catch (SQLException e) {
            logger.error("Ошибка загрузки избранного пользователя ID={}", userId, e);            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
}