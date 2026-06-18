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
//  Logic giao diện đã có hoặc chưa có tài khoản
    private void toggleMode() {
        isLoginMode = !isLoginMode;
        if (isLoginMode) {
            titleLabel.setText("Welcome Back"); 
            subtitleLabel.setText("Đăng nhập để tiếp tục xem phim");
            actionBtn.setText("Đăng nhập"); 
            switchBtn.setText("Chưa có tài khoản? Đăng ký");
            nameField.setVisible(false);
        } else {
            titleLabel.setText("Tạo tài khoản"); 
            subtitleLabel.setText("Tham gia và bắt đầu xem phim");
            actionBtn.setText("Đăng ký"); 
            switchBtn.setText("Đã có tài khoản? Đăng nhập");
            nameField.setVisible(true);
        }
        messageLabel.setText(" "); 
        revalidate(); 
        repaint();
    }
//	logic xử lý phần xác thực
    private void handleAuth() {
        String email    = emailField.getText().trim();
        String password = new String(passwordField.getPassword());
        if (email.isEmpty() || password.isEmpty()) {
            displayErrorMessage("Vui lòng điền đầy đủ thông tin."); 
            return;
        }
        if (authListener == null) return;
        if (isLoginMode) {
            authListener.onLogin(email, password);
        } else {
            String name = nameField.getText().trim();
            if (name.isEmpty()) { 
            	displayErrorMessage("Vui lòng nhập tên."); 
            	return; 
            }
            authListener.onRegister(name, email, password);
        }
    }
    // logic hiển thị tin nhắn
    public void showMessage(String msg, Color color) {
        messageLabel.setText(msg); 
        messageLabel.setForeground(color);
    }
    public void setAuthListener(AuthListener l) { 
    	this.authListener = l; 
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
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                Color c = Theme.ACCENT;
                g2.setColor(c); 
                g2.fillRoundRect(0,0,getWidth(),getHeight(),12,12);
                g2.dispose(); 
                super.paintComponent(g);
            }
        };
        btn.setFont(Theme.fontBold(14)); 
        btn.setForeground(Color.BLACK);
        btn.setContentAreaFilled(false); 
        btn.setBorderPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setAlignmentX(CENTER_ALIGNMENT); 
        btn.setFocusPainted(false);
        return btn;
    }
}