package ru.auctionhub.servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

@WebServlet("/logout")
public class LogoutServlet extends HttpServlet {

     @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Получаем текущую сессию
        HttpSession session = request.getSession(false);

        // Если сессия существует, завершаем её
        if (session != null) {
            session.invalidate();
        }

        // Перенаправляем пользователя на страницу входа
        response.sendRedirect(
                request.getContextPath() + "/login?logout=true"
        );
    }
}
