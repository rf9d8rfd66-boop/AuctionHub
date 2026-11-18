package ru.auctionhub.listener;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import ru.auctionhub.util.DbConnection;

@WebListener
public class ApplicationListener implements ServletContextListener {

    private static final Logger logger = LogManager.getLogger(ApplicationListener.class);

    @Override
    public void contextInitialized(ServletContextEvent event) {
        try {
            logger.info("Начинаем инициализацию подключения к БД...");
            DbConnection.init();
            logger.info("Подключение к БД успешно инициализировано!");
        } catch (Exception e) {
            logger.error("Ошибка при инициализации БД", e);            e.printStackTrace();

            throw new RuntimeException(e);
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent event) {
        DbConnection.destroy();

        logger.info("Пул соединений закрыт.");    }
}