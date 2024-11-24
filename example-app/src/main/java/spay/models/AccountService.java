/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package spay.models;

import java.util.Random;
import javax.enterprise.context.Dependent;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author koduki
 */
@Dependent
public class AccountService {

    @PersistenceContext
    private EntityManager em;

    public User registUser(User user) {
        user.setCustomerNumber(generateCustomerNumber());
        em.persist(user);
        addCard(user.getCustomerNumber());

        return user;
    }

    public User getUser(long customerNumber) {
        User user = em.createQuery("SELECT u FROM User u WHERE u.customerNumber = :customerNumber", User.class)
                .setParameter("customerNumber", customerNumber)
                .getResultList()
                .get(0);
        return user;
    }

    public Card addCard(long customerNumber) {
        User user = getUser(customerNumber);

        Card card = new Card();
        card.setCardNumber(generateCardNumber());
        card.setLimitAmount(generateLimitAmount());
        card.setUsedAmount(0);
        em.persist(card);

        user.getCards().add(card);
        em.persist(user);

        return card;
    }

    private String generateCardNumber() {
        Random random = new Random();
        StringBuilder cardNumber = new StringBuilder();
        for (int i = 0; i < 16; i++) {
            cardNumber.append(random.nextInt(10));
        }
        return cardNumber.toString();
    }

    private int generateLimitAmount() {
        return 1000000 + new Random().nextInt(9000000);
    }

    private long generateCustomerNumber() {
        Random random = new Random(System.currentTimeMillis());
        return 1000000000L + random.nextInt(900000000);
    }
}