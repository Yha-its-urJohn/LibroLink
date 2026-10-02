package library;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

/** Facebook-style login page with Member / Admin accounts. */
public class LoginFrame extends JFrame {
    private final Library lib;
    private final AccountModule accounts;
    private String role = "Member";
    private final JButton memberBtn = Theme.button("Member", Theme.BLUE);
    private final JButton adminBtn = Theme.button("Admin", Theme.LINE);
    private final JTextField userField = new JTextField();
    private final JPasswordField passField = new JPasswordField();
    private final JLabel msg = Theme.label(" ", 13, false, Theme.RED);

    public LoginFrame(Library lib) {
        this.lib = lib;
        this.accounts = new AccountModule(lib);
        setTitle("LibroLink - Log in");
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        // left side: brand
        JPanel brand = new JPanel();
        brand.setOpaque(false);
        brand.setLayout(new BoxLayout(brand, BoxLayout.Y_AXIS));
        JLabel logo = Theme.label("LibroLink", 56, true, Color.WHITE);
        JLabel tag = Theme.label("<html><body style='width:340px'>Borrow, return and manage books with the people who love reading.</body></html>", 24, false, new Color(225, 228, 255));
        logo.setAlignmentX(0f);
        tag.setAlignmentX(0f);
        brand.add(Box.createVerticalGlue());
        brand.add(logo);
        brand.add(Box.createVerticalStrut(8));
        brand.add(tag);
        brand.add(Box.createVerticalGlue());

        // right side: login card
        Theme.style(userField);
        Theme.style(passField);
        JPanel roles = new JPanel(new GridLayout(1, 2, 8, 0));
        roles.setOpaque(false);
        roles.add(memberBtn);
        roles.add(adminBtn);
        JButton login = Theme.button("Log In", Theme.BLUE);
        JButton create = Theme.button("Create new account", Theme.GREEN);

        JPanel card = new JPanel(new GridLayout(0, 1, 0, 10));
        card.setBackground(Color.WHITE);
        card.setBorder(new CompoundBorder(new LineBorder(Theme.LINE), new EmptyBorder(24, 24, 24, 24)));
        card.add(roles);
        card.add(labeled("Username", userField));
        card.add(labeled("Password", passField));
        card.add(login);
        card.add(msg);
        card.add(create);

        JPanel right = new JPanel(new GridBagLayout());
        right.setOpaque(false);
        GridBagConstraints g = new GridBagConstraints();
        g.weightx = 1;
        g.fill = GridBagConstraints.HORIZONTAL;
        right.add(card, g);

        AnimatedBackground root = new AnimatedBackground();
        root.setLayout(new GridLayout(1, 2, 40, 0));
        root.setBorder(new EmptyBorder(30, 60, 30, 60));
        root.add(brand);
        root.add(right);
        setContentPane(root);

        memberBtn.addActionListener(e -> setRole("Member"));
        adminBtn.addActionListener(e -> setRole("Admin"));
        login.addActionListener(e -> doLogin());
        create.addActionListener(e -> register());
        getRootPane().setDefaultButton(login);
        setRole("Member");
        setSize(1000, 700);
        setLocationRelativeTo(null);
    }

    private JPanel labeled(String text, JComponent c) {
        JPanel p = new JPanel(new BorderLayout(0, 4));
        p.setOpaque(false);
        p.add(Theme.label(text, 13, true, Theme.GRAY), BorderLayout.NORTH);
        p.add(c, BorderLayout.CENTER);
        return p;
    }

    private void setRole(String r) {
        role = r;
        memberBtn.setBackground(r.equals("Member") ? Theme.BLUE : Theme.LINE);
        memberBtn.setForeground(r.equals("Member") ? Color.WHITE : Theme.TEXT);
        adminBtn.setBackground(r.equals("Admin") ? Theme.BLUE : Theme.LINE);
        adminBtn.setForeground(r.equals("Admin") ? Color.WHITE : Theme.TEXT);
    }

    private void doLogin() {
        User u = accounts.login(userField.getText().trim(), new String(passField.getPassword()), role);
        if (u == null) {
            msg.setForeground(Theme.RED);
            msg.setText("Wrong username, password, or account type.");
            return;
        }
        dispose();
        if (u instanceof Admin) new AdminFrame(lib, (Admin) u).setVisible(true);   // POLYMORPHISM in use
        else new MemberFrame(lib, (Member) u).setVisible(true);
    }

    private void register() {
        JTextField name = new JTextField(), user = new JTextField();
        JPasswordField pass = new JPasswordField();
        Object[] form = {"Full name", name, "Username", user, "Password", pass};
        if (JOptionPane.showConfirmDialog(this, form, "Create new account", JOptionPane.OK_CANCEL_OPTION) != JOptionPane.OK_OPTION) return;
        String p = new String(pass.getPassword());
        if (name.getText().isBlank() || user.getText().isBlank() || p.isEmpty()) {
            msg.setForeground(Theme.RED);
            msg.setText("Please fill in every field.");
            return;
        }
        boolean ok = accounts.register(new Member(user.getText().trim(), p, name.getText().trim()));
        msg.setForeground(ok ? Theme.GREEN : Theme.RED);
        msg.setText(ok ? "Account created. You can log in now." : "That username is taken.");
    }
}
