package library;

/** INHERITANCE: a Magazine is a LibraryItem. Rent PHP 5/day, up to 3 days. */
public class Magazine extends LibraryItem {
    private final String issue;

    public Magazine(String id, String title, String issue, double price) {
        super(id, title, price);
        this.issue = issue;
    }

    @Override public String getType() { return "Magazine"; }
    @Override public int getLoanDays() { return 3; }
    @Override public double getRentPerDay() { return 5.0; }
    @Override public double getFinePerDay() { return 10.0; }
    @Override public String getDetails() { return "Issue " + issue; }
}
