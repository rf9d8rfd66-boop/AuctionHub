package ru.auctionhub.servlet;

import ru.auctionhub.model.User;
import ru.auctionhub.service.UserService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

@WebServlet("/admin")
public class AdminServlet extends HttpServlet {

    private final UserService userService = new UserService();

    @Override
    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws ServletException, IOException {

        try {
            List<User> users = userService.findAll();

            request.setAttribute("users", users);
            request.setAttribute("usersCount", users.size());

            request.getRequestDispatcher("/template/admin.jsp")
                    .forward(request, response);

        } catch (SQLException e) {
            throw new ServletException(
                    "Ошибка при загрузке административной панели", e
            );
        }
    }
}