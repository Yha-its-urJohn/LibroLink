package library;

/** INHERITANCE: a Book is a LibraryItem. Rent PHP 10/day, up to 7 days. */
public class Book extends LibraryItem {
    private final String author;

    public Book(String id, String title, String author, double price) {
        super(id, title, price);
        this.author = author;
    }

    @Override public String getType() { return "Book"; }
    @Override public int getLoanDays() { return 7; }
    @Override public double getRentPerDay() { return 10.0; }
    @Override public double getFinePerDay() { return 20.0; }
    @Override public String getDetails() { return "by " + author; }
}
