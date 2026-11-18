package ru.auctionhub.servlet;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.auctionhub.service.BidService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;

@WebServlet("/auction/bid")
public class BidServlet extends HttpServlet {

    private final BidService bidService = new BidService();
    private static final Logger logger = LogManager.getLogger(BidServlet.class);


    @Override
    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        HttpSession session = request.getSession(false);

        if (session == null || session.getAttribute("userId") == null) {
            response.sendRedirect(
                    request.getContextPath() + "/login");
            return;
        }

        long userId = (Long) session.getAttribute("userId");

        String auctionIdParameter = request.getParameter("auctionId");
        String amountParameter = request.getParameter("amount");

        long auctionId;

        try {
            auctionId = Long.parseLong(auctionIdParameter);

            if (auctionId <= 0) {
                throw new NumberFormatException();
            }

        } catch (NumberFormatException | NullPointerException e) {
            session.setAttribute("bidMessage", "Некорректный идентификатор аукциона");
            session.setAttribute("bidSuccess", false);

            response.sendRedirect(request.getContextPath() + "/catalog");
            return;
        }

        try {
            BigDecimal amount = new BigDecimal(amountParameter.trim());

            bidService.placeBid(auctionId, userId, amount);
            logger.info("Пользователь ID={} сделал ставку {} руб. на аукцион ID={}", userId, amount, auctionId);

            session.setAttribute("bidMessage", "Ставка успешно размещена!");
            session.setAttribute("bidSuccess", true);

        } catch (NumberFormatException | NullPointerException e) {

            session.setAttribute("bidMessage", "Введите корректную сумму ставки");
            session.setAttribute("bidSuccess", false);

        } catch (IllegalArgumentException e) {

            session.setAttribute("bidMessage", e.getMessage());
            session.setAttribute("bidSuccess", false);

        } catch (SQLException e) {

            String message = e.getSQLState() == null
                    ? e.getMessage()
                    : "Не удалось разместить ставку. Попробуйте позже.";

            session.setAttribute("bidMessage", message);
            session.setAttribute("bidSuccess", false);
        }

        response.sendRedirect(
                request.getContextPath() + "/auction/view?id=" + auctionId);
    }
}