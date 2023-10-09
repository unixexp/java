package com.example.demo.services;


import com.example.demo.entities.User;

import java.util.List;
import java.util.UUID;

public interface UserService {

    public List<User> findAll();

    public User getById(UUID id);

    public User create(User user);

    public User update(User user);

}
