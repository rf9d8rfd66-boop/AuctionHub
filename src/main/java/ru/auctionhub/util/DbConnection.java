package ru.auctionhub.util;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import javax.sql.DataSource;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

public class DbConnection {

    private static DataSource dataSource;

    public static void init() throws ClassNotFoundException {
        Class.forName("org.postgresql.Driver");
        Properties props = new Properties();

        try (InputStream is = DbConnection.class.getClassLoader().getResourceAsStream("application.properties")) {
            props.load(is);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(props.getProperty("db.url"));
        config.setUsername(props.getProperty("db.username"));
        config.setPassword(props.getProperty("db.password"));
        config.setConnectionTimeout(50000); // в миллисекундах
        config.setMaximumPoolSize(10);
        dataSource = new HikariDataSource(config);
    }
    public static Connection getConnection() throws SQLException, ClassNotFoundException {
        if (dataSource != null) {
            return dataSource.getConnection();
        } else {
            try {
                init();
                return dataSource.getConnection();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static void destroy() {
        ((HikariDataSource)dataSource).close();
    }

}