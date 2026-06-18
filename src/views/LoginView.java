package views;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;

import models.Category;
import models.Member;
import models.Movie;
import models.User;

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

public class LoginView extends JPanel {

    // ══════════════════════════════════════════════════════
    //  PHẦN 1 — FORM ĐĂNG NHẬP / ĐĂNG KÝ
    // ══════════════════════════════════════════════════════
//	tạo interface xác thực
    public interface AuthListener {
        void onLogin(String email, String password);
        void onRegister(String name, String email, String password);
    }

    private AuthListener authListener;
    private boolean      isLoginMode = true;
//  khởi tạo các components trong giao diện
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
//  các phương thức chính
    public void showRegisterForm() {
    	if (isLoginMode) toggleMode(); 
    }
    public void showLoginForm()    {
    	if (!isLoginMode) toggleMode(); 
    }
    public void displayErrorMessage(String msg) {
    	showMessage(msg, Theme.ERROR); 
    }
	// tạo giao diện
    private void buildUI() {
        JPanel card = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Theme.BG_CARD); 
                g2.fillRoundRect(0,0,getWidth(),getHeight(),20,20);
                g2.setColor(Theme.BORDER);  
                g2.drawRoundRect(0,0,getWidth()-1,getHeight()-1,20,20);
                g2.dispose();
            }
        };
        card.setOpaque(false); 
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setPreferredSize(new Dimension(420, 540));
        card.setBorder(BorderFactory.createEmptyBorder(40,40,40,40));

        JLabel logo = new JLabel("NLUChill", SwingConstants.CENTER);
        logo.setFont(new Font("SansSerif",Font.BOLD,48)); 
        logo.setAlignmentX(CENTER_ALIGNMENT);
        logo.setForeground(Color.RED);;
        

        titleLabel = new JLabel("Welcome Back", SwingConstants.CENTER);
        titleLabel.setFont(Theme.fontBold(26)); 
        titleLabel.setForeground(Theme.TEXT_PRIMARY);
        titleLabel.setAlignmentX(CENTER_ALIGNMENT);

        subtitleLabel = new JLabel("Đăng nhập để tiếp tục xem phim", SwingConstants.CENTER);
        subtitleLabel.setFont(Theme.fontPlain(13)); 
        subtitleLabel.setForeground(Theme.TEXT_SECONDARY);
        subtitleLabel.setAlignmentX(CENTER_ALIGNMENT);

        nameField = makePlaceholderField("Họ và tên");
        nameField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 48)); 
        nameField.setVisible(false);

        emailField = makePlaceholderField("Email");
        emailField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 48));

        passwordField = new JPasswordField() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Theme.BG_INPUT); 
                g2.fillRoundRect(0,0,getWidth(),getHeight(),10,10);
                g2.dispose(); 
                super.paintComponent(g);
            }
        };
        styleField(passwordField); 
        passwordField.setEchoChar('●');
        passwordField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 48));
        passwordField.addActionListener(e -> handleAuth());

        messageLabel = new JLabel(" ");
        messageLabel.setFont(Theme.fontPlain(12)); 
        messageLabel.setForeground(Theme.ERROR);
        messageLabel.setAlignmentX(CENTER_ALIGNMENT);

        actionBtn = makeAccentBtn("Đăng nhập");
        actionBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 48));
        actionBtn.addActionListener(e -> handleAuth());

        switchBtn = new JButton("Chưa có tài khoản? Đăng ký");
        switchBtn.setFont(Theme.fontPlain(12)); 
        switchBtn.setForeground(Theme.ACCENT);
        switchBtn.setBackground(null); 
        switchBtn.setBorderPainted(false);
        switchBtn.setContentAreaFilled(false);
        switchBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        switchBtn.setAlignmentX(CENTER_ALIGNMENT);
        switchBtn.addActionListener(e -> toggleMode());

        card.add(logo); 
        card.add(Box.createVerticalStrut(12));
        card.add(titleLabel); 
        card.add(Box.createVerticalStrut(6)); 
        card.add(subtitleLabel);
        card.add(Box.createVerticalStrut(28)); 
        card.add(nameField); 
        card.add(Box.createVerticalStrut(10));
        card.add(emailField); 
        card.add(Box.createVerticalStrut(10)); 
        card.add(passwordField);
        card.add(Box.createVerticalStrut(6)); 
        card.add(messageLabel); 
        card.add(Box.createVerticalStrut(8));
        card.add(actionBtn); 
        card.add(Box.createVerticalStrut(14)); 
        card.add(switchBtn);
        add(card);
    }

   


    private JTextField makePlaceholderField(String ph) {
        JTextField f = new JTextField() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
//                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Theme.BG_INPUT); 
                g2.fillRoundRect(0,0,getWidth(),getHeight(),10,10);
                g2.dispose(); 
                super.paintComponent(g);
                if (getText().isEmpty() && !isFocusOwner()) {
                    Graphics2D pg = (Graphics2D) g.create(); 
                    pg.setColor(Theme.TEXT_MUTED); 
                    pg.setFont(getFont());
                    FontMetrics fm = pg.getFontMetrics();
                    pg.drawString(ph, getInsets().left, (getHeight()+fm.getAscent()-fm.getDescent())/2);
                    pg.dispose();
                }
            }
        };
        styleField(f); 
        f.addActionListener(e -> handleAuth()); 
        return f;
    }

    private void styleField(JTextField f) {
        f.setOpaque(false);
        f.setBorder(BorderFactory.createCompoundBorder(
            new RoundBorder(Theme.BORDER,10), BorderFactory.createEmptyBorder(10,14,10,14)));
        f.setFont(Theme.fontPlain(14)); 
        f.setForeground(Theme.TEXT_PRIMARY);
        f.setCaretColor(Theme.ACCENT); 
        f.setAlignmentX(CENTER_ALIGNMENT);
        f.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) { 
            	f.repaint(); 
            }
            public void focusLost(FocusEvent e)   { 
            	f.repaint(); 
            }
        });
    }

    private JButton makeAccentBtn(String text) {
        JButton btn = new JButton(text) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
//                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                Color c = Theme.ACCENT;
                g2.setColor(c); 
                g2.fillRoundRect(0,0,getWidth(),getHeight(),12,12);
                g2.dispose(); 
                super.paintComponent(g);
            }
        };
        btn.setFont(Theme.fontBold(14)); 
        btn.setForeground(Color.WHITE);
        btn.setContentAreaFilled(false); 
        btn.setBorderPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setAlignmentX(CENTER_ALIGNMENT); 
        btn.setFocusPainted(false);
        return btn;
    }

  
        // ══════════════════════════════════════════
        //  XỬ LÝ SỰ KIỆN
        // ══════════════════════════════════════════

        private void handleUpdateProfile() {
            String newName  = nameEditField.getText().trim();
            String newEmail = emailEditField.getText().trim();

            if (newName.isEmpty() && newEmail.isEmpty()) {
                setProfileMsg("Vui lòng nhập ít nhất một thông tin cần thay đổi.", Theme.WARNING);
                return;
            }
            if (!newEmail.isEmpty() && !newEmail.contains("@")) {
                setProfileMsg("Email không hợp lệ.", Theme.ERROR); 
                return;
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

            setProfileMsg("Cập nhật thông tin thành công!", Theme.SUCCESS);
        }

        private void handleChangePassword() {
            String oldPass  = new String(oldPassField.getPassword());
            String newPass  = new String(newPassField.getPassword());
            String confirm  = new String(confirmPassField.getPassword());

            if (oldPass.isEmpty() || newPass.isEmpty() || confirm.isEmpty()) {
                setPassMsg("Vui lòng điền đầy đủ tất cả các ô.", Theme.WARNING); 
                return;
            }
            if (!oldPass.equals(currentMember.getPassword())) {
                setPassMsg("Mật khẩu hiện tại không đúng.", Theme.ERROR); 
                return;
            }
            if (!newPass.equals(confirm)) {
                setPassMsg("Mật khẩu không khớp.", Theme.ERROR); 
                return;
            }
            if (newPass.equals(oldPass)) {
                setPassMsg("Mật khẩu mới không được trùng mật khẩu cũ.", Theme.WARNING); 
                return;
            }

            // Đổi mật khẩu thành công
            currentMember.setPassword(newPass);
            oldPassField.setText(""); 
            newPassField.setText(""); 
            confirmPassField.setText("");

            if (userListener != null)
                userListener.onChangePassword(currentMember, oldPass, newPass);

            setPassMsg("Đổi mật khẩu thành công!", Theme.SUCCESS);
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
            profileMsgLabel.setText(msg); 
            profileMsgLabel.setForeground(color);
        }
        private void setPassMsg(String msg, Color color) {
            passMsgLabel.setText(msg); 
            passMsgLabel.setForeground(color);
        }

        public void setUserListener(UserListener l) { 
        	this.userListener = l; 
        }
        public Member getCurrentMember(){ 
        	return currentMember; 
        }

        // ══════════════════════════════════════════
        //  HELPERS UI
        // ══════════════════════════════════════════
        private JPanel roundCard() {
            JPanel p = new JPanel() {
                @Override protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(Theme.BG_CARD); 
                    g2.fillRoundRect(0,0,getWidth(),getHeight(),12,12);
                    g2.setColor(Theme.BORDER);  
                    g2.drawRoundRect(0,0,getWidth()-1,getHeight()-1,12,12);
                    g2.dispose();
                }
            };
            p.setOpaque(false); 
            p.setBorder(BorderFactory.createEmptyBorder(20,20,20,20));
            p.setAlignmentX(LEFT_ALIGNMENT); 
            return p;
        }

        private JLabel labelRow(String text) {
            JLabel l = new JLabel(text);
            l.setFont(Theme.fontBold(12)); 
            l.setForeground(Theme.TEXT_SECONDARY);
            l.setAlignmentX(LEFT_ALIGNMENT); 
            return l;
        }

        private JTextField inputField(String ph) {
            JTextField f = new JTextField() {
                @Override protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(Theme.BG_INPUT); 
                    g2.fillRoundRect(0,0,getWidth(),getHeight(),8,8);
                    g2.dispose(); 
                    super.paintComponent(g);
                    if (getText().isEmpty() && !isFocusOwner()) {
                        Graphics2D pg = (Graphics2D) g.create(); 
                        pg.setColor(Theme.TEXT_MUTED); 
                        pg.setFont(getFont());
                        FontMetrics fm = pg.getFontMetrics();
                        pg.drawString(ph, getInsets().left, (getHeight()+fm.getAscent()-fm.getDescent())/2);
                        pg.dispose();
                    }
                }
            };
            f.setOpaque(false); 
            f.setFont(Theme.fontPlain(13));
            f.setForeground(Theme.TEXT_PRIMARY); 
            f.setCaretColor(Theme.ACCENT);
            f.setBorder(BorderFactory.createCompoundBorder(
                new RoundBorder(Theme.BORDER,8), 
                BorderFactory.createEmptyBorder(9,12,9,12))
            );
            f.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
            f.setAlignmentX(LEFT_ALIGNMENT);
            f.addFocusListener(new FocusAdapter() {
                public void focusGained(FocusEvent e) { 
                	f.repaint(); 
                }
                public void focusLost(FocusEvent e)   { 
                	f.repaint(); 
                }
            });
            return f;
        }

        private JPasswordField passField(String ph) {
            JPasswordField f = new JPasswordField() {
                @Override protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(Theme.BG_INPUT); 
                    g2.fillRoundRect(0,0,getWidth(),getHeight(),8,8);
                    g2.dispose(); 
                    super.paintComponent(g);
                    if (getPassword().length == 0 && !isFocusOwner()) {
                        Graphics2D pg = (Graphics2D) g.create(); 
                        pg.setColor(Theme.TEXT_MUTED); 
                        pg.setFont(getFont());
                        FontMetrics fm = pg.getFontMetrics();
                        pg.drawString(ph, getInsets().left, (getHeight()+fm.getAscent()-fm.getDescent())/2);
                        pg.dispose();
                    }
                }
            };
            f.setEchoChar('●'); f.setOpaque(false); 
            f.setFont(Theme.fontPlain(13));
            f.setForeground(Theme.TEXT_PRIMARY); 
            f.setCaretColor(Theme.ACCENT);
            f.setBorder(BorderFactory.createCompoundBorder(
                new RoundBorder(Theme.BORDER,8), BorderFactory.createEmptyBorder(9,12,9,12)));
            f.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
            f.setAlignmentX(LEFT_ALIGNMENT);
            f.addFocusListener(new FocusAdapter() {
                public void focusGained(FocusEvent e) { 
                	f.repaint(); 
                }
                public void focusLost(FocusEvent e)   { 
                	f.repaint(); 
                }
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
            b.setFont(Theme.fontBold(13)); 
            b.setForeground(Color.WHITE);
            b.setContentAreaFilled(false); 
            b.setBorderPainted(false);
            b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            b.setBorder(BorderFactory.createEmptyBorder(10,16,10,16));
            b.setFocusPainted(false); 
            return b;
        }
    }

