package library;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableModel;

/** Facebook-style colors and reusable styled components. */
public class Theme {
    public static final Color BLUE = new Color(24, 119, 242);
    public static final Color GREEN = new Color(66, 183, 42);
    public static final Color RED = new Color(220, 53, 69);
    public static final Color BG = new Color(240, 242, 245);
    public static final Color TEXT = new Color(28, 30, 33);
    public static final Color GRAY = new Color(96, 103, 112);
    public static final Color LINE = new Color(206, 208, 212);
    public static final Font FONT = new Font("Segoe UI", Font.PLAIN, 15);
    public static final Font BOLD = new Font("Segoe UI", Font.BOLD, 15);

    public static JButton button(String text, Color bg) {
        JButton b = new JButton(text);
        b.setBackground(bg);
        b.setForeground(Color.WHITE);
        b.setFont(BOLD);
        b.setOpaque(true);
        b.setFocusPainted(false);
        b.setBorderPainted(false);
        b.setBorder(new EmptyBorder(10, 18, 10, 18));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return b;
    }

    public static JLabel label(String text, int size, boolean bold, Color color) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("Segoe UI", bold ? Font.BOLD : Font.PLAIN, size));
        l.setForeground(color);
        return l;
    }

    public static void style(JTextField f) {
        f.setFont(FONT);
        f.setBorder(BorderFactory.createCompoundBorder(new LineBorder(LINE), new EmptyBorder(10, 12, 10, 12)));
    }

    public static JTable table(String... columns) {
        DefaultTableModel m = new DefaultTableModel(columns, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable t = new JTable(m);
        t.setFont(FONT);
        t.setRowHeight(30);
        t.setGridColor(LINE);
        t.setSelectionBackground(new Color(231, 243, 255));
        t.setSelectionForeground(TEXT);
        t.getTableHeader().setFont(BOLD);
        t.getTableHeader().setBackground(Color.WHITE);
        return t;
    }

    public static JScrollPane scroll(Component c) {
        JScrollPane s = new JScrollPane(c);
        s.setBorder(new LineBorder(LINE));
        s.getViewport().setBackground(Color.WHITE);
        return s;
    }

    public static JPanel page() {
        JPanel p = new JPanel(new BorderLayout(0, 10));
        p.setOpaque(false);
        p.setBorder(new EmptyBorder(16, 16, 16, 16));
        return p;
    }

    public static JPanel header(String title, String who, Runnable onLogout) {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(BLUE);
        p.setBorder(new EmptyBorder(12, 20, 12, 20));
        p.add(label(title, 22, true, Color.WHITE), BorderLayout.WEST);
        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        right.setOpaque(false);
        JButton out = button("Log out", Color.WHITE);
        out.setForeground(BLUE);
        out.addActionListener(e -> onLogout.run());
        right.add(label(who, 15, false, Color.WHITE));
        right.add(out);
        p.add(right, BorderLayout.EAST);
        return p;
    }
}
