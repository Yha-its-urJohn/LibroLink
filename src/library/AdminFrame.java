package library;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

/** Admin dashboard: manage books, users and reports. */
public class AdminFrame extends JFrame {
    private final Library lib;
    private final AccountModule accounts;
    private final TransactionModule tx;
    private final ReportModule reports;
    private final JTable books = Theme.table("ID", "Type", "Title", "Details", "Price (PHP)", "Status");
    private final JTable users = Theme.table("Username", "Name", "Role", "Borrowed");
    private final JTextArea reportArea = new JTextArea();
    private final JTextField search = new JTextField();
    private final JTable payments = Theme.table("Member", "Item", "Type", "Amount (PHP)", "Status", "Paid via", "Date");
    private List<Payment> shownPayments = new ArrayList<>();

    public AdminFrame(Library lib, Admin admin) {
        this.lib = lib;
        accounts = new AccountModule(lib);
        tx = new TransactionModule(lib);
        reports = new ReportModule(lib);
        setTitle("LibroLink - Admin");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        DemonBackground bg = new DemonBackground();
        bg.setLayout(new BorderLayout());
        setContentPane(bg);
        add(Theme.header("LibroLink Admin", admin.getFullName(), this::logout), BorderLayout.NORTH);
        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(Theme.BOLD);
        tabs.setOpaque(false);
        tabs.addTab("Books", booksTab());
        tabs.addTab("Users", usersTab());
        tabs.addTab("Payments", paymentsTab());
        tabs.addTab("Report", reportTab());
        add(tabs, BorderLayout.CENTER);
        refreshAll();
        setSize(1000, 660);
        setLocationRelativeTo(null);
    }

    private JPanel booksTab() {
        Theme.style(search);
        JButton go = Theme.button("Search", Theme.BLUE);
        JButton all = Theme.button("Show all", Theme.GRAY);
        go.addActionListener(e -> fillBooks(reports.search(search.getText())));
        search.addActionListener(e -> go.doClick());
        all.addActionListener(e -> { search.setText(""); fillBooks(lib.getItems()); });
        JPanel btns = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        btns.setOpaque(false);
        btns.add(go);
        btns.add(all);
        JPanel top = new JPanel(new BorderLayout(8, 0));
        top.setOpaque(false);
        top.add(search, BorderLayout.CENTER);
        top.add(btns, BorderLayout.EAST);

        JButton addB = Theme.button("Add book", Theme.GREEN);
        JButton addM = Theme.button("Add magazine", Theme.GREEN);
        JButton upd = Theme.button("Update title", Theme.BLUE);
        JButton del = Theme.button("Delete", Theme.RED);
        addB.addActionListener(e -> addItem(false));
        addM.addActionListener(e -> addItem(true));
        upd.addActionListener(e -> updateTitle());
        del.addActionListener(e -> deleteItem());
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        bottom.setOpaque(false);
        bottom.add(addB);
        bottom.add(addM);
        bottom.add(upd);
        bottom.add(del);

        JPanel p = Theme.page();
        p.add(top, BorderLayout.NORTH);
        p.add(Theme.scroll(books), BorderLayout.CENTER);
        p.add(bottom, BorderLayout.SOUTH);
        return p;
    }

    private JPanel usersTab() {
        JButton del = Theme.button("Delete member", Theme.RED);
        del.addActionListener(e -> {
            int r = users.getSelectedRow();
            if (r < 0) { info("Select a row first."); return; }
            String name = (String) users.getValueAt(r, 0);
            info(accounts.deleteMember(name) ? "Member deleted." : "Only members with no borrowed items can be deleted.");
            refreshAll();
        });
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        bottom.setOpaque(false);
        JButton add = Theme.button("Add member", Theme.GREEN);
        add.addActionListener(e -> addMember());
        bottom.add(add);
        bottom.add(del);
        JPanel p = Theme.page();
        p.add(Theme.scroll(users), BorderLayout.CENTER);
        p.add(bottom, BorderLayout.SOUTH);
        return p;
    }

