/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package spay.models;

import java.util.stream.Stream;
import jakarta.enterprise.context.Dependent;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

/**
 *
 * @author koduki
 */
@Dependent
public class UserDao {

    @PersistenceContext
    private EntityManager em;

    public User persist(User user) {
        em.persist(user);

        return user;
    }

    public Stream<User> stream() {
        return em
                .createQuery("SELECT u FROM User u", User.class)
                .getResultList().stream();
    }

}