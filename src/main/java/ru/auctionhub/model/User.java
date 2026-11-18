package ru.auctionhub.model;

import java.time.LocalDateTime;

public class User {

    private long id;
    private String username;
    private String email;
    private String phone;
    private String password;
    private long roleId;
    private LocalDateTime createdAt;

    public User(long id, String username, String email, String phone,
                String password, long roleId, LocalDateTime createdAt) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.phone = phone;
        this.password = password;
        this.roleId = roleId;
        this.createdAt = createdAt;
    }

    public long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    public String getPassword() {
        return password;
    }

    public long getRoleId() {
        return roleId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}