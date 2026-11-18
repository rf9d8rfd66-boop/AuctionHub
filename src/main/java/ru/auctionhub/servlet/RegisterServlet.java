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

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.Locale;

@WebServlet("/register")
public class RegisterServlet extends HttpServlet {

    private final UserService userService = new UserService();
    private static final Logger logger = LogManager.getLogger(RegisterServlet.class);


    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        if ("true".equals(request.getParameter("success"))) {
            request.setAttribute(
                    "success",
                    "Регистрация прошла успешно!"
            );
        }

        request.getRequestDispatcher("/template/register.jsp")
                .forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        String username = request.getParameter("username");
        String email = request.getParameter("email");
        String phone = request.getParameter("phone");
        String password = request.getParameter("password");
        String confirmPassword = request.getParameter("confirmPassword");

        // Убираем пробелы по краям
        username = username != null ? username.trim() : "";
        email = email != null ? email.trim().toLowerCase(Locale.ROOT) : "";
        phone = phone != null ? phone.trim() : "";

        request.setAttribute("username", username);
        request.setAttribute("email", email);
        request.setAttribute("phone", phone);


        // Проверяем обязательные поля
        if (username.isEmpty() || email.isEmpty()
                || password == null || password.isEmpty()
                || confirmPassword == null || confirmPassword.isEmpty()) {

            showError(request, response, "Заполните все обязательные поля.");
            return;
        }

        // Проверяем длину логина
        if (username.length() < 3 || username.length() > 50) {
            showError(request, response,
                    "Логин должен содержать от 3 до 50 символов.");
            return;
        }

        // Проверяем длину email
        if (email.length() > 100 || !email.matches("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$")) {
            showError(request, response, "Введите корректный email.");
            return;
        }

        // Проверяем телефон
        if (phone.length() > 20) {
            showError(request, response, "Слишком длинный номер телефона.");
            return;
        }

        // Проверяем пароль
        if (password.length() < 8) {
            showError(request, response,
                    "Пароль должен содержать минимум 8 символов.");
            return;
        }

        if (password.getBytes(StandardCharsets.UTF_8).length > 72) {
            showError(request, response,
                    "Пароль слишком длинный. Максимум 72 байта UTF-8.");
            return;
        }

        // Проверяем совпадение паролей
        if (!password.equals(confirmPassword)) {
            showError(request, response, "Пароли не совпадают.");
            return;
        }

        try {
            User user = userService.register(
                    username,
                    email,
                    phone.isEmpty() ? null : phone,
                    password
            );

            // После успешной регистрации перенаправляем на GET
            // Авторизуем пользователя сразу после регистрации
            request.getSession().setAttribute("userId", user.getId());
            request.getSession().setAttribute("username", user.getUsername());
            request.getSession().setAttribute("roleId", user.getRoleId());

            // Перенаправляем в профиль
            response.sendRedirect(request.getContextPath() + "/profile");

        } catch (IllegalArgumentException e) {

            showError(request, response, e.getMessage());

        } catch (SQLException e) {

            logger.error("Ошибка при регистрации пользователя {}", username, e);
            if ("23505".equals(e.getSQLState())) {
                showError(request, response,
                        "Пользователь с таким логином или email уже существует.");
            } else {
                showError(request, response,
                        "Ошибка при сохранении пользователя: " + e.getMessage());
            }
        }
    }

    private void showError(
            HttpServletRequest request,
            HttpServletResponse response,
            String message
    ) throws ServletException, IOException {

        request.setAttribute("error", message);

        request.getRequestDispatcher("/template/register.jsp")
                .forward(request, response);
    }
}