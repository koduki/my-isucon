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
        em.persist(user);
        addCard(user.getId());

        return user;
    }

    public User getUser(long userId) {
        User user = em.find(User.class, userId);
        return user;
    }

    public Card addCard(long userId) {
        User user = getUser(userId);

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
}