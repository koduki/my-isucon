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
public class CardDao {

    @PersistenceContext
    private EntityManager em;

    public Card add(Card card) {
        em.persist(card);

        return card;
    }

    public Stream<Card> stream() {
        return em
                .createQuery("SELECT u FROM User u", Card.class)
                .getResultList().stream();
    }

}