package library;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

/** Starts the app with sample data. */
public class Main {
    public static void main(String[] args) {
        UIManager.put("TabbedPane.contentOpaque", false);   // lets the animated background show through the tabs
        Library lib = new Library();
        AccountModule accounts = new AccountModule(lib);
        TransactionModule tx = new TransactionModule(lib);
        lib.getUsers().add(new Admin("admin", "admin123", "Library Admin"));
        accounts.register(new Member("jochel", "1234", "Jochel"));
        accounts.register(new Member("henry", "1234", "Henry"));
        accounts.register(new Member("laurice", "1234", "Laurice"));
        tx.addItem(new Book("B1", "Java Basics", "J. Cruz", 250));
        tx.addItem(new Book("B2", "Data Structures", "M. Reyes", 320));
        tx.addItem(new Book("B3", "Noli Me Tangere", "J. Rizal", 180));
        tx.addItem(new Magazine("M1", "Tech Monthly", "Oct 2026", 120));
        tx.addItem(new Magazine("M2", "Science Today", "Sep 2026", 95));
        SwingUtilities.invokeLater(() -> new LoginFrame(lib).setVisible(true));
    }
}
