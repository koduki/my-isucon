/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package spay.models;

import java.util.stream.Stream;
import javax.enterprise.context.Dependent;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

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

    public User get(long customerNumber) {
        return em
                .createQuery("SELECT u FROM User u WHERE u.customerNumber = :customerNumber", User.class)
                .setParameter("customerNumber", customerNumber)
                .getSingleResult();
    }

}