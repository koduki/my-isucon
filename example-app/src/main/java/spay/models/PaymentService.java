/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package spay.models;

import javax.enterprise.context.Dependent;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

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

    public List<PaymentTransaction> history(String cardNumber) {
        return em
                .createQuery("SELECT pt FROM PaymentTransaction pt WHERE pt.card.cardNumber = :cardNumber",
                        PaymentTransaction.class)
                .setParameter("cardNumber", cardNumber)
                .getResultList();
    }

    private void verifyLimit(int requestAmount, Card card) throws LimitExceededException {
        if (requestAmount > card.getLimitAmount() - card.getUsedAmount()) {
            throw new LimitExceededException();
        }
    }

}
