package ua.kiev.prog;

import javax.persistence.*;

@Entity
@Table(name="Transactions")
public class Transaction {
    @Id
    @GeneratedValue
    private Long id;

    private int operation;

    @ManyToOne
    @JoinColumn(name="credit_acc_id")
    private Account creditAccId;
    @ManyToOne
    @JoinColumn(name="debit_acc_id")
    private Account debitAccId;

    private int amnt;



    public Transaction() {
    }

    public Transaction(int operation, Account creditAccId, Account debitAccId, int amnt) {
        this.operation = operation;
        this.creditAccId = creditAccId;
        this.debitAccId = debitAccId;
        this.amnt = amnt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getOperation() {
        return operation;
    }

    public void setOperation(int operation) {
        this.operation = operation;
    }

    public Account getCreditAccId() {
        return creditAccId;
    }

    public void setCreditAccId(Account creditAccId) {
        this.creditAccId = creditAccId;
    }

    public Account getDebitAccId() {
        return debitAccId;
    }

    public void setDebitAccId(Account debitAccId) {
        this.debitAccId = debitAccId;
    }

    public int getAmnt() {
        return amnt;
    }

    public void setAmnt(int amnt) {
        this.amnt = amnt;
    }



    @Override
    public String toString() {
        return "Transactions{" +
                "id=" + id +
                ", operation=" + operation +
                ", creditAccId=" + creditAccId +
                ", debitAccId=" + debitAccId +
                ", amnt=" + amnt +
                '}';
    }
}
