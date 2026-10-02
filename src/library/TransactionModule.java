package library;

import java.util.ArrayList;

/** MODULE 2 (Main Transaction): add/update records, borrow (rent), buy, return, fines, payments. */
public class TransactionModule {
    private final Library lib;

    public TransactionModule(Library lib) { this.lib = lib; }

    public boolean addItem(LibraryItem item) {
        if (find(item.getId()) != null) return false;
        return lib.getItems().add(item);
    }

    public LibraryItem find(String id) {
        for (LibraryItem i : lib.getItems()) if (i.getId().equalsIgnoreCase(id)) return i;
        return null;
    }

    public String updateTitle(String id, String title) {
        LibraryItem i = find(id);
        if (i == null) return "Item not found.";
        i.setTitle(title);
        return "Title updated.";
    }

    /** Borrow = rent for a number of days, paid right away. */
    public String borrow(Member m, String itemId, int days, String method) {
        LibraryItem i = find(itemId);
        if (i == null) return "Item not found.";
        if (!i.isAvailable()) return "That item is already borrowed.";
        if (!m.canBorrow()) return "You reached the limit of " + Member.MAX_LOANS + " items.";
        if (days < 1 || days > i.getLoanDays()) return "You can borrow this for 1 to " + i.getLoanDays() + " days.";
        double cost = days * i.getRentPerDay();
        i.setAvailable(false);
        i.setDaysBorrowed(days);
        m.getBorrowed().add(i);
        record(m, i.getTitle(), "Rental", cost, method);
        return "Borrowed \"" + i.getTitle() + "\" for " + days + " day(s). Paid PHP "
                + String.format("%.2f", cost) + " via " + method + ".";
    }

    /** Buy = pay the price and keep it; the item leaves the library. */
    public String buy(Member m, String itemId, String method) {
        LibraryItem i = find(itemId);
        if (i == null) return "Item not found.";
        if (!i.isAvailable()) return "That item is currently borrowed and can't be sold.";
        lib.getItems().remove(i);
        record(m, i.getTitle(), "Purchase", i.getPrice(), method);
        return "You bought \"" + i.getTitle() + "\" for PHP " + String.format("%.2f", i.getPrice()) + " via " + method + ".";
    }

    public String giveBack(Member m, String itemId, int daysKept) {
        LibraryItem i = find(itemId);
        if (i == null || !m.getBorrowed().contains(i)) return "You did not borrow that item.";
        double fine = Math.max(0, daysKept - i.getDaysBorrowed()) * i.getFinePerDay();   // CALCULATE
        i.setAvailable(true);
        i.setDaysBorrowed(0);
        m.getBorrowed().remove(i);
        if (fine > 0) lib.getPayments().add(new Payment(m, i.getTitle(), "Late fine", fine));
        return "Returned \"" + i.getTitle() + "\". "
                + (fine > 0 ? "Late fine: PHP " + String.format("%.2f", fine) + " (pay it in the My Activity tab)." : "No fine.");
    }

    public String pay(Payment p, String method) {
        if (p.isPaid()) return "That one is already paid.";
        p.pay(method);
        return "Payment received: PHP " + String.format("%.2f", p.getAmount()) + " via " + method + ".";
    }

    public ArrayList<Payment> paymentsOf(Member m) {
        ArrayList<Payment> mine = new ArrayList<>();
        for (Payment p : lib.getPayments()) if (p.getMember() == m) mine.add(p);
        return mine;
    }

    private void record(Member m, String item, String type, double amount, String method) {
        Payment p = new Payment(m, item, type, amount);
        p.pay(method);
        lib.getPayments().add(p);
    }
}
