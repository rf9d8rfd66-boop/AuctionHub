package ru.auctionhub.servlet;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.auctionhub.model.Bid;
import ru.auctionhub.service.BidService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

@WebServlet("/my-bids")
public class MyBidsServlet extends HttpServlet {

    private final BidService bidService = new BidService();
    private static final Logger logger = LogManager.getLogger(MyBidsServlet.class);


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
            List<Bid> bids = bidService.findByUserId(userId);

            request.setAttribute("bids", bids);

            request.getRequestDispatcher("/template/my-bids.jsp")
                    .forward(request, response);

        } catch (SQLException e) {
            logger.error("Ошибка загрузки ставок пользователя ID={}", userId, e);            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
}