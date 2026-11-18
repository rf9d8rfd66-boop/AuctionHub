package ru.auctionhub.servlet;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.auctionhub.listener.ApplicationListener;
import ru.auctionhub.model.Category;
import ru.auctionhub.service.CategoryService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

@WebServlet("/admin/categories")
public class AdminCategoriesServlet extends HttpServlet {

    private final CategoryService categoryService = new CategoryService();
    private static final Logger logger = LogManager.getLogger(AdminCategoriesServlet.class);


    @Override
    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws ServletException, IOException {

        try {
            List<Category> categories = categoryService.findAll();

            request.setAttribute("categories", categories);

            HttpSession session = request.getSession();

            request.setAttribute(
                    "message",
                    session.getAttribute("categoryMessage")
            );

            request.setAttribute(
                    "messageType",
                    session.getAttribute("categoryMessageType")
            );

            session.removeAttribute("categoryMessage");
            session.removeAttribute("categoryMessageType");

            request.getRequestDispatcher(
                    "/template/admin-categories.jsp"
            ).forward(request, response);

        } catch (SQLException e) {
            throw new ServletException(
                    "Ошибка при загрузке категорий", e
            );
        }
    }

    @Override
    protected void doPost(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {

        request.setCharacterEncoding("UTF-8");

        String action = request.getParameter("action");

        HttpSession session = request.getSession();

        try {

            if ("add".equals(action)) {

                String name = request.getParameter("name");

                categoryService.add(name);

                setMessage(
                        session,
                        "Категория успешно добавлена",
                        "success"
                );

            } else if ("delete".equals(action)) {

                long id = Long.parseLong(
                        request.getParameter("categoryId")
                );

                boolean deleted = categoryService.deleteIfUnused(id);

                if (deleted) {
                    setMessage(
                            session,
                            "Категория успешно удалена",
                            "success"
                    );
                } else {
                    setMessage(
                            session,
                            "Нельзя удалить категорию: к ней привязаны товары",
                            "error"
                    );
                }

            } else if ("update".equals(action)) {
                long id = Long.parseLong(request.getParameter("categoryId"));
                String name = request.getParameter("name");
                categoryService.update(id, name);
                setMessage(session, "Категория обновлена", "success");
            }

            else {
                setMessage(
                        session,
                        "Неизвестное действие",
                        "error"
                );
            }

        } catch (IllegalArgumentException e) {

            setMessage(session, e.getMessage(), "error");

        } catch (SQLException e) {

            logger.error("Ошибка управления категориями", e);
            setMessage(
                    session,
                    "Не удалось выполнить операцию. Возможно, категория с таким названием уже существует.",
                    "error"
            );
        }

        response.sendRedirect(
                request.getContextPath() + "/admin/categories"
        );
    }

    private void setMessage(
            HttpSession session,
            String message,
            String type
    ) {
        session.setAttribute("categoryMessage", message);
        session.setAttribute("categoryMessageType", type);
    }


}