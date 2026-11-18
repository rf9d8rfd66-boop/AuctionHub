package ru.auctionhub.servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ru.auctionhub.util.FileStorage;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@WebServlet("/images/*")
public class ImageServlet extends HttpServlet {

    private static final Path UPLOAD_DIRECTORY =
            FileStorage.getUploadDirectory();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String fileName = request.getPathInfo();

        if (fileName == null || fileName.equals("/")) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        // Убираем первый символ "/"
        fileName = fileName.substring(1);

        // Проверяем имя файла
        if (!fileName.matches("[a-fA-F0-9-]+\\.(jpg|png|webp)")) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        Path imagePath = UPLOAD_DIRECTORY.resolve(fileName).normalize();

        if (!imagePath.startsWith(UPLOAD_DIRECTORY)
                || !Files.exists(imagePath)
                || !Files.isRegularFile(imagePath)) {

            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        String contentType = Files.probeContentType(imagePath);

        if (contentType == null) {
            response.sendError(HttpServletResponse.SC_UNSUPPORTED_MEDIA_TYPE);
            return;
        }

        response.setContentType(contentType);
        response.setContentLengthLong(Files.size(imagePath));

        Files.copy(imagePath, response.getOutputStream());
    }
}