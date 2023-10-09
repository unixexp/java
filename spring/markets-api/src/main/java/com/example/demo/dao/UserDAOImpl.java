package com.example.demo.dao;

import com.example.demo.entities.User;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public class UserDAOImpl implements UserDAO {

    @Autowired
    SessionFactory sessionFactory;

    @Override
    public List<User> findAll() {
        return sessionFactory.getCurrentSession().createQuery("from User", User.class).list();
    }

    public User getById(UUID id) {
        return sessionFactory.getCurrentSession().get(User.class, id);
    }

    @Override
    public void save(User user) {
        sessionFactory.getCurrentSession().save(user);
    }


}
