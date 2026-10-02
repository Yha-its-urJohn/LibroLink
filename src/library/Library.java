package library;

import java.util.ArrayList;

/** Shared data store used by all three modules. */
public class Library {
    private final ArrayList<LibraryItem> items = new ArrayList<>();
    private final ArrayList<User> users = new ArrayList<>();
    private final ArrayList<Payment> payments = new ArrayList<>();

    public ArrayList<LibraryItem> getItems() { return items; }
    public ArrayList<User> getUsers() { return users; }
    public ArrayList<Payment> getPayments() { return payments; }
}
