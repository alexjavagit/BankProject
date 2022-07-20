package ua.kiev.prog;

import javax.persistence.*;
import javax.persistence.criteria.CriteriaBuilder;
import java.util.List;
import java.util.Scanner;

public class App {
    private static final Scanner sc = new Scanner(System.in);
    private static EntityManager em;

    public static void main(String[] args) {
        try {
            EntityManagerFactory emf = Persistence.createEntityManagerFactory("JPATest");
            em = emf.createEntityManager();

            rates_init();

            try {
                while (true) {
                    System.out.println("1: add new client");
                    System.out.println("2: add new account");
                    System.out.println("3: view all clients");
                    System.out.println("4: add money to user account");
                    System.out.println("5: transfer money");
                    System.out.print("-> ");

                    String s = sc.nextLine();
                    switch (s) {
                        case "1":
                            addUser();
                            break;
                        case "2":
                            addAccount();
                            break;
                        case "3":
                            viewUsers();
                            break;
                        case "4":
                            addMoneyToAccount();
                            break;
                        case "5":
                            transferMoney();
                            break;
                        default:
                            return;
                    }
                }
            } finally {
                em.close();
                emf.close();
                sc.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void rates_init() {
        em.getTransaction().begin();
        Rate rate1 = new Rate("USD", 40);
        em.persist(rate1);
        Rate rate2 = new Rate("EUR", 40);
        em.persist(rate2);
        em.getTransaction().commit();
    }

    private static void addUser() {
        em.getTransaction().begin();
        System.out.print("Enter client name: ");
        String name = sc.nextLine();
        User user = new User(name);
        em.persist(user);
        em.getTransaction().commit();
        System.out.println("New Client Added!");
    }

    private static void addAccount() {
        System.out.print("Enter client name: ");
        String uname = sc.nextLine();

        Query query = em.createQuery("SELECT c FROM User c WHERE c.name = :name");
        query.setParameter("name", uname);
        try {
            User user = (User) query.getSingleResult();
            System.out.print("Select currency: 1 - USD, 2 - EUR, 3 - UAH");
            String sCurrency = sc.nextLine();
            Currency currency = Currency.USD;
            if (sCurrency.equals("2"))
                currency = Currency.EUR;
            else if (sCurrency.equals("3"))
                currency = Currency.UAH;

            System.out.print("Enter amount: ");
            String sAmnt = sc.nextLine();
            int amnt = Integer.parseInt(sAmnt);

            em.getTransaction().begin();
            Account account = new Account(user, currency.name(), amnt);
            em.persist(account);
            em.getTransaction().commit();
            System.out.println("New Account Added!");
        } catch (NoResultException ex) {
            System.out.println("User not found!");
            return;
        } catch (NonUniqueResultException ex) {
            System.out.println("Non unique user found!");
            return;
        }
    }

    private static void viewUsers() {
        try {
            Query query = em.createQuery("SELECT u FROM User u ORDER BY u.name", User.class);
            List<User> userList = query.getResultList();

            for (User user : userList) {
                System.out.println(user);
            }
        } catch (NoResultException ex) {
            System.out.println("There are no users yet!");
            return;
        }
    }

    private static void addMoneyToAccount() {
        int myAmnt = 0;
        Query query;

        System.out.println("Enter Account Id:");
        String sAccountId = sc.nextLine();

        Account account = em.find(Account.class, Long.parseLong(sAccountId));
        if (account == null) {
            System.out.println("Account not found error!");
            return;
        }

        System.out.print("Select currency: 1 - USD, 2 - EUR, 3 - UAH");
        String sCurrency = sc.nextLine();
        Currency currency = Currency.USD;
        if (sCurrency.equals("2"))
            currency = Currency.EUR;
        else if (sCurrency.equals("3"))
            currency = Currency.UAH;

        System.out.println("Enter Amount:");
        String sAmount = sc.nextLine();
        int amount = Integer.parseInt(sAmount);

        em.getTransaction().begin();
        try {
            if (!currency.name().equals(account.getCurrency())) {
                query = em.createQuery("SELECT r from Rate r WHERE r.currency = :currency");
                query.setParameter("currency", account.getCurrency());
                Rate rate = (Rate) query.getSingleResult();
                int rateAmntOfAccount = rate.getAmnt();

                if (currency.name() == "UAH") {
                    myAmnt = amount / rateAmntOfAccount;
                } else {
                    query = em.createQuery("SELECT r from Rate r WHERE r.currency = :currency");
                    query.setParameter("currency", currency.name());
                    rate = (Rate) query.getSingleResult();
                    int rateAmnt = rate.getAmnt();
                    myAmnt = amount * rateAmnt / rateAmntOfAccount;
                }
                account.addAmnt(myAmnt);
                em.persist(account);
            } else {
                myAmnt = amount;
                account.addAmnt(amount);
                em.persist(account);
            }

            Transaction transaction = new Transaction(1, null, account, myAmnt);
            em.persist(transaction);
            em.getTransaction().commit();
            System.out.println("Money Added!");
        } catch (Exception ex) {
            ex.printStackTrace();
            em.getTransaction().rollback();
            return;
        }
    }

    private static void transferMoney() {
        Query query;
        int myAmnt;

        System.out.println("Your account Id:");
        String sYourAccountId = sc.nextLine();

        Account yourAccount = em.find(Account.class, Long.parseLong(sYourAccountId));
        if (yourAccount == null) {
            System.out.println("Account not found error!");
            return;
        }

        System.out.println("Receiver account Id:");
        String sReceiverAccountId = sc.nextLine();

        Account receiverAccount = em.find(Account.class, Long.parseLong(sReceiverAccountId));
        if (receiverAccount == null) {
            System.out.println("Account not found error!");
            return;
        }

        System.out.println("Enter Amount to transfer:");
        String sAmount = sc.nextLine();
        int amount = Integer.parseInt(sAmount);
        if (amount > yourAccount.getAmnt()) {
            System.out.println("You have not enough money to make such transfer!");
            return;
        }

        em.getTransaction().begin();
        try {
            Transaction transaction = new Transaction(1, yourAccount, receiverAccount, amount);
            em.persist(transaction);

            if (yourAccount.getCurrency() != receiverAccount.getCurrency()) {
                query = em.createQuery("SELECT r from Rate r WHERE currency = :currency");
                query.setParameter("currency", yourAccount.getCurrency());
                Rate rate = (Rate) query.getSingleResult();
                int yourAccountRate = rate.getAmnt();

                query = em.createQuery("SELECT r from Rate r WHERE currency = :currency");
                query.setParameter("currency", receiverAccount.getCurrency());
                rate = (Rate) query.getSingleResult();
                int receiverAccountRate = rate.getAmnt();

                if (yourAccount.getCurrency() == "UAH") {
                    myAmnt = amount / receiverAccountRate;
                } else {
                    query = em.createQuery("SELECT r from Rate r WHERE currency = :currency");
                    query.setParameter("currency", yourAccount.getCurrency());
                    rate = (Rate) query.getSingleResult();
                    int rateAmnt = rate.getAmnt();

                    myAmnt = amount * rateAmnt / receiverAccountRate;
                }
                yourAccount.decreaseAmnt(amount);
                receiverAccount.addAmnt(myAmnt);
            } else {
                receiverAccount.addAmnt(amount);
                yourAccount.decreaseAmnt(amount);
            }
            em.getTransaction().commit();
            System.out.println("Money Transferred!");
        } catch (Exception ex) {
            ex.printStackTrace();
            em.getTransaction().rollback();
            return;
        }
    }
}
