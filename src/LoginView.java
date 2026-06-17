
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

public class LoginView extends JPanel {

    // ══════════════════════════════════════════════════════
    //  PHẦN 1 — FORM ĐĂNG NHẬP / ĐĂNG KÝ
    // ══════════════════════════════════════════════════════

    public interface AuthListener {
        void onLogin(String email, String password);
        void onRegister(String name, String email, String password);
    }

    private AuthListener authListener;
    private boolean      isLoginMode = true;

    private JLabel         titleLabel;
    private JLabel         subtitleLabel;
    private JLabel         messageLabel;
    private JTextField     nameField;
    private JTextField     emailField;
    private JPasswordField passwordField;
    private JButton        actionBtn;
    private JButton        switchBtn;

    public LoginView() {
        setBackground(Theme.BG_DARK);
        setLayout(new GridBagLayout());
        buildUI();
    }

    public void showRegisterForm() { if (isLoginMode) toggleMode(); }
    public void showLoginForm()    { if (!isLoginMode) toggleMode(); }
    public void displayErrorMessage(String msg) { showMessage(msg, Theme.ERROR); }

    private void buildUI() {
        JPanel card = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Theme.BG_CARD); g2.fillRoundRect(0,0,getWidth(),getHeight(),20,20);
                g2.setColor(Theme.BORDER);  g2.drawRoundRect(0,0,getWidth()-1,getHeight()-1,20,20);
                g2.dispose();
            }
        };
        card.setOpaque(false); card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setPreferredSize(new Dimension(420, 540));
        card.setBorder(BorderFactory.createEmptyBorder(40,40,40,40));

        JLabel logo = new JLabel("🎬", SwingConstants.CENTER);
        logo.setFont(new Font("SansSerif",Font.PLAIN,48)); logo.setAlignmentX(CENTER_ALIGNMENT);

        titleLabel = new JLabel("Welcome Back", SwingConstants.CENTER);
        titleLabel.setFont(Theme.fontBold(26)); titleLabel.setForeground(Theme.TEXT_PRIMARY);
        titleLabel.setAlignmentX(CENTER_ALIGNMENT);

        subtitleLabel = new JLabel("Đăng nhập để tiếp tục xem phim", SwingConstants.CENTER);
        subtitleLabel.setFont(Theme.fontPlain(13)); subtitleLabel.setForeground(Theme.TEXT_SECONDARY);
        subtitleLabel.setAlignmentX(CENTER_ALIGNMENT);

        nameField = makePlaceholderField("Họ và tên");
        nameField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 48)); nameField.setVisible(false);

        emailField = makePlaceholderField("Email");
        emailField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 48));

        passwordField = new JPasswordField() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Theme.BG_INPUT); g2.fillRoundRect(0,0,getWidth(),getHeight(),10,10);
                g2.dispose(); super.paintComponent(g);
            }
        };
        styleField(passwordField); passwordField.setEchoChar('●');
        passwordField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 48));
        passwordField.addActionListener(e -> handleAuth());

        messageLabel = new JLabel(" ");
        messageLabel.setFont(Theme.fontPlain(12)); messageLabel.setForeground(Theme.ERROR);
        messageLabel.setAlignmentX(CENTER_ALIGNMENT);

        actionBtn = makeAccentBtn("Đăng nhập");
        actionBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 48));
        actionBtn.addActionListener(e -> handleAuth());

        switchBtn = new JButton("Chưa có tài khoản? Đăng ký");
        switchBtn.setFont(Theme.fontPlain(12)); switchBtn.setForeground(Theme.ACCENT);
        switchBtn.setBackground(null); switchBtn.setBorderPainted(false);
        switchBtn.setContentAreaFilled(false);
        switchBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        switchBtn.setAlignmentX(CENTER_ALIGNMENT);
        switchBtn.addActionListener(e -> toggleMode());

        card.add(logo); card.add(Box.createVerticalStrut(12));
        card.add(titleLabel); card.add(Box.createVerticalStrut(6)); card.add(subtitleLabel);
        card.add(Box.createVerticalStrut(28)); card.add(nameField); card.add(Box.createVerticalStrut(10));
        card.add(emailField); card.add(Box.createVerticalStrut(10)); card.add(passwordField);
        card.add(Box.createVerticalStrut(6)); card.add(messageLabel); card.add(Box.createVerticalStrut(8));
        card.add(actionBtn); card.add(Box.createVerticalStrut(14)); card.add(switchBtn);
        add(card);
    }

    private void toggleMode() {
        isLoginMode = !isLoginMode;
        if (isLoginMode) {
            titleLabel.setText("Welcome Back"); subtitleLabel.setText("Đăng nhập để tiếp tục xem phim");
            actionBtn.setText("Đăng nhập"); switchBtn.setText("Chưa có tài khoản? Đăng ký");
            nameField.setVisible(false);
        } else {
            titleLabel.setText("Tạo tài khoản"); subtitleLabel.setText("Tham gia và bắt đầu xem phim");
            actionBtn.setText("Đăng ký"); switchBtn.setText("Đã có tài khoản? Đăng nhập");
            nameField.setVisible(true);
        }
        messageLabel.setText(" "); revalidate(); repaint();
    }

    private void handleAuth() {
        String email    = emailField.getText().trim();
        String password = new String(passwordField.getPassword());
        if (email.isEmpty() || password.isEmpty()) {
            displayErrorMessage("Vui lòng điền đầy đủ thông tin."); return;
        }
        if (authListener == null) return;
        if (isLoginMode) {
            authListener.onLogin(email, password);
        } else {
            String name = nameField.getText().trim();
            if (name.isEmpty()) { displayErrorMessage("Vui lòng nhập tên."); return; }
            authListener.onRegister(name, email, password);
        }
    }

    public void showMessage(String msg, Color color) {
        messageLabel.setText(msg); messageLabel.setForeground(color);
    }
    public void setAuthListener(AuthListener l) { this.authListener = l; }

    private JTextField makePlaceholderField(String ph) {
        JTextField f = new JTextField() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Theme.BG_INPUT); g2.fillRoundRect(0,0,getWidth(),getHeight(),10,10);
                g2.dispose(); super.paintComponent(g);
                if (getText().isEmpty() && !isFocusOwner()) {
                    Graphics2D pg = (Graphics2D) g.create(); pg.setColor(Theme.TEXT_MUTED); pg.setFont(getFont());
                    FontMetrics fm = pg.getFontMetrics();
                    pg.drawString(ph, getInsets().left, (getHeight()+fm.getAscent()-fm.getDescent())/2);
                    pg.dispose();
                }
            }
        };
        styleField(f); f.addActionListener(e -> handleAuth()); return f;
    }

    private void styleField(JTextField f) {
        f.setOpaque(false);
        f.setBorder(BorderFactory.createCompoundBorder(
            new RoundBorder(Theme.BORDER,10), BorderFactory.createEmptyBorder(10,14,10,14)));
        f.setFont(Theme.fontPlain(14)); f.setForeground(Theme.TEXT_PRIMARY);
        f.setCaretColor(Theme.ACCENT); f.setAlignmentX(CENTER_ALIGNMENT);
        f.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) { f.repaint(); }
            public void focusLost(FocusEvent e)   { f.repaint(); }
        });
    }

    private JButton makeAccentBtn(String text) {
        JButton btn = new JButton(text) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                Color c = getModel().isPressed() ? Theme.ACCENT_DARK
                        : getModel().isRollover() ? Theme.ACCENT.brighter() : Theme.ACCENT;
                g2.setColor(c); g2.fillRoundRect(0,0,getWidth(),getHeight(),12,12);
                g2.dispose(); super.paintComponent(g);
            }
        };
        btn.setFont(Theme.fontBold(14)); btn.setForeground(Color.WHITE);
        btn.setContentAreaFilled(false); btn.setBorderPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setAlignmentX(CENTER_ALIGNMENT); btn.setFocusPainted(false);
        return btn;
    }

    // ══════════════════════════════════════════════════════
    //  PHẦN 2 — USER PANEL (inner static class)
    //  Quản lý thông tin cá nhân: thêm/sửa/xóa/đổi mật khẩu
    // ══════════════════════════════════════════════════════
    public static class UserPanel extends JPanel {

        public interface UserListener {
            void onUpdateProfile(Member member, String newName, String newEmail);
            void onChangePassword(Member member, String oldPass, String newPass);
            void onDeleteAccount(Member member);
        }

        private UserListener userListener;
        private Member       currentMember;

        // Các trường hiển thị thông tin
        private JLabel   avatarLabel;
        private JLabel   nameDisplayLabel;
        private JLabel   emailDisplayLabel;
        private JLabel   statusDisplayLabel;

        // Các trường chỉnh sửa
        private JTextField     nameEditField;
        private JTextField     emailEditField;
        private JPasswordField oldPassField;
        private JPasswordField newPassField;
        private JPasswordField confirmPassField;
        private JLabel         profileMsgLabel;
        private JLabel         passMsgLabel;

        public UserPanel() {
            setBackground(Theme.BG_DARK);
            setLayout(new BorderLayout());
        }

        /**
         * Nạp thông tin Member vào panel và xây dựng UI.
         * Gọi phương thức này sau khi đăng nhập thành công.
         */
        public void loadMember(Member member) {
            this.currentMember = member;
            removeAll();
            buildUI();
            revalidate(); repaint();
        }

        private void buildUI() {
            // Header
            JPanel header = new JPanel(new BorderLayout());
            header.setBackground(Theme.BG_DARK);
            header.setBorder(BorderFactory.createEmptyBorder(24,32,12,32));
            JLabel title = new JLabel("👤  Tài khoản của tôi");
            title.setFont(Theme.fontBold(22)); title.setForeground(Theme.TEXT_PRIMARY);
            JLabel sub = new JLabel("Quản lý thông tin cá nhân và bảo mật");
            sub.setFont(Theme.fontPlain(13)); sub.setForeground(Theme.TEXT_SECONDARY);
            JPanel tBox = new JPanel(); tBox.setOpaque(false);
            tBox.setLayout(new BoxLayout(tBox, BoxLayout.Y_AXIS));
            tBox.add(title); tBox.add(Box.createVerticalStrut(3)); tBox.add(sub);
            header.add(tBox, BorderLayout.WEST);

            // Nội dung chính: 2 cột
            JPanel body = new JPanel(new GridLayout(1, 2, 20, 0));
            body.setBackground(Theme.BG_DARK);
            body.setBorder(BorderFactory.createEmptyBorder(0, 32, 32, 32));
            body.add(buildInfoCard());
            body.add(buildRightColumn());

            JPanel wrap = new JPanel(new BorderLayout());
            wrap.setBackground(Theme.BG_DARK);
            wrap.add(header, BorderLayout.NORTH);
            wrap.add(body,   BorderLayout.CENTER);

            JScrollPane sp = new JScrollPane(wrap);
            sp.setBackground(Theme.BG_DARK); sp.getViewport().setBackground(Theme.BG_DARK);
            sp.setBorder(null); sp.getVerticalScrollBar().setUnitIncrement(12);
            add(sp, BorderLayout.CENTER);
        }

        // ── Cột trái: Thông tin hiện tại ──
        private JPanel buildInfoCard() {
            JPanel card = roundCard();
            card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));

            avatarLabel = new JLabel("👤", SwingConstants.CENTER);
            avatarLabel.setFont(new Font("SansSerif", Font.PLAIN, 64));
            avatarLabel.setAlignmentX(CENTER_ALIGNMENT);

            nameDisplayLabel = new JLabel(currentMember.getEmail(), SwingConstants.CENTER);
            nameDisplayLabel.setFont(Theme.fontBold(17));
            nameDisplayLabel.setForeground(Theme.TEXT_PRIMARY);
            nameDisplayLabel.setAlignmentX(CENTER_ALIGNMENT);

            emailDisplayLabel = new JLabel(currentMember.getEmail(), SwingConstants.CENTER);
            emailDisplayLabel.setFont(Theme.fontPlain(13));
            emailDisplayLabel.setForeground(Theme.TEXT_SECONDARY);
            emailDisplayLabel.setAlignmentX(CENTER_ALIGNMENT);

            JSeparator sep = new JSeparator();
            sep.setForeground(Theme.BORDER); sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));

            String status = currentMember.getAccountStatus();
            statusDisplayLabel = new JLabel(status, SwingConstants.CENTER);
            statusDisplayLabel.setFont(Theme.fontBold(20));
            statusDisplayLabel.setForeground("VIP".equalsIgnoreCase(status) ? Theme.VIP : Theme.ACCENT);
            statusDisplayLabel.setAlignmentX(CENTER_ALIGNMENT);

            JLabel statusTitle = new JLabel("Trạng thái tài khoản", SwingConstants.CENTER);
            statusTitle.setFont(Theme.fontPlain(12)); statusTitle.setForeground(Theme.TEXT_SECONDARY);
            statusTitle.setAlignmentX(CENTER_ALIGNMENT);

            JLabel idLabel = new JLabel("Member ID: #" + currentMember.getId(), SwingConstants.CENTER);
            idLabel.setFont(Theme.fontPlain(12)); idLabel.setForeground(Theme.TEXT_MUTED);
            idLabel.setAlignmentX(CENTER_ALIGNMENT);

            card.add(Box.createVerticalStrut(20));
            card.add(avatarLabel);
            card.add(Box.createVerticalStrut(12));
            card.add(nameDisplayLabel);
            card.add(Box.createVerticalStrut(4));
            card.add(emailDisplayLabel);
            card.add(Box.createVerticalStrut(4));
            card.add(idLabel);
            card.add(Box.createVerticalStrut(20));
            card.add(sep);
            card.add(Box.createVerticalStrut(16));
            card.add(statusTitle);
            card.add(Box.createVerticalStrut(6));
            card.add(statusDisplayLabel);
            card.add(Box.createVerticalStrut(20));
            return card;
        }

        // ── Cột phải: Form chỉnh sửa ──
        private JPanel buildRightColumn() {
            JPanel col = new JPanel();
            col.setBackground(Theme.BG_DARK);
            col.setLayout(new BoxLayout(col, BoxLayout.Y_AXIS));

            col.add(buildEditProfileCard());
            col.add(Box.createVerticalStrut(16));
            col.add(buildChangePasswordCard());
            col.add(Box.createVerticalStrut(16));
            col.add(buildDangerZoneCard());
            return col;
        }

        // ── Card 1: Cập nhật thông tin ──
        private JPanel buildEditProfileCard() {
            JPanel card = roundCard();
            card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));

            JLabel title = new JLabel("✏️  Cập nhật thông tin");
            title.setFont(Theme.fontBold(15)); title.setForeground(Theme.TEXT_PRIMARY);
            title.setAlignmentX(LEFT_ALIGNMENT);

            nameEditField  = inputField("Tên hiển thị mới");
            emailEditField = inputField("Email mới");

            profileMsgLabel = new JLabel(" ");
            profileMsgLabel.setFont(Theme.fontPlain(12));
            profileMsgLabel.setForeground(Theme.SUCCESS);
            profileMsgLabel.setAlignmentX(LEFT_ALIGNMENT);

            JButton saveBtn = colorBtn("💾  Lưu thay đổi", Theme.ACCENT);
            saveBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
            saveBtn.setAlignmentX(LEFT_ALIGNMENT);
            saveBtn.addActionListener(e -> handleUpdateProfile());

            card.add(title);
            card.add(Box.createVerticalStrut(14));
            card.add(labelRow("Tên mới"));  card.add(Box.createVerticalStrut(6));
            card.add(nameEditField);        card.add(Box.createVerticalStrut(10));
            card.add(labelRow("Email mới")); card.add(Box.createVerticalStrut(6));
            card.add(emailEditField);       card.add(Box.createVerticalStrut(10));
            card.add(profileMsgLabel);      card.add(Box.createVerticalStrut(8));
            card.add(saveBtn);
            return card;
        }

        // ── Card 2: Đổi mật khẩu ──
        private JPanel buildChangePasswordCard() {
            JPanel card = roundCard();
            card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));

            JLabel title = new JLabel("🔑  Đổi mật khẩu");
            title.setFont(Theme.fontBold(15)); title.setForeground(Theme.TEXT_PRIMARY);
            title.setAlignmentX(LEFT_ALIGNMENT);

            oldPassField     = passField("Mật khẩu hiện tại");
            newPassField     = passField("Mật khẩu mới (tối thiểu 6 ký tự)");
            confirmPassField = passField("Xác nhận mật khẩu mới");

            passMsgLabel = new JLabel(" ");
            passMsgLabel.setFont(Theme.fontPlain(12)); passMsgLabel.setForeground(Theme.SUCCESS);
            passMsgLabel.setAlignmentX(LEFT_ALIGNMENT);

            JButton changeBtn = colorBtn("🔒  Đổi mật khẩu", new Color(59, 130, 246));
            changeBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
            changeBtn.setAlignmentX(LEFT_ALIGNMENT);
            changeBtn.addActionListener(e -> handleChangePassword());

            card.add(title);
            card.add(Box.createVerticalStrut(14));
            card.add(labelRow("Mật khẩu hiện tại")); card.add(Box.createVerticalStrut(6));
            card.add(oldPassField);                   card.add(Box.createVerticalStrut(10));
            card.add(labelRow("Mật khẩu mới"));       card.add(Box.createVerticalStrut(6));
            card.add(newPassField);                   card.add(Box.createVerticalStrut(10));
            card.add(labelRow("Xác nhận lại"));        card.add(Box.createVerticalStrut(6));
            card.add(confirmPassField);               card.add(Box.createVerticalStrut(10));
            card.add(passMsgLabel);                   card.add(Box.createVerticalStrut(8));
            card.add(changeBtn);
            return card;
        }

        // ── Card 3: Xóa tài khoản ──
        private JPanel buildDangerZoneCard() {
            JPanel card = new JPanel() {
                @Override protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(new Color(40, 15, 15)); g2.fillRoundRect(0,0,getWidth(),getHeight(),12,12);
                    g2.setColor(new Color(120, 40, 40)); g2.drawRoundRect(0,0,getWidth()-1,getHeight()-1,12,12);
                    g2.dispose();
                }
            };
            card.setOpaque(false); card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
            card.setBorder(BorderFactory.createEmptyBorder(18,20,18,20));
            card.setAlignmentX(LEFT_ALIGNMENT);

            JLabel title = new JLabel("⚠️  Vùng nguy hiểm");
            title.setFont(Theme.fontBold(15)); title.setForeground(Theme.ERROR);
            title.setAlignmentX(LEFT_ALIGNMENT);

            JLabel warn = new JLabel("<html>Xóa tài khoản sẽ không thể hoàn tác.<br>Tất cả dữ liệu của bạn sẽ bị xóa vĩnh viễn.</html>");
            warn.setFont(Theme.fontPlain(12)); warn.setForeground(Theme.TEXT_SECONDARY);
            warn.setAlignmentX(LEFT_ALIGNMENT);

            JButton deleteBtn = colorBtn("🗑  Xóa tài khoản của tôi", Theme.ERROR);
            deleteBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
            deleteBtn.setAlignmentX(LEFT_ALIGNMENT);
            deleteBtn.addActionListener(e -> handleDeleteAccount());

            card.add(title);
            card.add(Box.createVerticalStrut(10));
            card.add(warn);
            card.add(Box.createVerticalStrut(14));
            card.add(deleteBtn);
            return card;
        }

        // ══════════════════════════════════════════
        //  XỬ LÝ SỰ KIỆN
        // ══════════════════════════════════════════

        private void handleUpdateProfile() {
            String newName  = nameEditField.getText().trim();
            String newEmail = emailEditField.getText().trim();

            if (newName.isEmpty() && newEmail.isEmpty()) {
                setProfileMsg("⚠️  Vui lòng nhập ít nhất một thông tin cần thay đổi.", Theme.WARNING);
                return;
            }
            if (!newEmail.isEmpty() && !newEmail.contains("@")) {
                setProfileMsg("❌  Email không hợp lệ.", Theme.ERROR); return;
            }

            // Áp dụng thay đổi
            if (!newEmail.isEmpty()) {
                currentMember.setEmail(newEmail);
                emailDisplayLabel.setText(newEmail);
                nameDisplayLabel.setText(newEmail);
            }

            // Xóa trắng ô nhập
            nameEditField.setText(""); emailEditField.setText("");

            if (userListener != null)
                userListener.onUpdateProfile(currentMember, newName, newEmail);

            setProfileMsg("✅  Cập nhật thông tin thành công!", Theme.SUCCESS);
        }

        private void handleChangePassword() {
            String oldPass  = new String(oldPassField.getPassword());
            String newPass  = new String(newPassField.getPassword());
            String confirm  = new String(confirmPassField.getPassword());

            if (oldPass.isEmpty() || newPass.isEmpty() || confirm.isEmpty()) {
                setPassMsg("⚠️  Vui lòng điền đầy đủ tất cả các ô.", Theme.WARNING); return;
            }
            if (!oldPass.equals(currentMember.getPassword())) {
                setPassMsg("❌  Mật khẩu hiện tại không đúng.", Theme.ERROR); return;
            }
            if (newPass.length() < 6) {
                setPassMsg("❌  Mật khẩu mới phải có ít nhất 6 ký tự.", Theme.ERROR); return;
            }
            if (!newPass.equals(confirm)) {
                setPassMsg("❌  Mật khẩu xác nhận không khớp.", Theme.ERROR); return;
            }
            if (newPass.equals(oldPass)) {
                setPassMsg("⚠️  Mật khẩu mới không được trùng mật khẩu cũ.", Theme.WARNING); return;
            }

            // Đổi mật khẩu thành công
            currentMember.setPassword(newPass);
            oldPassField.setText(""); newPassField.setText(""); confirmPassField.setText("");

            if (userListener != null)
                userListener.onChangePassword(currentMember, oldPass, newPass);

            setPassMsg("✅  Đổi mật khẩu thành công!", Theme.SUCCESS);
        }

        private void handleDeleteAccount() {
            int confirm = JOptionPane.showConfirmDialog(
                this,
                "<html><b>Bạn chắc chắn muốn xóa tài khoản?</b><br>" +
                "Email: <b>" + currentMember.getEmail() + "</b><br><br>" +
                "<font color='red'>Hành động này KHÔNG THỂ hoàn tác!</font></html>",
                "⚠️  Xác nhận xóa tài khoản",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
            );
            if (confirm == JOptionPane.YES_OPTION) {
                if (userListener != null) userListener.onDeleteAccount(currentMember);
            }
        }

        // ── Cập nhật hiển thị sau thay đổi từ bên ngoài ──
        public void refreshDisplay() {
            if (currentMember == null) return;
            emailDisplayLabel.setText(currentMember.getEmail());
            nameDisplayLabel.setText(currentMember.getEmail());
            String status = currentMember.getAccountStatus();
            statusDisplayLabel.setText(status);
            statusDisplayLabel.setForeground("VIP".equalsIgnoreCase(status) ? Theme.VIP : Theme.ACCENT);
            repaint();
        }

        private void setProfileMsg(String msg, Color color) {
            profileMsgLabel.setText(msg); profileMsgLabel.setForeground(color);
        }
        private void setPassMsg(String msg, Color color) {
            passMsgLabel.setText(msg); passMsgLabel.setForeground(color);
        }

        public void setUserListener(UserListener l) { this.userListener = l; }
        public Member getCurrentMember()            { return currentMember; }

        // ══════════════════════════════════════════
        //  HELPERS UI
        // ══════════════════════════════════════════
        private JPanel roundCard() {
            JPanel p = new JPanel() {
                @Override protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(Theme.BG_CARD); g2.fillRoundRect(0,0,getWidth(),getHeight(),12,12);
                    g2.setColor(Theme.BORDER);  g2.drawRoundRect(0,0,getWidth()-1,getHeight()-1,12,12);
                    g2.dispose();
                }
            };
            p.setOpaque(false); p.setBorder(BorderFactory.createEmptyBorder(20,20,20,20));
            p.setAlignmentX(LEFT_ALIGNMENT); return p;
        }

        private JLabel labelRow(String text) {
            JLabel l = new JLabel(text);
            l.setFont(Theme.fontBold(12)); l.setForeground(Theme.TEXT_SECONDARY);
            l.setAlignmentX(LEFT_ALIGNMENT); return l;
        }

        private JTextField inputField(String ph) {
            JTextField f = new JTextField() {
                @Override protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(Theme.BG_INPUT); g2.fillRoundRect(0,0,getWidth(),getHeight(),8,8);
                    g2.dispose(); super.paintComponent(g);
                    if (getText().isEmpty() && !isFocusOwner()) {
                        Graphics2D pg = (Graphics2D) g.create(); pg.setColor(Theme.TEXT_MUTED); pg.setFont(getFont());
                        FontMetrics fm = pg.getFontMetrics();
                        pg.drawString(ph, getInsets().left, (getHeight()+fm.getAscent()-fm.getDescent())/2);
                        pg.dispose();
                    }
                }
            };
            f.setOpaque(false); f.setFont(Theme.fontPlain(13));
            f.setForeground(Theme.TEXT_PRIMARY); f.setCaretColor(Theme.ACCENT);
            f.setBorder(BorderFactory.createCompoundBorder(
                new RoundBorder(Theme.BORDER,8), BorderFactory.createEmptyBorder(9,12,9,12)));
            f.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
            f.setAlignmentX(LEFT_ALIGNMENT);
            f.addFocusListener(new FocusAdapter() {
                public void focusGained(FocusEvent e) { f.repaint(); }
                public void focusLost(FocusEvent e)   { f.repaint(); }
            });
            return f;
        }

        private JPasswordField passField(String ph) {
            JPasswordField f = new JPasswordField() {
                @Override protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(Theme.BG_INPUT); g2.fillRoundRect(0,0,getWidth(),getHeight(),8,8);
                    g2.dispose(); super.paintComponent(g);
                    if (getPassword().length == 0 && !isFocusOwner()) {
                        Graphics2D pg = (Graphics2D) g.create(); pg.setColor(Theme.TEXT_MUTED); pg.setFont(getFont());
                        FontMetrics fm = pg.getFontMetrics();
                        pg.drawString(ph, getInsets().left, (getHeight()+fm.getAscent()-fm.getDescent())/2);
                        pg.dispose();
                    }
                }
            };
            f.setEchoChar('●'); f.setOpaque(false); f.setFont(Theme.fontPlain(13));
            f.setForeground(Theme.TEXT_PRIMARY); f.setCaretColor(Theme.ACCENT);
            f.setBorder(BorderFactory.createCompoundBorder(
                new RoundBorder(Theme.BORDER,8), BorderFactory.createEmptyBorder(9,12,9,12)));
            f.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
            f.setAlignmentX(LEFT_ALIGNMENT);
            f.addFocusListener(new FocusAdapter() {
                public void focusGained(FocusEvent e) { f.repaint(); }
                public void focusLost(FocusEvent e)   { f.repaint(); }
            });
            return f;
        }

        private JButton colorBtn(String text, Color color) {
            JButton b = new JButton(text) {
                @Override protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    Color bg = getModel().isRollover() ? color.brighter() : color;
                    g2.setColor(new Color(bg.getRed(),bg.getGreen(),bg.getBlue(),220));
                    g2.fillRoundRect(0,0,getWidth(),getHeight(),8,8);
                    g2.dispose(); super.paintComponent(g);
                }
            };
            b.setFont(Theme.fontBold(13)); b.setForeground(Color.WHITE);
            b.setContentAreaFilled(false); b.setBorderPainted(false);
            b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            b.setBorder(BorderFactory.createEmptyBorder(10,16,10,16));
            b.setFocusPainted(false); return b;
        }
    }

    // ══════════════════════════════════════════════════════
    //  PHẦN 3 — ADMIN PANEL (inner static class)
    // ══════════════════════════════════════════════════════
    public static class AdminPanel extends JPanel {

        public interface AdminListener {
            void onAddMovie(Movie movie);
            void onDeleteMovie(Movie movie);
            void onUpdateMovie(Movie movie);
            void onLockAccount(User user);
            void onUnlockAccount(User user);
            void onWarnUser(User user, String reason);
        }

        private AdminListener listener;
        private DefaultTableModel movieTableModel;
        private DefaultTableModel userTableModel;
        private JTable movieTable;
        private JTable userTable;
        private JLabel totalMoviesLbl, totalUsersLbl, vipUsersLbl, lockedUsersLbl;
        private List<Movie>  movies = new ArrayList<>();
        private List<Member> users  = new ArrayList<>();

        public AdminPanel() { setBackground(Theme.BG_DARK); setLayout(new BorderLayout()); buildUI(); }

        private void buildUI() {
            JPanel header = new JPanel(new BorderLayout());
            header.setBackground(Theme.BG_DARK); header.setBorder(BorderFactory.createEmptyBorder(24,32,12,32));
            JLabel title = new JLabel("🛠  Admin Dashboard");
            title.setFont(Theme.fontBold(22)); title.setForeground(Theme.TEXT_PRIMARY);
            JLabel sub = new JLabel("Quản lý phim và tài khoản người dùng");
            sub.setFont(Theme.fontPlain(13)); sub.setForeground(Theme.TEXT_SECONDARY);
            JPanel tBox = new JPanel(); tBox.setOpaque(false);
            tBox.setLayout(new BoxLayout(tBox, BoxLayout.Y_AXIS));
            tBox.add(title); tBox.add(Box.createVerticalStrut(3)); tBox.add(sub);
            header.add(tBox, BorderLayout.WEST);
            JPanel top = new JPanel(new BorderLayout()); top.setBackground(Theme.BG_DARK);
            top.add(header, BorderLayout.NORTH); top.add(buildStats(), BorderLayout.CENTER);
            add(top, BorderLayout.NORTH); add(buildTabs(), BorderLayout.CENTER);
        }

        private JPanel buildStats() {
            JPanel row = new JPanel(new GridLayout(1,4,12,0));
            row.setBackground(Theme.BG_DARK); row.setBorder(BorderFactory.createEmptyBorder(0,32,16,32));
            totalMoviesLbl=new JLabel("0"); totalUsersLbl=new JLabel("0");
            vipUsersLbl=new JLabel("0"); lockedUsersLbl=new JLabel("0");
            row.add(statCard("🎬  Tổng phim",   totalMoviesLbl, Theme.ACCENT));
            row.add(statCard("👥  Người dùng",  totalUsersLbl,  Theme.SUCCESS));
            row.add(statCard("👑  VIP",          vipUsersLbl,    Theme.VIP));
            row.add(statCard("🔒  Bị khóa",      lockedUsersLbl, Theme.ERROR));
            return row;
        }

        private JPanel statCard(String label, JLabel valLbl, Color accent) {
            JPanel c = new JPanel() {
                @Override protected void paintComponent(Graphics g) {
                    Graphics2D g2=(Graphics2D)g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(Theme.BG_CARD); g2.fillRoundRect(0,0,getWidth(),getHeight(),12,12);
                    g2.setColor(accent); g2.fillRoundRect(0,12,4,getHeight()-24,4,4); g2.dispose();
                }
            };
            c.setOpaque(false); c.setLayout(new BoxLayout(c,BoxLayout.Y_AXIS));
            c.setBorder(BorderFactory.createEmptyBorder(16,20,16,16));
            JLabel lbl=new JLabel(label); lbl.setFont(Theme.fontPlain(12)); lbl.setForeground(Theme.TEXT_SECONDARY);
            valLbl.setFont(Theme.fontBold(28)); valLbl.setForeground(accent);
            c.add(lbl); c.add(Box.createVerticalStrut(4)); c.add(valLbl); return c;
        }

        private JTabbedPane buildTabs() {
            JTabbedPane tabs = new JTabbedPane();
            tabs.setFont(Theme.fontBold(13)); tabs.setBorder(BorderFactory.createEmptyBorder(0,24,24,24));
            tabs.addTab("🎬  Quản lý Phim",       buildMovieTab());
            tabs.addTab("👥  Quản lý Người dùng", buildUserTab());
            return tabs;
        }

        private JPanel buildMovieTab() {
            JPanel p=new JPanel(new BorderLayout(0,10)); p.setBackground(Theme.BG_DARK); p.setBorder(BorderFactory.createEmptyBorder(14,8,8,8));
            JPanel toolbar=new JPanel(new BorderLayout()); toolbar.setBackground(Theme.BG_DARK);
            JLabel t=new JLabel("Danh sách phim"); t.setFont(Theme.fontBold(15)); t.setForeground(Theme.TEXT_PRIMARY);
            JButton addBtn=accentBtn("＋  Thêm",   Theme.ACCENT);
            JButton editBtn=accentBtn("✏️  Sửa",   new Color(59,130,246));
            JButton delBtn=accentBtn("🗑  Xóa",    Theme.ERROR);
            addBtn.addActionListener(e->showAddMovieDialog());
            editBtn.addActionListener(e->showEditMovieDialog());
            delBtn.addActionListener(e->deleteSelectedMovie());
            JPanel btns=new JPanel(new FlowLayout(FlowLayout.RIGHT,8,0)); btns.setOpaque(false);
            btns.add(delBtn); btns.add(editBtn); btns.add(addBtn);
            toolbar.add(t,BorderLayout.WEST); toolbar.add(btns,BorderLayout.EAST);
            String[] cols={"ID","Tên phim","Đạo diễn","Diễn viên","Thể loại","Quốc gia","Link","VIP"};
            movieTableModel=new DefaultTableModel(cols,0){public boolean isCellEditable(int r,int c){return false;}};
            movieTable=styledTable(movieTableModel);
            movieTable.getColumnModel().getColumn(0).setPreferredWidth(35);
            movieTable.getColumnModel().getColumn(1).setPreferredWidth(170);
            movieTable.getColumnModel().getColumn(7).setPreferredWidth(40);
            p.add(toolbar,BorderLayout.NORTH); p.add(styledScroll(movieTable),BorderLayout.CENTER); return p;
        }

        private JPanel buildUserTab() {
            JPanel p=new JPanel(new BorderLayout(0,10)); p.setBackground(Theme.BG_DARK); p.setBorder(BorderFactory.createEmptyBorder(14,8,8,8));
            JPanel toolbar=new JPanel(new BorderLayout()); toolbar.setBackground(Theme.BG_DARK);
            JLabel t=new JLabel("Tài khoản người dùng"); t.setFont(Theme.fontBold(15)); t.setForeground(Theme.TEXT_PRIMARY);
            JButton lockBtn=accentBtn("🔒  Khóa",       Theme.ERROR);
            JButton unlockBtn=accentBtn("🔓  Mở khóa",  Theme.SUCCESS);
            JButton warnBtn=accentBtn("⚠️  Cảnh báo",   new Color(202,138,4));
            lockBtn.addActionListener(e->toggleLock(true));
            unlockBtn.addActionListener(e->toggleLock(false));
            warnBtn.addActionListener(e->warnSelectedUser());
            JPanel btns=new JPanel(new FlowLayout(FlowLayout.RIGHT,8,0)); btns.setOpaque(false);
            btns.add(warnBtn); btns.add(lockBtn); btns.add(unlockBtn);
            toolbar.add(t,BorderLayout.WEST); toolbar.add(btns,BorderLayout.EAST);
            String[] cols={"ID","Email","Trạng thái","Hết hạn VIP","Bị khóa"};
            userTableModel=new DefaultTableModel(cols,0){public boolean isCellEditable(int r,int c){return false;}};
            userTable=styledTable(userTableModel);
            userTable.getColumnModel().getColumn(0).setPreferredWidth(40);
            userTable.getColumnModel().getColumn(1).setPreferredWidth(220);
            p.add(toolbar,BorderLayout.NORTH); p.add(styledScroll(userTable),BorderLayout.CENTER); return p;
        }

        private void showAddMovieDialog() {
            JDialog d=new JDialog((Frame)SwingUtilities.getWindowAncestor(this),"Thêm phim mới",true);
            d.setSize(480,510); d.setLocationRelativeTo(this); d.getContentPane().setBackground(Theme.BG_CARD);
            JPanel form=new JPanel(); form.setBackground(Theme.BG_CARD);
            form.setLayout(new BoxLayout(form,BoxLayout.Y_AXIS)); form.setBorder(BorderFactory.createEmptyBorder(28,32,28,32));
            JLabel title=new JLabel("Thêm phim mới"); title.setFont(Theme.fontBold(18)); title.setForeground(Theme.TEXT_PRIMARY); title.setAlignmentX(LEFT_ALIGNMENT);
            JTextField nF=dF("Tên phim"), dirF=dF("Đạo diễn"), actF=dF("Diễn viên"), cntF=dF("Quốc gia"), catF=dF("Thể loại"), lnkF=dF("Link phim");
            JCheckBox vip=new JCheckBox("Chỉ VIP"); vip.setFont(Theme.fontPlain(13)); vip.setForeground(Theme.TEXT_PRIMARY); vip.setBackground(Theme.BG_CARD); vip.setAlignmentX(LEFT_ALIGNMENT);
            JLabel err=new JLabel(" "); err.setFont(Theme.fontPlain(12)); err.setForeground(Theme.ERROR); err.setAlignmentX(LEFT_ALIGNMENT);
            JButton save=accentBtn("Lưu phim",Theme.ACCENT); save.setMaximumSize(new Dimension(Integer.MAX_VALUE,44)); save.setAlignmentX(LEFT_ALIGNMENT);
            save.addActionListener(e->{
                if(nF.getText().trim().isEmpty()){err.setText("Tên phim không được trống."); return;}
                Movie m=new Movie(movies.size()+1,nF.getText().trim(),dirF.getText().trim(),actF.getText().trim(),
                    new Category(0,catF.getText().isEmpty()?"Chung":catF.getText().trim()),cntF.getText().trim(),
                    lnkF.getText().isEmpty()?"/videos/"+nF.getText().trim().toLowerCase().replace(" ","-"):lnkF.getText().trim(),vip.isSelected());
                if(listener!=null) listener.onAddMovie(m); d.dispose();
            });
            form.add(title); form.add(Box.createVerticalStrut(18));
            for(Object[] r:new Object[][]{{" Tên phim",nF},{"Đạo diễn",dirF},{"Diễn viên",actF},{"Quốc gia",cntF},{"Thể loại",catF},{"Link",lnkF}})
            { form.add(fRow((String)r[0],(JTextField)r[1])); form.add(Box.createVerticalStrut(8)); }
            form.add(vip); form.add(Box.createVerticalStrut(6)); form.add(err); form.add(Box.createVerticalStrut(12)); form.add(save);
            JScrollPane sp=new JScrollPane(form); sp.setBorder(null); sp.getViewport().setBackground(Theme.BG_CARD);
            d.add(sp); d.setVisible(true);
        }

        private void showEditMovieDialog() {
            int row=movieTable.getSelectedRow();
            if(row<0){JOptionPane.showMessageDialog(this,"Vui lòng chọn phim cần sửa.","Chưa chọn",JOptionPane.WARNING_MESSAGE); return;}
            Movie target=movies.stream().filter(m->m.getNameMovie().equals(movieTableModel.getValueAt(row,1))).findFirst().orElse(null);
            if(target==null) return;
            JDialog d=new JDialog((Frame)SwingUtilities.getWindowAncestor(this),"Cập nhật phim",true);
            d.setSize(480,510); d.setLocationRelativeTo(this); d.getContentPane().setBackground(Theme.BG_CARD);
            JPanel form=new JPanel(); form.setBackground(Theme.BG_CARD);
            form.setLayout(new BoxLayout(form,BoxLayout.Y_AXIS)); form.setBorder(BorderFactory.createEmptyBorder(28,32,28,32));
            JLabel title=new JLabel("Cập nhật phim"); title.setFont(Theme.fontBold(18)); title.setForeground(Theme.TEXT_PRIMARY); title.setAlignmentX(LEFT_ALIGNMENT);
            JTextField nF=dF("Tên phim"),dirF=dF("Đạo diễn"),actF=dF("Diễn viên"),cntF=dF("Quốc gia"),catF=dF("Thể loại"),lnkF=dF("Link");
            nF.setText(target.getNameMovie()); dirF.setText(target.getDirector()); actF.setText(target.getActor());
            cntF.setText(target.getCountry()); if(target.getCategory()!=null)catF.setText(target.getCategory().getCategory()); lnkF.setText(target.getLink());
            JCheckBox vip=new JCheckBox("Chỉ VIP"); vip.setSelected(target.isVip()); vip.setFont(Theme.fontPlain(13)); vip.setForeground(Theme.TEXT_PRIMARY); vip.setBackground(Theme.BG_CARD); vip.setAlignmentX(LEFT_ALIGNMENT);
            JLabel err=new JLabel(" "); err.setFont(Theme.fontPlain(12)); err.setForeground(Theme.ERROR); err.setAlignmentX(LEFT_ALIGNMENT);
            JButton save=accentBtn("Lưu thay đổi",new Color(59,130,246)); save.setMaximumSize(new Dimension(Integer.MAX_VALUE,44)); save.setAlignmentX(LEFT_ALIGNMENT);
            save.addActionListener(e->{
                if(nF.getText().trim().isEmpty()){err.setText("Tên phim không được trống."); return;}
                target.setNameMovie(nF.getText().trim()); target.setDirector(dirF.getText().trim()); target.setActor(actF.getText().trim());
                target.setCountry(cntF.getText().trim()); target.setCategory(new Category(catF.getText().trim(),target.getCategory()!=null?target.getCategory().getId():0));
                target.setLink(lnkF.getText().trim()); target.setVip(vip.isSelected());
                if(listener!=null) listener.onUpdateMovie(target); d.dispose();
            });
            form.add(title); form.add(Box.createVerticalStrut(18));
            for(Object[] r:new Object[][]{{" Tên phim",nF},{"Đạo diễn",dirF},{"Diễn viên",actF},{"Quốc gia",cntF},{"Thể loại",catF},{"Link",lnkF}})
            { form.add(fRow((String)r[0],(JTextField)r[1])); form.add(Box.createVerticalStrut(8)); }
            form.add(vip); form.add(Box.createVerticalStrut(6)); form.add(err); form.add(Box.createVerticalStrut(12)); form.add(save);
            JScrollPane sp=new JScrollPane(form); sp.setBorder(null); sp.getViewport().setBackground(Theme.BG_CARD);
            d.add(sp); d.setVisible(true);
        }

        private void deleteSelectedMovie() {
            int row=movieTable.getSelectedRow();
            if(row<0){JOptionPane.showMessageDialog(this,"Vui lòng chọn phim cần xóa.","Chưa chọn",JOptionPane.WARNING_MESSAGE); return;}
            String name=(String)movieTableModel.getValueAt(row,1);
            if(JOptionPane.showConfirmDialog(this,"Xóa phim: \""+name+"\"?","Xác nhận",JOptionPane.YES_NO_OPTION,JOptionPane.WARNING_MESSAGE)==JOptionPane.YES_OPTION){
                Movie t=movies.stream().filter(m->m.getNameMovie().equals(name)).findFirst().orElse(null);
                if(t!=null && listener!=null) listener.onDeleteMovie(t);
            }
        }

        private void warnSelectedUser() {
            int row=userTable.getSelectedRow();
            if(row<0){JOptionPane.showMessageDialog(this,"Vui lòng chọn người dùng.","Chưa chọn",JOptionPane.WARNING_MESSAGE); return;}
            String email=(String)userTableModel.getValueAt(row,1);
            Member target=users.stream().filter(u->u.getEmail().equals(email)).findFirst().orElse(null);
            if(target==null) return;
            String[] reasons={"Bình luận xúc phạm","Spam bình luận","Nội dung không phù hợp","Vi phạm cộng đồng"};
            String reason=(String)JOptionPane.showInputDialog(this,"Lý do cảnh báo cho: "+email,"⚠️  Cảnh báo",JOptionPane.WARNING_MESSAGE,null,reasons,reasons[0]);
            if(reason!=null && listener!=null) listener.onWarnUser(target,reason);
        }

        private void toggleLock(boolean lock) {
            int row=userTable.getSelectedRow();
            if(row<0){JOptionPane.showMessageDialog(this,"Vui lòng chọn người dùng.","Chưa chọn",JOptionPane.WARNING_MESSAGE); return;}
            String email=(String)userTableModel.getValueAt(row,1);
            Member t=users.stream().filter(u->u.getEmail().equals(email)).findFirst().orElse(null);
            if(t==null) return;
            t.setAccountStatus(lock?"LOCKED":"Regular");
            if(listener!=null){if(lock)listener.onLockAccount(t); else listener.onUnlockAccount(t);}
            refreshUserTable();
        }

        public void loadMovies(List<Movie> list) {
            this.movies=list; movieTableModel.setRowCount(0);
            for(Movie m:list) movieTableModel.addRow(new Object[]{m.getId(),m.getNameMovie(),m.getDirector(),m.getActor(),
                m.getCategory()!=null?m.getCategory().getCategory():"-",m.getCountry(),m.getLink(),m.isVip()?"✓":""});
            totalMoviesLbl.setText(String.valueOf(list.size())); updateStats();
        }

        public void loadUsers(List<Member> list) { this.users=list; refreshUserTable(); }

        private void refreshUserTable() {
            userTableModel.setRowCount(0);
            for(Member u:users) userTableModel.addRow(new Object[]{u.getId(),u.getEmail(),u.getAccountStatus(),
                u.getExpiredVIP()!=null?u.getExpiredVIP().toString():"-","LOCKED".equalsIgnoreCase(u.getAccountStatus())?"🔒":""});
            updateStats();
        }

        private void updateStats() {
            totalUsersLbl.setText(String.valueOf(users.size()));
            vipUsersLbl.setText(String.valueOf(users.stream().filter(u->"VIP".equalsIgnoreCase(u.getAccountStatus())).count()));
            lockedUsersLbl.setText(String.valueOf(users.stream().filter(u->"LOCKED".equalsIgnoreCase(u.getAccountStatus())).count()));
        }

        public void setAdminListener(AdminListener l) { this.listener=l; }

        private JTable styledTable(DefaultTableModel model) {
            JTable t=new JTable(model){
                @Override public Component prepareRenderer(TableCellRenderer r,int row,int col){
                    Component c=super.prepareRenderer(r,row,col);
                    if(isRowSelected(row)){c.setBackground(Theme.ACCENT_GLOW);c.setForeground(Theme.TEXT_PRIMARY);}
                    else{c.setBackground(row%2==0?Theme.BG_CARD:Theme.BG_DARK);c.setForeground(Theme.TEXT_PRIMARY);}
                    if(c instanceof JLabel)((JLabel)c).setBorder(BorderFactory.createEmptyBorder(0,10,0,10));
                    return c;
                }
            };
            t.setBackground(Theme.BG_CARD); t.setForeground(Theme.TEXT_PRIMARY); t.setFont(Theme.fontPlain(13)); t.setRowHeight(40);
            t.setShowGrid(false); t.setIntercellSpacing(new Dimension(0,1)); t.setSelectionBackground(Theme.ACCENT_GLOW); t.setFillsViewportHeight(true);
            t.getTableHeader().setBackground(Theme.BG_SIDEBAR); t.getTableHeader().setForeground(Theme.TEXT_SECONDARY);
            t.getTableHeader().setFont(Theme.fontBold(12)); t.getTableHeader().setReorderingAllowed(false);
            return t;
        }

        private JScrollPane styledScroll(JTable t) {
            JScrollPane sp=new JScrollPane(t); sp.setBackground(Theme.BG_CARD); sp.getViewport().setBackground(Theme.BG_CARD);
            sp.setBorder(new RoundBorder(Theme.BORDER,10)); sp.getVerticalScrollBar().setUnitIncrement(12); return sp;
        }

        private JButton accentBtn(String text,Color color) {
            JButton b=new JButton(text){
                @Override protected void paintComponent(Graphics g){
                    Graphics2D g2=(Graphics2D)g.create(); g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
                    Color bg=getModel().isRollover()?color.brighter():color;
                    g2.setColor(new Color(bg.getRed(),bg.getGreen(),bg.getBlue(),210)); g2.fillRoundRect(0,0,getWidth(),getHeight(),8,8);
                    g2.dispose(); super.paintComponent(g);
                }
            };
            b.setFont(Theme.fontBold(12)); b.setForeground(Color.WHITE); b.setContentAreaFilled(false); b.setBorderPainted(false);
            b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)); b.setBorder(BorderFactory.createEmptyBorder(8,14,8,14)); b.setFocusPainted(false);
            return b;
        }

        private JTextField dF(String ph) {
            JTextField f=new JTextField(){
                @Override protected void paintComponent(Graphics g){
                    Graphics2D g2=(Graphics2D)g.create(); g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(Theme.BG_INPUT); g2.fillRoundRect(0,0,getWidth(),getHeight(),8,8); g2.dispose(); super.paintComponent(g);
                    if(getText().isEmpty()&&!isFocusOwner()){
                        Graphics2D pg=(Graphics2D)g.create(); pg.setColor(Theme.TEXT_MUTED); pg.setFont(getFont());
                        FontMetrics fm=pg.getFontMetrics(); pg.drawString(ph,getInsets().left,(getHeight()+fm.getAscent()-fm.getDescent())/2); pg.dispose();
                    }
                }
            };
            f.setOpaque(false); f.setFont(Theme.fontPlain(13)); f.setForeground(Theme.TEXT_PRIMARY); f.setCaretColor(Theme.ACCENT);
            f.setBorder(BorderFactory.createCompoundBorder(new RoundBorder(Theme.BORDER,8),BorderFactory.createEmptyBorder(8,10,8,10)));
            f.addFocusListener(new FocusAdapter(){public void focusGained(FocusEvent e){f.repaint();}public void focusLost(FocusEvent e){f.repaint();}});
            return f;
        }

        private JPanel fRow(String label,JTextField field) {
            JPanel row=new JPanel(new BorderLayout(8,0)); row.setOpaque(false);
            row.setMaximumSize(new Dimension(Integer.MAX_VALUE,44)); row.setAlignmentX(LEFT_ALIGNMENT);
            JLabel lbl=new JLabel(label); lbl.setFont(Theme.fontPlain(12)); lbl.setForeground(Theme.TEXT_SECONDARY); lbl.setPreferredSize(new Dimension(72,0));
            row.add(lbl,BorderLayout.WEST); row.add(field,BorderLayout.CENTER); return row;
        }
    }

    // ── RoundBorder dùng chung toàn app ──
    public static class RoundBorder extends AbstractBorder {
        private final Color color; private final int radius;
        public RoundBorder(Color c,int r){color=c;radius=r;}
        @Override public void paintBorder(Component c,Graphics g,int x,int y,int w,int h){
            Graphics2D g2=(Graphics2D)g.create(); g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(color); g2.drawRoundRect(x,y,w-1,h-1,radius,radius); g2.dispose();
        }
        @Override public Insets getBorderInsets(Component c){return new Insets(1,1,1,1);}
    }
}
