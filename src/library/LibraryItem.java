package library;

/** ABSTRACTION: common blueprint for everything the library lends or sells. */
public abstract class LibraryItem {
    // ENCAPSULATION: private fields, accessed only through getters/setters
    private final String id;
    private String title;
    private final double price;          // price to BUY the item
    private boolean available = true;
    private int daysBorrowed = 0;        // days chosen by the member who borrowed it

    public LibraryItem(String id, String title, double price) {   // CONSTRUCTOR
        this.id = id;
        this.title = title;
        this.price = price;
    }

    public String getId() { return id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public double getPrice() { return price; }
    public boolean isAvailable() { return available; }
    public void setAvailable(boolean available) { this.available = available; }
    public int getDaysBorrowed() { return daysBorrowed; }
    public void setDaysBorrowed(int daysBorrowed) { this.daysBorrowed = daysBorrowed; }

    // POLYMORPHISM: each subclass supplies its own rules
    public abstract String getType();
    public abstract int getLoanDays();          // longest allowed borrowing period
    public abstract double getRentPerDay();
    public abstract double getFinePerDay();     // charged for each day kept beyond the borrowed days
    public abstract String getDetails();
}
