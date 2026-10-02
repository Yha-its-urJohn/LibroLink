package library;

/** ABSTRACTION: base class for every account. */
public abstract class User {
    private final String username;
    private final String password;
    private final String fullName;

    public User(String username, String password, String fullName) {
        this.username = username;
        this.password = password;
        this.fullName = fullName;
    }

    public String getUsername() { return username; }
    public String getFullName() { return fullName; }
    public boolean checkPassword(String p) { return password.equals(p); }
    public abstract String getRole();   // POLYMORPHISM: each account type answers differently
}
