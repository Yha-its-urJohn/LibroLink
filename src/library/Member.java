package library;

import java.util.ArrayList;

/** INHERITANCE: a Member is a User who can borrow up to 3 items. */
public class Member extends User {
    public static final int MAX_LOANS = 3;
    private final ArrayList<LibraryItem> borrowed = new ArrayList<>();

    public Member(String username, String password, String fullName) { super(username, password, fullName); }
    @Override public String getRole() { return "Member"; }
    public ArrayList<LibraryItem> getBorrowed() { return borrowed; }
    public boolean canBorrow() { return borrowed.size() < MAX_LOANS; }
}
