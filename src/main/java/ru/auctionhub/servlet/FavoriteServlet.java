package ru.auctionhub.servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import ru.auctionhub.service.FavoriteService;

import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/favorites")
public class FavoriteServlet extends HttpServlet {

    private final FavoriteService favoriteService = new FavoriteService();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        Long userId = (Long) request.getSession().getAttribute("userId");

        try {
            long auctionId = Long.parseLong(request.getParameter("auctionId"));
            String action = request.getParameter("action");

            if ("add".equals(action)) {
                favoriteService.add(userId, auctionId);
            } else if ("remove".equals(action)) {
                favoriteService.remove(userId, auctionId);
            } else {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST);
                return;
            }

            String returnTo = request.getParameter("returnTo");

            if ("profile".equals(returnTo)) {
                response.sendRedirect(
                        request.getContextPath() + "/profile"
                );
            } else if ("my-favorites".equals(returnTo)){
                response.sendRedirect(
                        request.getContextPath() + "/my-favorites"
                );
            } else {
                response.sendRedirect(
                        request.getContextPath() + "/auction/view?id=" + auctionId
                );
            }
        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
        } catch (SQLException e) {
            throw new ServletException("Ошибка при изменении избранного", e);
        }
    }
}
