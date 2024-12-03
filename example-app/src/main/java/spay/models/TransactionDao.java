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
public class TransactionDao {

    @PersistenceContext
    private EntityManager em;

    public PaymentTransaction persist(PaymentTransaction transaction) {
        em.persist(transaction);

        return transaction;
    }

    public List<PaymentTransaction> list(String cardNumber) {
        return em
                .createQuery("SELECT t FROM PaymentTransaction t where t.cardNumber = :cardNumber", PaymentTransaction.class)
                .setParameter("cardNumber", cardNumber)
                .getResultList();
    }
    public Stream<PaymentTransaction> stream(String cardNumber) {
        return this.list(cardNumber).stream();
    }
}