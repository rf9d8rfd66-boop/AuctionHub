package ru.auctionhub.filter;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import ru.auctionhub.model.User;
import ru.auctionhub.service.UserService;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Optional;

@WebFilter(urlPatterns = {
        "/profile",
        "/favorites",
        "/my-bids",
        "/item/create",
        "/auction/create",
        "/favorites-test",
        "/admin/*",
        "/auction/bid",
        "/my-items",
        "/my-favorites"
        })
public class AuthFilter implements Filter {

    private final UserService userService = new UserService();

    @Override
    public void doFilter(
            ServletRequest request,
            ServletResponse response,
            FilterChain chain
    ) throws IOException, ServletException {


        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        // Получаем существующую сессию
        HttpSession session = httpRequest.getSession(false);

        // Проверяем, авторизован ли пользователь
        if (session == null || session.getAttribute("userId") == null) {

            httpResponse.sendRedirect(
                    httpRequest.getContextPath() + "/login"
            );

            return;
        }

        // Проверяем, является ли пользователь администратором
        String servletPath = httpRequest.getServletPath();

        if (servletPath.equals("/admin") || servletPath.startsWith("/admin/")) {

            Object userIdObject = session.getAttribute("userId");

            if (!(userIdObject instanceof Number)) {
                httpResponse.sendError(
                        HttpServletResponse.SC_FORBIDDEN,
                        "Доступ запрещён"
                );
                return;
            }

            long userId = ((Number) userIdObject).longValue();

            try {
                Optional<User> userOptional = userService.findById(userId);

                if (userOptional.isEmpty()
                        || userOptional.get().getRoleId() != 2) {

                    httpResponse.sendError(
                            HttpServletResponse.SC_FORBIDDEN,
                            "Доступ запрещён"
                    );
                    return;
                }

            } catch (SQLException e) {
                throw new ServletException(
                        "Ошибка проверки прав пользователя", e
                );
            }
        }

        // Если проверки пройдены, передаём запрос дальше
        chain.doFilter(request, response);
    }
}