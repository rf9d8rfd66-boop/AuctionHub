package ru.auctionhub.util;

import jakarta.servlet.http.Part;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;
import java.util.Set;
import java.util.UUID;

public class FileStorage {
    private static final Path UPLOAD_DIRECTORY;

    static {
        Properties props = new Properties();
        try (InputStream is = FileStorage.class.getClassLoader()
                .getResourceAsStream("application.properties")) {
            if (is == null) {
                throw new RuntimeException("Файл application.properties не найден");
            }
            props.load(is);
            UPLOAD_DIRECTORY = Path.of(props.getProperty("upload.dir"));
        } catch (IOException e) {
            throw new RuntimeException("Ошибка чтения application.properties", e);
        }
    }

    public static Path getUploadDirectory() {
        return UPLOAD_DIRECTORY;
    }

    private static final Set<String> ALLOWED_TYPES = Set.of(
            "image/jpeg",
            "image/png",
            "image/webp"
    );

    public String saveImage(Part part) throws IOException {
        if (part == null || part.getSize() == 0) {
            return null;
        }
        String contentType = part.getContentType();
        if (contentType == null || !ALLOWED_TYPES.contains(contentType)) {
            throw new IllegalArgumentException(
                    "Можно загружать только JPG, PNG и WEBP"
            );
        }
        String extension = switch (contentType) {
            case "image/jpeg" -> ".jpg";
            case "image/png" -> ".png";
            case "image/webp" -> ".webp";
            default -> throw new IllegalArgumentException("Недопустимый формат изображения");
        };
        Files.createDirectories(UPLOAD_DIRECTORY);
        String fileName = UUID.randomUUID() + extension;
        Path filePath = UPLOAD_DIRECTORY.resolve(fileName);
        try (InputStream inputStream = part.getInputStream()) {
            Files.copy(inputStream, filePath);
        }
        return "uploads/" + fileName;
    }

    public void deleteImage(String relativePath) throws IOException {
        if (relativePath == null || !relativePath.startsWith("uploads/")) {
            return;
        }
        String fileName = relativePath.substring("uploads/".length());
        if (fileName.contains("/") || fileName.contains("\\")) {
            return;
        }
        Files.deleteIfExists(UPLOAD_DIRECTORY.resolve(fileName));
    }
}