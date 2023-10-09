package com.example.demo.services;

import com.example.demo.dao.UserDAO;
import com.example.demo.entities.User;
import com.example.demo.exceptions.ResourceNotFoundException;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    UserDAO userDAO;

    @Autowired
    SessionFactory sessionFactory;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Override
    public List<User> findAll() {
        return userDAO.findAll();
    }

    @Override
    public User getById(UUID id) {
        final User user = userDAO.getById(id);
        if (user == null) {
            throw new ResourceNotFoundException("User id not found - " + id);
        } else {
            return user;
        }
    }

    @Transactional
    @Override
    public User create(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userDAO.save(user);
        return user;
    }

    @Transactional
    @Override
    public User update(User user) {
        final User savedUser = this.getById(user.getId());

        savedUser.setLogin(user.getLogin());
        savedUser.setName(user.getName());

        userDAO.save(savedUser);

        return savedUser;
    }

}
