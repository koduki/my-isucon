package io.helidon.examples.quickstart.mp;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_seq")
    @SequenceGenerator(name = "user_seq", sequenceName = "user_sequence", allocationSize = 1)
    private Long id;

    @Column(unique = true, nullable = false)
    private String userName;

    @Column(nullable = false)
    private java.sql.Timestamp createdAt;

    @Column(nullable = false)
    private java.sql.Timestamp updatedAt;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Card> cards;

    // デフォルトコンストラクタ
    public User() {
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = new java.sql.Timestamp(System.currentTimeMillis());
        this.updatedAt = this.createdAt;
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = new java.sql.Timestamp(System.currentTimeMillis());
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public java.sql.Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(java.sql.Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public java.sql.Timestamp getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(java.sql.Timestamp updatedAt) {
        this.updatedAt = updatedAt;
    }

    public List<Card> getCards() {
        return cards;
    }

    public void setCards(List<Card> cards) {
        this.cards = cards;
    }
}