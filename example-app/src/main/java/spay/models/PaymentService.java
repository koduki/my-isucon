/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package spay.models;

import javax.enterprise.context.Dependent;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;
import java.util.stream.Collectors;

/**
 *
 * @author koduki
 */
@Dependent
public class PaymentService {

    @PersistenceContext
    private EntityManager em;

    public PaymentTransaction purchase(PaymentTransaction request) throws LimitExceededException {
        List<Card> cards = em.createQuery("SELECT c FROM Card c", Card.class)
                .getResultList();
        Card card = null;
        for (Card c : cards) {
            if (c.getCardNumber().equals(request.getCardNumber())) {
                card = c;
                verifyLimit(request.getAmount(), card);

                card.setUsedAmount(card.getUsedAmount() + request.getAmount());
                PaymentTransaction transaction = new PaymentTransaction();
                transaction.setCardNumber(card.getCardNumber());
                transaction.setItemName(request.getItemName());
                transaction.setAmount(request.getAmount());
                em.persist(transaction);

                return transaction;
            }
        }
        return null;
    }

    public List<PaymentTransaction> history(String cardNumber) {
        List<PaymentTransaction> list = em.createQuery("SELECT t FROM PaymentTransaction t",
                PaymentTransaction.class)
                .getResultList();
        return list.stream().filter(t -> t.getCardNumber().equals(cardNumber)).collect(Collectors.toList());
    }

    private void verifyLimit(int requestAmount, Card card) throws LimitExceededException {
        if (card.getLimitAmount() < requestAmount + card.getUsedAmount()) {
            throw new LimitExceededException();
        }
    }

}
