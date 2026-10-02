package library;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

/** Member dashboard: browse, borrow and return books. */
public class MemberFrame extends JFrame {
    private final Library lib;
    private final Member me;
    private final TransactionModule tx;
    private final ReportModule reports;
    private final JTable browse = Theme.table("ID", "Type", "Title", "Details", "Price (PHP)", "Status");
    private final JTable loans = Theme.table("ID", "Title", "Type", "Days borrowed");
    private final JTextField search = new JTextField();
    private final JTable fines = Theme.table("Item", "Type", "Amount (PHP)", "Status", "Paid via", "Date");
    private final JLabel owed = Theme.label(" ", 14, true, new Color(225, 230, 240));
    private List<Payment> shownPayments = new ArrayList<>();

    public MemberFrame(Library lib, Member me) {
        this.lib = lib;
        this.me = me;
        tx = new TransactionModule(lib);
        reports = new ReportModule(lib);
        setTitle("LibroLink - " + me.getFullName());
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        DemonBackground bg = new DemonBackground();
        bg.setLayout(new BorderLayout());
        setContentPane(bg);
        add(Theme.header("LibroLink", "Hi, " + me.getFullName(), this::logout), BorderLayout.NORTH);
        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(Theme.BOLD);
        tabs.setOpaque(false);
        tabs.addTab("Browse books", browseTab());
        tabs.addTab("My Activity", activityTab());
        add(tabs, BorderLayout.CENTER);
        refresh();
        setSize(1000, 660);
        setLocationRelativeTo(null);
    }

    private JPanel browseTab() {
        Theme.style(search);
        JButton go = Theme.button("Search", Theme.BLUE);
        go.addActionListener(e -> refresh());
        search.addActionListener(e -> refresh());
        JPanel top = new JPanel(new BorderLayout(8, 0));
        top.setOpaque(false);
        top.add(search, BorderLayout.CENTER);
        top.add(go, BorderLayout.EAST);
        JButton borrow = Theme.button("Borrow selected", Theme.GREEN);
        borrow.addActionListener(e -> {
            int r = browse.getSelectedRow();
            if (r < 0) { info("Select a row first."); return; }
            borrowDialog((String) browse.getValueAt(r, 0));
        });
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        bottom.setOpaque(false);
        JButton buy = Theme.button("Buy selected", Theme.BLUE);
        buy.addActionListener(e -> {
            int r = browse.getSelectedRow();
            if (r < 0) { info("Select a row first."); return; }
            buyDialog((String) browse.getValueAt(r, 0));
        });
        bottom.add(borrow);
        bottom.add(buy);
        bottom.add(Theme.label("Borrow = rent by the day. Buy = pay the price and keep it.", 13, false, new Color(225, 230, 240)));
        JPanel p = Theme.page();
        p.add(top, BorderLayout.NORTH);
        p.add(Theme.scroll(browse), BorderLayout.CENTER);
        p.add(bottom, BorderLayout.SOUTH);
        return p;
    }

    private JPanel loansTab() {
        JButton back = Theme.button("Return selected", Theme.BLUE);
        back.addActionListener(e -> {
            int r = loans.getSelectedRow();
            if (r < 0) { info("Select a row first."); return; }
            String s = JOptionPane.showInputDialog(this, "How many days did you keep it?", "1");
            if (s == null) return;
            try {
                info(tx.giveBack(me, (String) loans.getValueAt(r, 0), Integer.parseInt(s.trim())));
            } catch (NumberFormatException ex) {
                info("Please enter a number.");
            }
            refresh();
        });
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        bottom.setOpaque(false);
        bottom.add(back);
        bottom.add(Theme.label("Rent: Books PHP 10/day (max 7 days), Magazines PHP 5/day (max 3). Late fine: Books PHP 20/day, Magazines PHP 10/day.", 13, false, new Color(225, 230, 240)));
        return section("Borrowed items", loans, bottom);
    }

    private JPanel finesTab() {
        JButton pay = Theme.button("Pay selected", Theme.GREEN);
        pay.addActionListener(e -> {
            int r = fines.getSelectedRow();
            if (r < 0) { info("Select a fine first."); return; }
            Payment f = shownPayments.get(r);
            if (f.isPaid()) { info("That fine is already paid."); return; }
            Object method = JOptionPane.showInputDialog(this, "Pay PHP " + String.format("%.2f", f.getAmount()) + " using:",
                    "Pay fine", JOptionPane.PLAIN_MESSAGE, null, new String[]{"Cash", "GCash", "Maya"}, "Cash");
            if (method == null) return;
            info(tx.pay(f, (String) method));
            refresh();
        });
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        bottom.setOpaque(false);
        bottom.add(pay);
        bottom.add(owed);
        return section("Payments (rentals, purchases and late fines)", fines, bottom);
    }

