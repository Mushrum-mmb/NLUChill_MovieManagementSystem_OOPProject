package views;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.RenderingHints;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import controllers.MemberController;
import models.Member;

public class MemberView extends JPanel {

    public interface MemberListener {
        void onUpdateProfile(Member member, String newName, String newEmail);
        void onChangePassword(Member member, String oldPass, String newPass);
        void onDeleteAccount(Member member);
    }

    private MemberListener userListener;
    private Member       currentMember;
    private String       customName = "";

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

    public MemberView() {
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
        revalidate(); 
        repaint();
    }

    private void buildUI() {
        // Header
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(Theme.BG_DARK);
        header.setBorder(BorderFactory.createEmptyBorder(24,32,12,32));
        JLabel title = new JLabel("Tài khoản của tôi");
        title.setFont(Theme.fontBold(22)); 
        title.setForeground(Theme.TEXT_PRIMARY);
        JLabel sub = new JLabel("Quản lý thông tin cá nhân và bảo mật");
        sub.setFont(Theme.fontPlain(13)); 
        sub.setForeground(Theme.TEXT_SECONDARY);
        JPanel tBox = new JPanel(); 
        tBox.setOpaque(false);
        tBox.setLayout(new BoxLayout(tBox, BoxLayout.Y_AXIS));
        tBox.add(title); 
        tBox.add(Box.createVerticalStrut(3)); 
        tBox.add(sub);
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
        sp.setBackground(Theme.BG_DARK); 
        sp.getViewport().setBackground(Theme.BG_DARK);
        sp.setBorder(null); 
        sp.getVerticalScrollBar().setUnitIncrement(12);
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
        sep.setForeground(Theme.BORDER); 
        sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));

        String status = currentMember.getAccountStatus();
        statusDisplayLabel = new JLabel(status, SwingConstants.CENTER);
        statusDisplayLabel.setFont(Theme.fontBold(20));
        statusDisplayLabel.setForeground("VIP".equalsIgnoreCase(status) ? Theme.VIP : Theme.ACCENT);
        statusDisplayLabel.setAlignmentX(CENTER_ALIGNMENT);

        JLabel statusTitle = new JLabel("Trạng thái tài khoản", SwingConstants.CENTER);
        statusTitle.setFont(Theme.fontPlain(12)); 
        statusTitle.setForeground(Theme.TEXT_SECONDARY);
        statusTitle.setAlignmentX(CENTER_ALIGNMENT);

        JLabel idLabel = new JLabel("Member ID: #" + currentMember.getId(), SwingConstants.CENTER);
        idLabel.setFont(Theme.fontPlain(12)); 
        idLabel.setForeground(Theme.TEXT_MUTED);
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

        JLabel title = new JLabel("Cập nhật thông tin");
        title.setFont(Theme.fontBold(15)); 
        title.setForeground(Theme.TEXT_PRIMARY);
        title.setAlignmentX(LEFT_ALIGNMENT);

        nameEditField  = inputField("Tên hiển thị mới");
        emailEditField = inputField("Email mới");

        profileMsgLabel = new JLabel(" ");
        profileMsgLabel.setFont(Theme.fontPlain(12));
        profileMsgLabel.setForeground(Theme.SUCCESS);
        profileMsgLabel.setAlignmentX(LEFT_ALIGNMENT);

        JButton saveBtn = colorBtn("Lưu thay đổi", new Color(59, 130, 246));
        saveBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        saveBtn.setAlignmentX(LEFT_ALIGNMENT);
        saveBtn.addActionListener(e -> {
            String newName = nameEditField.getText().trim();
            String newEmail = emailEditField.getText().trim();
            if (!newName.isEmpty()) {
                this.customName = newName; // Lưu ngay tại View
            }
            MemberController.handleUpdateProfile(this, currentMember, newName, newEmail);
        });

        card.add(title);
        card.add(Box.createVerticalStrut(14));
        card.add(labelRow("Tên mới"));  
        card.add(Box.createVerticalStrut(6));
        card.add(nameEditField);        
        card.add(Box.createVerticalStrut(10));
        card.add(labelRow("Email mới")); 
        card.add(Box.createVerticalStrut(6));
        card.add(emailEditField);       
        card.add(Box.createVerticalStrut(10));
        card.add(profileMsgLabel);      
        card.add(Box.createVerticalStrut(8));
        card.add(saveBtn);
        return card;
    }

    // ── Card 2: Đổi mật khẩu ──
    private JPanel buildChangePasswordCard() {
        JPanel card = roundCard();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));

        JLabel title = new JLabel("Đổi mật khẩu");
        title.setFont(Theme.fontBold(15)); 
        title.setForeground(Theme.TEXT_PRIMARY);
        title.setAlignmentX(LEFT_ALIGNMENT);

        oldPassField     = passField("Mật khẩu hiện tại");
        newPassField     = passField("Mật khẩu mới (tối thiểu 6 ký tự)");
        confirmPassField = passField("Xác nhận mật khẩu mới");

        passMsgLabel = new JLabel(" ");
        passMsgLabel.setFont(Theme.fontPlain(12)); 
        passMsgLabel.setForeground(Theme.SUCCESS);
        passMsgLabel.setAlignmentX(LEFT_ALIGNMENT);

        JButton changeBtn = colorBtn("Đổi mật khẩu", new Color(59, 130, 246));
        changeBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        changeBtn.setAlignmentX(LEFT_ALIGNMENT);
        changeBtn.addActionListener(e -> {
            String oldPass = new String(oldPassField.getPassword());
            String newPass = new String(newPassField.getPassword());
            String confirm = new String(confirmPassField.getPassword());
            MemberController.handleChangePassword(this, currentMember, oldPass, newPass, confirm);
        });

        card.add(title);
        card.add(Box.createVerticalStrut(14));
        card.add(labelRow("Mật khẩu hiện tại")); 
        card.add(Box.createVerticalStrut(6));
        card.add(oldPassField);                   
        card.add(Box.createVerticalStrut(10));
        card.add(labelRow("Mật khẩu mới"));       
        card.add(Box.createVerticalStrut(6));
        card.add(newPassField);                   
        card.add(Box.createVerticalStrut(10));
        card.add(labelRow("Xác nhận lại"));        
        card.add(Box.createVerticalStrut(6));
        card.add(confirmPassField);               
        card.add(Box.createVerticalStrut(10));
        card.add(passMsgLabel);                   
        card.add(Box.createVerticalStrut(8));
        card.add(changeBtn);
        return card;
    }

    // ── Card 3: Xóa tài khoản ──
    private JPanel buildDangerZoneCard() {
        JPanel card = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(40, 15, 15)); 
                g2.fillRoundRect(0,0,getWidth(),getHeight(),12,12);
                g2.setColor(new Color(120, 40, 40)); 
                g2.drawRoundRect(0,0,getWidth()-1,getHeight()-1,12,12);
                g2.dispose();
            }
        };
        card.setOpaque(false); 
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createEmptyBorder(18,20,18,20));
        card.setAlignmentX(LEFT_ALIGNMENT);

        JLabel title = new JLabel("Xóa tài khoản");
        title.setFont(Theme.fontBold(15)); 
        title.setForeground(Theme.ERROR);
        title.setAlignmentX(LEFT_ALIGNMENT);

        JLabel warn = new JLabel("Xóa tài khoản sẽ không thể hoàn tác.Tất cả dữ liệu của bạn sẽ bị xóa vĩnh viễn.");
        warn.setFont(Theme.fontPlain(12)); 
        warn.setForeground(Theme.TEXT_SECONDARY);
        warn.setAlignmentX(LEFT_ALIGNMENT);

        JButton deleteBtn = colorBtn("Xóa tài khoản của tôi", Theme.ERROR);
        deleteBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        deleteBtn.setAlignmentX(LEFT_ALIGNMENT);
        deleteBtn.addActionListener(e -> MemberController.handleDeleteAccount(this, currentMember));

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
    // Thêm các hàm hỗ trợ hiển thị lỗi
    public void setProfileMsg(String msg, Color color) {
        profileMsgLabel.setText(msg); 
        profileMsgLabel.setForeground(color);
    }

    public void setPassMsg(String msg, Color color) {
        passMsgLabel.setText(msg); 
        passMsgLabel.setForeground(color);
    }

    public void clearProfileEditFields() {
        nameEditField.setText("");
        emailEditField.setText("");
    }

    public void clearPasswordFields() {
        oldPassField.setText(""); 
        newPassField.setText(""); 
        confirmPassField.setText("");
    }

    public MemberListener getUserListener() {
        return this.userListener;
    }
    
    // ── Cập nhật hiển thị sau thay đổi từ bên ngoài ──
    public void refreshDisplay() {
    	if (currentMember == null) return;
        emailDisplayLabel.setText(currentMember.getEmail());
        
        // Nếu có tên tạm thì hiển thị, không thì hiển thị email
        if (!customName.isEmpty()) {
            nameDisplayLabel.setText(customName);
        } else {
            nameDisplayLabel.setText(currentMember.getEmail());
        }
        String status = currentMember.getAccountStatus();
        statusDisplayLabel.setText(status);
        statusDisplayLabel.setForeground("VIP".equalsIgnoreCase(status) ? Theme.VIP : Theme.ACCENT);
        repaint();
    }

    public void setUserListener(MemberListener l) { 
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
