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

@WebServlet("/login")
public class LoginServlet extends HttpServlet {

    private final UserService userService = new UserService();
    private static final Logger logger = LogManager.getLogger(LoginServlet.class);


    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        if ("true".equals(request.getParameter("success"))) {
            request.setAttribute("success", "Вы успешно вошли в аккаунт!");
        }

        if ("true".equals(request.getParameter("logout"))) {
            request.setAttribute("success", "Вы вышли из аккаунта.");
        }

        request.getRequestDispatcher("/template/login.jsp")
                .forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        String username = request.getParameter("username");
        String password = request.getParameter("password");

        username = username != null ? username.trim() : "";

        request.setAttribute("username", username);

        if (username.isEmpty() || password == null || password.isEmpty()) {
            showError(request, response, "Заполните все поля.");
            return;
        }

        try {
            Optional<User> userOptional =
                    userService.authenticate(username, password);

            if (userOptional.isEmpty()) {
                showError(request, response,
                        "Неверный логин или пароль.");
                return;
            }

            User user = userOptional.get();

            // Удаляем старую сессию, если она существует
            HttpSession oldSession = request.getSession(false);

            if (oldSession != null) {
                oldSession.invalidate();
            }

            // Создаём новую сессию
            HttpSession session = request.getSession(true);

            session.setAttribute("userId", user.getId());
            session.setAttribute("username", user.getUsername());
            session.setAttribute("roleId", user.getRoleId());

            response.sendRedirect(
                    request.getContextPath() + "/profile"
            );

        } catch (SQLException e) {

            logger.error("Ошибка БД при авторизации пользователя {}", username, e);
            showError(request, response,
                    "Не удалось выполнить вход. Попробуйте позже.");
        }
    }

    private void showError(
            HttpServletRequest request,
            HttpServletResponse response,
            String message
    ) throws ServletException, IOException {

        request.setAttribute("error", message);

        request.getRequestDispatcher("/template/login.jsp")
                .forward(request, response);
    }
}