    /** One tab that shows borrowed items on top and payments below. */
    private JPanel activityTab() {
        JPanel both = new JPanel(new GridLayout(2, 1, 0, 14));
        both.setOpaque(false);
        both.add(loansTab());
        both.add(finesTab());
        JPanel p = Theme.page();
        p.add(both, BorderLayout.CENTER);
        return p;
    }

    private JPanel section(String title, JTable table, JPanel buttons) {
        JPanel p = new JPanel(new BorderLayout(0, 6));
        p.setOpaque(false);
        p.add(Theme.label(title, 16, true, Color.WHITE), BorderLayout.NORTH);
        p.add(Theme.scroll(table), BorderLayout.CENTER);
        p.add(buttons, BorderLayout.SOUTH);
        return p;
    }

    private String chooseMethod(String prompt) {
        Object m = JOptionPane.showInputDialog(this, prompt, "Payment", JOptionPane.PLAIN_MESSAGE, null,
                new String[]{"Cash", "GCash", "Maya"}, "Cash");
        return m == null ? null : (String) m;
    }

    private void borrowDialog(String itemId) {
        LibraryItem item = tx.find(itemId);
        if (item == null) { info("Item not found."); return; }
        if (!item.isAvailable()) { info("That item is already borrowed."); return; }
        if (!me.canBorrow()) { info("You reached the limit of " + Member.MAX_LOANS + " items."); return; }
        String s = JOptionPane.showInputDialog(this, "How many days? (1 to " + item.getLoanDays() + ")\nRent: PHP "
                + String.format("%.2f", item.getRentPerDay()) + " per day", "1");
        if (s == null) return;
        int days;
        try { days = Integer.parseInt(s.trim()); } catch (NumberFormatException ex) { info("Please enter a number."); return; }
        if (days < 1 || days > item.getLoanDays()) { info("You can borrow this for 1 to " + item.getLoanDays() + " days."); return; }
        String method = chooseMethod("Pay PHP " + String.format("%.2f", days * item.getRentPerDay()) + " for " + days + " day(s) using:");
        if (method == null) return;
        info(tx.borrow(me, itemId, days, method));
        refresh();
    }

    private void buyDialog(String itemId) {
        LibraryItem item = tx.find(itemId);
        if (item == null) { info("Item not found."); return; }
        if (!item.isAvailable()) { info("That item is currently borrowed and can't be sold."); return; }
        String method = chooseMethod("Buy \"" + item.getTitle() + "\" for PHP " + String.format("%.2f", item.getPrice()) + " using:");
        if (method == null) return;
        info(tx.buy(me, itemId, method));
        refresh();
    }

    private void info(String s) { JOptionPane.showMessageDialog(this, s); }

    private void refresh() {
        List<LibraryItem> list = reports.search(search.getText());
        DefaultTableModel b = (DefaultTableModel) browse.getModel();
        b.setRowCount(0);
        for (LibraryItem i : list)
            b.addRow(new Object[]{i.getId(), i.getType(), i.getTitle(), i.getDetails(),
                    String.format("%.2f", i.getPrice()), i.isAvailable() ? "Available" : "Borrowed"});
        DefaultTableModel l = (DefaultTableModel) loans.getModel();
        l.setRowCount(0);
        for (LibraryItem i : me.getBorrowed())
            l.addRow(new Object[]{i.getId(), i.getTitle(), i.getType(), i.getDaysBorrowed()});
        shownPayments = tx.paymentsOf(me);
        DefaultTableModel fm = (DefaultTableModel) fines.getModel();
        fm.setRowCount(0);
        double due = 0;
        for (Payment f : shownPayments) {
            fm.addRow(new Object[]{f.getItem(), f.getType(), String.format("%.2f", f.getAmount()), f.isPaid() ? "Paid" : "Unpaid", f.getMethod(), f.getTime()});
            if (!f.isPaid()) due += f.getAmount();
        }
        owed.setText("Unpaid: PHP " + String.format("%.2f", due));
    }

    private void logout() {
        dispose();
        new LoginFrame(lib).setVisible(true);
    }
}
