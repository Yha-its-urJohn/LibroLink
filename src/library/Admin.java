package library;

/** INHERITANCE: an Admin is a User who manages books and accounts. */
public class Admin extends User {
    public Admin(String username, String password, String fullName) { super(username, password, fullName); }
    @Override public String getRole() { return "Admin"; }
}
