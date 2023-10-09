package com.example.demo.dao;

import com.example.demo.entities.User;

import java.util.List;
import java.util.UUID;

public interface UserDAO {

    public List<User> findAll();

    public User getById(UUID id);

    public void save(User user);

}