    private JPanel reportTab() {
        reportArea.setEditable(false);
        reportArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 16));
        reportArea.setBorder(new EmptyBorder(16, 16, 16, 16));
        JButton refresh = Theme.button("Refresh", Theme.BLUE);
        refresh.addActionListener(e -> refreshAll());
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        bottom.setOpaque(false);
        bottom.add(refresh);
        JPanel p = Theme.page();
        p.add(Theme.scroll(reportArea), BorderLayout.CENTER);
        p.add(bottom, BorderLayout.SOUTH);
        return p;
    }

    private JPanel paymentsTab() {
        JButton mark = Theme.button("Mark as paid (cash)", Theme.GREEN);
        mark.addActionListener(e -> {
            int r = payments.getSelectedRow();
            if (r < 0) { info("Select a row first."); return; }
            info(tx.pay(shownPayments.get(r), "Cash (counter)"));
            refreshAll();
        });
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        bottom.setOpaque(false);
        bottom.add(mark);
        JPanel p = Theme.page();
        p.add(Theme.scroll(payments), BorderLayout.CENTER);
        p.add(bottom, BorderLayout.SOUTH);
        return p;
    }

    private void addMember() {
        JTextField name = new JTextField(), user = new JTextField();
        JPasswordField pass = new JPasswordField();
        Object[] form = {"Full name", name, "Username", user, "Password", pass};
        if (JOptionPane.showConfirmDialog(this, form, "Add member", JOptionPane.OK_CANCEL_OPTION) != JOptionPane.OK_OPTION) return;
        String p = new String(pass.getPassword());
        if (name.getText().isBlank() || user.getText().isBlank() || p.isEmpty()) { info("Please fill in every field."); return; }
        info(accounts.register(new Member(user.getText().trim(), p, name.getText().trim())) ? "Member added." : "That username is taken.");
        refreshAll();
    }

    private void addItem(boolean magazine) {
        String id = ask("Item ID:");
        if (id == null) return;
        String title = ask("Title:");
        if (title == null) return;
        String extra = ask(magazine ? "Issue (e.g. Oct 2026):" : "Author:");
        if (extra == null) return;
        String pr = ask("Price to buy (PHP):");
        if (pr == null) return;
        double price;
        try { price = Double.parseDouble(pr); } catch (NumberFormatException ex) { info("Price must be a number."); return; }
        LibraryItem item = magazine ? new Magazine(id, title, extra, price) : new Book(id, title, extra, price);
        info(tx.addItem(item) ? "Item added." : "That ID already exists.");
        refreshAll();
    }

    private void updateTitle() {
        String id = selectedId();
        if (id == null) return;
        String t = ask("New title:");
        if (t == null) return;
        info(tx.updateTitle(id, t));
        refreshAll();
    }

    private void deleteItem() {
        String id = selectedId();
        if (id == null) return;
        if (JOptionPane.showConfirmDialog(this, "Delete " + id + "?", "Confirm", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) return;
        info(reports.deleteItem(id) ? "Item deleted." : "A borrowed item cannot be deleted.");
        refreshAll();
    }

    private String selectedId() {
        int r = books.getSelectedRow();
        if (r < 0) { info("Select a row first."); return null; }
        return (String) books.getValueAt(r, 0);
    }

    private String ask(String prompt) {
        String s = JOptionPane.showInputDialog(this, prompt);
        return (s == null || s.isBlank()) ? null : s.trim();
    }

    private void info(String s) { JOptionPane.showMessageDialog(this, s); }

    private void fillBooks(List<LibraryItem> list) {
        DefaultTableModel m = (DefaultTableModel) books.getModel();
        m.setRowCount(0);
        for (LibraryItem i : list)
            m.addRow(new Object[]{i.getId(), i.getType(), i.getTitle(), i.getDetails(),
                    String.format("%.2f", i.getPrice()), i.isAvailable() ? "Available" : "Borrowed"});
    }

    private void refreshAll() {
        fillBooks(lib.getItems());
        DefaultTableModel m = (DefaultTableModel) users.getModel();
        m.setRowCount(0);
        for (User u : lib.getUsers())
            m.addRow(new Object[]{u.getUsername(), u.getFullName(), u.getRole(),
                    u instanceof Member ? String.valueOf(((Member) u).getBorrowed().size()) : "-"});
        shownPayments = new ArrayList<>(lib.getPayments());
        DefaultTableModel pm = (DefaultTableModel) payments.getModel();
        pm.setRowCount(0);
        for (Payment f : shownPayments)
            pm.addRow(new Object[]{f.getMember().getFullName(), f.getItem(), f.getType(), String.format("%.2f", f.getAmount()),
                    f.isPaid() ? "Paid" : "Unpaid", f.getMethod(), f.getTime()});
        reportArea.setText(reports.summary());
    }

    private void logout() {
        dispose();
        new LoginFrame(lib).setVisible(true);
    }
}
