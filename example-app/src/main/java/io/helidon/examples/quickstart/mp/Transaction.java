package io.helidon.examples.quickstart.mp;

import javax.persistence.*;

@Entity
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "transaction_seq")
    @SequenceGenerator(name = "transaction_seq", sequenceName = "transaction_sequence", allocationSize = 1)
    private Long id;

    @ManyToOne
    @JoinColumn(nullable = false)
    private Card card;

    @Column(nullable = false)
    private String itemName;

    @Column(nullable = false)
    private int amount;

    @Column(nullable = false)
    private java.sql.Timestamp transactionDate;

    public Transaction() {
    }

    @PrePersist
    protected void onCreate() {
        this.transactionDate = new java.sql.Timestamp(System.currentTimeMillis());
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Card getCard() {
        return card;
    }

    public void setCard(Card card) {
        this.card = card;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public java.sql.Timestamp getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(java.sql.Timestamp transactionDate) {
        this.transactionDate = transactionDate;
    }
}