package ua.kiev.prog;

import javax.persistence.*;

@Entity
@Table(name="Accounts")
public class Account {
    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne
    @JoinColumn(name="user_id")
    private User user;

    @Column(name="currency", nullable = false)
    private String currency;
    @Column(name="amnt", nullable = false)
    private int amnt;

    public Account() {
    }

    public Account(User user, String currency, int amnt) {
        this.currency = currency;
        this.amnt = amnt;
        this.user = user;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public int getAmnt() {
        return amnt;
    }

    public void setAmnt(int amnt) {
        this.amnt = amnt;
    }

    public void addAmnt(int amnt) {
        this.amnt = this.amnt + amnt;
    }

    public void decreaseAmnt(int amnt) {
        this.amnt = this.amnt - amnt;
    }

}
