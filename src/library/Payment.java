package library;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/** Any money record: Rental, Purchase, or Late fine. Late fines start unpaid. */
public class Payment {
    private final Member member;
    private final String item;
    private final String type;
    private final double amount;
    private boolean paid = false;
    private String method = "-";
    private String time = "-";

    public Payment(Member member, String item, String type, double amount) {
        this.member = member;
        this.item = item;
        this.type = type;
        this.amount = amount;
    }

    public void pay(String method) {
        paid = true;
        this.method = method;
        time = LocalDateTime.now().format(DateTimeFormatter.ofPattern("MMM dd, yyyy hh:mm a"));
    }

    public Member getMember() { return member; }
    public String getItem() { return item; }
    public String getType() { return type; }
    public double getAmount() { return amount; }
    public boolean isPaid() { return paid; }
    public String getMethod() { return method; }
    public String getTime() { return time; }
}
