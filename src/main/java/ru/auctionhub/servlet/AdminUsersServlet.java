package ru.auctionhub.servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import ru.auctionhub.model.User;
import ru.auctionhub.service.UserService;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

@WebServlet("/admin/users")
public class AdminUsersServlet extends HttpServlet {

    private final UserService userService = new UserService();

    @Override
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
            throws ServletException, IOException {

        try {
            List<User> users = userService.findAll();

            request.setAttribute("users", users);

        } catch (SQLException e) {
            throw new ServletException(
                    "Ошибка при загрузке пользователей", e
            );
        }

        HttpSession session = request.getSession();

        String message = (String) session.getAttribute("userMessage");
        String messageType = (String) session.getAttribute("userMessageType");

        request.setAttribute("userMessage", message);
        request.setAttribute("userMessageType", messageType);

        session.removeAttribute("userMessage");
        session.removeAttribute("userMessageType");

        request.setAttribute(
                "currentUserId",
                session.getAttribute("userId")
        );

        request.getRequestDispatcher("/template/admin-users.jsp")
                .forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response)
            throws IOException {

        HttpSession session = request.getSession();

        try {
            long userId = Long.parseLong(
                    request.getParameter("userId")
            );

            long roleId = Long.parseLong(
                    request.getParameter("roleId")
            );

            Object currentUserIdObject = session.getAttribute("userId");

            if (!(currentUserIdObject instanceof Number)) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN);
                return;
            }

            long currentUserId =
                    ((Number) currentUserIdObject).longValue();

            if (userId == currentUserId) {
                session.setAttribute(
                        "userMessage",
                        "Нельзя изменить собственную роль."
                );
                session.setAttribute("userMessageType", "error");

            } else {
                boolean updated = userService.updateRole(userId, roleId);

                if (updated) {
                    session.setAttribute(
                            "userMessage",
                            "Роль пользователя успешно изменена."
                    );
                    session.setAttribute("userMessageType", "success");

                } else {
                    session.setAttribute(
                            "userMessage",
                            "Пользователь не найден."
                    );
                    session.setAttribute("userMessageType", "error");
                }
            }

        } catch (IllegalArgumentException e) {
            session.setAttribute(
                    "userMessage",
                    "Некорректные данные пользователя."
            );
            session.setAttribute("userMessageType", "error");

        } catch (SQLException e) {
            session.setAttribute(
                    "userMessage",
                    "Не удалось изменить роль пользователя."
            );
            session.setAttribute("userMessageType", "error");
        }

        response.sendRedirect(
                request.getContextPath() + "/admin/users"
        );
    }
}


