package ru.auctionhub.servlet;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.auctionhub.model.User;
import ru.auctionhub.service.UserService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Optional;

@WebServlet("/profile")
public class ProfileServlet extends HttpServlet {

    private final UserService userService = new UserService();
    private static final Logger logger = LogManager.getLogger(ProfileServlet.class);


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
            Optional<User> userOptional = userService.findById(userId);

            if (userOptional.isEmpty()) {
                session.invalidate();
                response.sendRedirect(request.getContextPath() + "/login");
                return;
            }

            request.setAttribute("user", userOptional.get());

            request.getRequestDispatcher("/template/profile.jsp")
                    .forward(request, response);

        } catch (SQLException e) {
            logger.error("Ошибка загрузки профиля пользователя ID={}", userId, e);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
}