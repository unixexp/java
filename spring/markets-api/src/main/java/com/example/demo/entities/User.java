package com.example.demo.entities;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
public class User extends MarketEntity {

    @Column(name = "login", length = 128, nullable = false, unique = true)
    private String login;

    @Column(name = "name", length = 128, nullable = false, unique = false)
    private String name;

    @Column(name = "password", length = 68, nullable = false)
    private String password;

    public User() {}

    public User (String login, String name) {
        this.login = login;
        this.name = name;
    }

    // In mappedBy we defined field 'user' from 'Order' entity
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "user", cascade = {CascadeType.PERSIST})
    private List<Order> orders = new ArrayList<>();

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public List<Order> getOrders() {
        return orders;
    }

    public void addOrder(Order order) {
        this.orders.add(order);
        order.setUser(this);
    }

    public Order deleteOrder(Order order) {
        this.orders.remove(order);
        order.setUser(null);
        return order;
    }

}
