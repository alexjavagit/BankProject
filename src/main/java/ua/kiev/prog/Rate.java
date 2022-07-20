package ua.kiev.prog;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="Rate")
public class Rate {
    @Id
    @GeneratedValue
    private Long id;

    private String currency;
    private int amnt;


    public Rate() {
    }

    public Rate(String currency, int amnt) {
        this.currency = currency;
        this.amnt = amnt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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



    @Override
    public String toString() {
        return "Rates{" +
                "id=" + id +
                ", currency='" + currency + '\'' +
                ", amnt=" + amnt +
                '}';
    }
}
