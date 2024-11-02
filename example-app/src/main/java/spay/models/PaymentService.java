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
public class PaymentService {

    @PersistenceContext
    private EntityManager em;

    public PaymentTransaction purchase(PaymentTransaction request) throws LimitExceededException {
        Card card = em.createQuery("SELECT c FROM Card c WHERE c.cardNumber = :cardNumber", Card.class)
                .setParameter("cardNumber", request.getCard().getCardNumber())
                .getSingleResult();
        verifyLimit(request.getAmount(), card);
        card.setUsedAmount(card.getUsedAmount() + request.getAmount());
        PaymentTransaction transaction = new PaymentTransaction();
        transaction.setCard(card);
        transaction.setItemName(request.getItemName());
        transaction.setAmount(request.getAmount());
        em.persist(transaction);
        return transaction;
    }

    public Card addCard(long userId) {
        User user = em.find(User.class, userId);

        Card card = new Card();
        card.setCardNumber(generateCardNumber());
        card.setLimitAmount(generateLimitAmount());
        card.setUsedAmount(0);
        em.persist(card);

        user.getCards().add(card);

        return card;
    }

    public void registUser(User user) {
        em.persist(user);

        Card card = new Card();
        card.setCardNumber(generateCardNumber());
        card.setLimitAmount(generateLimitAmount());
        card.setUsedAmount(0);
        em.persist(card);

        user.getCards().add(card);
    }

    private void verifyLimit(int requestAmount, Card card) throws LimitExceededException {
        if (requestAmount > card.getLimitAmount() - card.getUsedAmount()) {
            throw new LimitExceededException();
        }
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
