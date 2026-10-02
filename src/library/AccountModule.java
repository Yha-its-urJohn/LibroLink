package library;

/** MODULE 1 (User/Account): login, registration, user management. */
public class AccountModule {
    private final Library lib;

    public AccountModule(Library lib) { this.lib = lib; }

    public User login(String username, String password, String role) {
        for (User u : lib.getUsers())
            if (u.getUsername().equalsIgnoreCase(username) && u.checkPassword(password) && u.getRole().equals(role))
                return u;
        return null;
    }

    public boolean register(Member m) {
        if (find(m.getUsername()) != null) return false;
        return lib.getUsers().add(m);
    }

    public User find(String username) {
        for (User u : lib.getUsers()) if (u.getUsername().equalsIgnoreCase(username)) return u;
        return null;
    }

    public boolean deleteMember(String username) {
        User u = find(username);
        if (!(u instanceof Member) || !((Member) u).getBorrowed().isEmpty()) return false;
        return lib.getUsers().remove(u);
    }
}
