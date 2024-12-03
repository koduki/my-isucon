/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package spay.models;

import java.util.List;
import java.util.stream.Stream;

import jakarta.enterprise.context.Dependent;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

/**
 *
 * @author koduki
 */
@Dependent
public class CardDao {

    @PersistenceContext
    private EntityManager em;

    public Card persist(Card card) {
        em.persist(card);

        return card;
    }

    public List<Card> list(String cardNumber) {
        return em
                .createQuery("SELECT c FROM Card c WHERE c.cardNumber = :cardNumber", Card.class)
                .setParameter("cardNumber", cardNumber)
                .getResultList();
    }

    public Stream<Card> stream(String cardNumber) {
        return this.list(cardNumber).stream();
    }
    public Card get(String cardNumber) {
        return em.createQuery("SELECT c FROM Card c WHERE c.cardNumber = :cardNumber", Card.class)
                .setParameter("cardNumber", cardNumber)
                .getSingleResult();
    }
}