package ru.auctionhub.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import ru.auctionhub.model.User;
import ru.auctionhub.repository.UserRepository;
import ru.auctionhub.servlet.RegisterServlet;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class UserService {

    private static final long DEFAULT_ROLE_ID = 1;
    private static final Logger logger = LogManager.getLogger(UserService.class);


    private final UserRepository userRepository;

    private final BCryptPasswordEncoder passwordEncoder =
            new BCryptPasswordEncoder(12);

    public UserService() {
        this.userRepository = new UserRepository();
    }

    public Optional<User> findById(long id) throws SQLException {
        return userRepository.findById(id);
    }

    public List<User> findAll() throws SQLException {
        return userRepository.findAll();
    }

    public Optional<User> findByUsername(String username) throws SQLException {
        return userRepository.findByUsername(username);
    }

    public boolean existsByUsername(String username) throws SQLException {
        return userRepository.existsByUsername(username);
    }



    public boolean existsByEmail(String email) throws SQLException {
        return userRepository.existsByEmail(email);
    }

    public User register(String username, String email, String phone, String password)
            throws SQLException {

        if (userRepository.existsByUsername(username)) {
            throw new IllegalArgumentException("Этот логин уже занят");
        }

        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("Этот email уже зарегистрирован");
        }

        String hashedPassword = passwordEncoder.encode(password);

        User user = new User(
                0,
                username,
                email,
                phone,
                hashedPassword,
                DEFAULT_ROLE_ID,
                null
        );

        logger.info("Зарегистрирован новый пользователь: {}", username);

        return userRepository.save(user);
    }

    public Optional<User> authenticate(String username, String password)
            throws SQLException {

        Optional<User> userOptional = userRepository.findByUsername(username);

        // Пользователь с таким логином не найден
        if (userOptional.isEmpty()) {
            return Optional.empty();
        }

        User user = userOptional.get();

        // Проверяем введённый пароль и его хеш из БД
        if (!passwordEncoder.matches(password, user.getPassword())) {
            logger.warn("Неудачная попытка входа (неверный пароль) для логина: {}", username);
            return Optional.empty();
        }

        return Optional.of(user);
    }


    public boolean updateRole(long userId, long roleId)
            throws SQLException {

        if (userId <= 0) {
            throw new IllegalArgumentException("Некорректный ID пользователя");
        }

        if (roleId != 1 && roleId != 2) {
            throw new IllegalArgumentException("Некорректная роль");
        }

        return userRepository.updateRole(userId, roleId);
    }
}