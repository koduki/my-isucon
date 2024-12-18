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

    public Card add(Card card) {
        em.persist(card);

        return card;
    }

    public List<Card> list() {
        return em
                .createQuery("SELECT c FROM Card c", Card.class)
                .getResultList();
    }

    public Stream<Card> stream() {
        return this.list().stream();
    }
}