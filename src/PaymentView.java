

import Controller.PaymentController;
import Model.Member;
import Model.Payment;
import Model.MomoPayment;
import Model.VisaPayment;
import Model.PaymentStrategy;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;

/**
 * PaymentView — Màn hình nâng cấp VIP.
 * Có trường hợp lỗi: nhập sai thông tin thẻ/SĐT → báo lỗi cụ thể.
 * Thanh toán thành công → hiển thị màn hình xác nhận đẹp.
 */
public class PaymentView extends JPanel {

    public interface PaymentListener {
        void onPay(int packageIndex, String method, String paymentInfo);
    }

    private PaymentListener paymentListener;

    // Thông tin member
    private JLabel memberLabel;
    private JLabel statusLabel;
    private JLabel messageLabel;

    // Lựa chọn
    private JPanel[] planCards   = new JPanel[3];
    private JPanel[] methodCards = new JPanel[2];
    private int    selectedPackage = 1;
    private String selectedMethod  = "Momo";

    // Input thông tin thanh toán
    private JPanel  momoInputPanel;
    private JPanel  visaInputPanel;
    private JTextField momoPhoneField;
    private JTextField visaCardField;
    private JTextField visaExpiryField;
    private JTextField visaCvvField;

    // Panel chính và panel thành công
    private JPanel formPanel;
    private JPanel successPanel;
    private CardLayout rootLayout;

    public PaymentView() {
        setBackground(Theme.BG_DARK);
        rootLayout = new CardLayout();
        setLayout(rootLayout);
        buildFormPanel();
        buildSuccessPanel();
    }

    // ══════════════════════════════════════════
    //  FORM PANEL — nhập thông tin thanh toán
    // ══════════════════════════════════════════
    private void buildFormPanel() {
        formPanel = new JPanel();
        formPanel.setBackground(Theme.BG_DARK);
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBorder(BorderFactory.createEmptyBorder(28, 32, 32, 32));

        JLabel title = new JLabel("💎  Nâng cấp VIP");
        title.setFont(Theme.fontBold(22)); title.setForeground(Theme.TEXT_PRIMARY);
        title.setAlignmentX(LEFT_ALIGNMENT);

        JLabel sub = new JLabel("Mở khóa tất cả phim với tài khoản VIP");
        sub.setFont(Theme.fontPlain(13)); sub.setForeground(Theme.TEXT_SECONDARY);
        sub.setAlignmentX(LEFT_ALIGNMENT);

        // ── Member info ──
        JPanel infoCard = roundCard();
        infoCard.setLayout(new BorderLayout(16, 0));
        infoCard.setMaximumSize(new Dimension(Integer.MAX_VALUE, 72));
        JLabel avatar = new JLabel("👤", SwingConstants.CENTER);
        avatar.setFont(new Font("SansSerif", Font.PLAIN, 28));
        avatar.setPreferredSize(new Dimension(56, 56));
        JPanel infoText = new JPanel();
        infoText.setOpaque(false);
        infoText.setLayout(new BoxLayout(infoText, BoxLayout.Y_AXIS));
        memberLabel = new JLabel("user@example.com");
        memberLabel.setFont(Theme.fontBold(14)); memberLabel.setForeground(Theme.TEXT_PRIMARY);
        statusLabel = new JLabel("Trạng thái: Regular");
        statusLabel.setFont(Theme.fontPlain(12)); statusLabel.setForeground(Theme.TEXT_SECONDARY);
        infoText.add(memberLabel); infoText.add(Box.createVerticalStrut(4)); infoText.add(statusLabel);
        infoCard.add(avatar, BorderLayout.WEST); infoCard.add(infoText, BorderLayout.CENTER);

        // ── Plans ──
        JLabel planTitle = new JLabel("Chọn gói");
        planTitle.setFont(Theme.fontBold(16)); planTitle.setForeground(Theme.TEXT_PRIMARY);
        planTitle.setAlignmentX(LEFT_ALIGNMENT);

        String[] durs   = {"1 Tháng", "3 Tháng", "1 Năm"};
        String[] prices = {"50,000đ", "120,000đ", "400,000đ"};
        String[] notes  = {"Cơ bản", "⭐ Phổ biến", "💰 Tốt nhất"};

        JPanel planRow = new JPanel(new GridLayout(1, 3, 12, 0));
        planRow.setOpaque(false);
        planRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 130));
        for (int i = 0; i < 3; i++) {
            final int idx = i;
            planCards[i] = buildPlanCard(durs[i], prices[i], notes[i], i == 1);
            planCards[i].addMouseListener(new MouseAdapter() {
                @Override public void mouseClicked(MouseEvent e) {
                    selectedPackage = idx;
                    for (int j = 0; j < 3; j++) planCards[j].setBorder(planBorder(j == idx));
                }
            });
            planRow.add(planCards[i]);
        }
        planCards[1].setBorder(planBorder(true));

        // ── Payment method ──
        JLabel methodTitle = new JLabel("Phương thức thanh toán");
        methodTitle.setFont(Theme.fontBold(16)); methodTitle.setForeground(Theme.TEXT_PRIMARY);
        methodTitle.setAlignmentX(LEFT_ALIGNMENT);

        String[] methods = {"Momo", "Visa"};
        String[] mLabels = {"💜 Momo", "💳 Visa"};
        JPanel methodRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        methodRow.setOpaque(false);
        for (int i = 0; i < 2; i++) {
            final int idx = i;
            methodCards[i] = buildMethodCard(mLabels[i]);
            methodCards[i].addMouseListener(new MouseAdapter() {
                @Override public void mouseClicked(MouseEvent e) {
                    selectedMethod = methods[idx];
                    for (int j = 0; j < 2; j++) methodCards[j].setBorder(methodBorder(j == idx));
                    momoInputPanel.setVisible(selectedMethod.equals("Momo"));
                    visaInputPanel.setVisible(selectedMethod.equals("Visa"));
                    formPanel.revalidate(); formPanel.repaint();
                }
            });
            methodRow.add(methodCards[i]);
        }
        methodCards[0].setBorder(methodBorder(true));

        // ── Momo Input ──
        momoInputPanel = buildInputCard();
        momoInputPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 70));
        momoInputPanel.setAlignmentX(LEFT_ALIGNMENT);
        JLabel momoLabel = new JLabel("Số điện thoại Momo");
        momoLabel.setFont(Theme.fontBold(13)); momoLabel.setForeground(Theme.TEXT_PRIMARY);
        momoPhoneField = inputField("VD: 0901234567");
        momoInputPanel.setLayout(new BoxLayout(momoInputPanel, BoxLayout.Y_AXIS));
        momoInputPanel.add(momoLabel); momoInputPanel.add(Box.createVerticalStrut(8));
        momoInputPanel.add(momoPhoneField);

        // ── Visa Input ──
        visaInputPanel = buildInputCard();
        visaInputPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 140));
        visaInputPanel.setAlignmentX(LEFT_ALIGNMENT);
        visaInputPanel.setVisible(false);
        visaInputPanel.setLayout(new BoxLayout(visaInputPanel, BoxLayout.Y_AXIS));
        JLabel visaTitle = new JLabel("Thông tin thẻ Visa");
        visaTitle.setFont(Theme.fontBold(13)); visaTitle.setForeground(Theme.TEXT_PRIMARY);
        visaCardField   = inputField("Số thẻ (16 chữ số)");
        JPanel row2 = new JPanel(new GridLayout(1, 2, 10, 0));
        row2.setOpaque(false); row2.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        visaExpiryField = inputField("Hết hạn (MM/YY)");
        visaCvvField    = inputField("CVV (3 số)");
        row2.add(visaExpiryField); row2.add(visaCvvField);
        visaInputPanel.add(visaTitle); visaInputPanel.add(Box.createVerticalStrut(8));
        visaInputPanel.add(visaCardField); visaInputPanel.add(Box.createVerticalStrut(8));
        visaInputPanel.add(row2);

        // ── Message ──
        messageLabel = new JLabel(" ");
        messageLabel.setFont(Theme.fontPlain(13)); messageLabel.setForeground(Theme.ERROR);
        messageLabel.setAlignmentX(LEFT_ALIGNMENT);

        // ── Pay button ──
        JButton payBtn = new JButton("✦  Nâng cấp VIP") {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getModel().isRollover() ? new Color(161,118,4) : new Color(202,138,4));
                g2.fillRoundRect(0,0,getWidth(),getHeight(),12,12);
                g2.dispose(); super.paintComponent(g);
            }
        };
        payBtn.setFont(Theme.fontBold(15)); payBtn.setForeground(new Color(10,12,20));
        payBtn.setContentAreaFilled(false); payBtn.setBorderPainted(false);
        payBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        payBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 52));
        payBtn.setAlignmentX(LEFT_ALIGNMENT); payBtn.setFocusPainted(false);
        payBtn.addActionListener(e -> handlePay());

        formPanel.add(title);     formPanel.add(Box.createVerticalStrut(4));
        formPanel.add(sub);       formPanel.add(Box.createVerticalStrut(20));
        formPanel.add(infoCard);  formPanel.add(Box.createVerticalStrut(24));
        formPanel.add(planTitle); formPanel.add(Box.createVerticalStrut(12));
        formPanel.add(planRow);   formPanel.add(Box.createVerticalStrut(24));
        formPanel.add(methodTitle); formPanel.add(Box.createVerticalStrut(12));
        formPanel.add(methodRow);   formPanel.add(Box.createVerticalStrut(12));
        formPanel.add(momoInputPanel);
        formPanel.add(visaInputPanel);
        formPanel.add(Box.createVerticalStrut(12));
        formPanel.add(messageLabel); formPanel.add(Box.createVerticalStrut(8));
        formPanel.add(payBtn);

        JScrollPane sp = new JScrollPane(formPanel);
        sp.setBackground(Theme.BG_DARK); sp.getViewport().setBackground(Theme.BG_DARK);
        sp.setBorder(null);
        add(sp, "FORM");
    }

    // ══════════════════════════════════════════
    //  SUCCESS PANEL — sau khi thanh toán xong
    // ══════════════════════════════════════════
    private void buildSuccessPanel() {
        successPanel = new JPanel(new GridBagLayout());
        successPanel.setBackground(Theme.BG_DARK);

        JPanel card = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Theme.BG_CARD);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                g2.setColor(Theme.BORDER);
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 20, 20);
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setPreferredSize(new Dimension(420, 360));
        card.setBorder(BorderFactory.createEmptyBorder(44, 44, 44, 44));

        // Icon thành công
        JLabel icon = new JLabel("✅", SwingConstants.CENTER);
        icon.setFont(new Font("SansSerif", Font.PLAIN, 64));
        icon.setAlignmentX(CENTER_ALIGNMENT);

        JLabel titleLbl = new JLabel("Thanh toán thành công!", SwingConstants.CENTER);
        titleLbl.setFont(Theme.fontBold(22));
        titleLbl.setForeground(Theme.SUCCESS);
        titleLbl.setAlignmentX(CENTER_ALIGNMENT);

        JLabel subLbl = new JLabel("Tài khoản VIP đã được kích hoạt.", SwingConstants.CENTER);
        subLbl.setFont(Theme.fontPlain(14));
        subLbl.setForeground(Theme.TEXT_SECONDARY);
        subLbl.setAlignmentX(CENTER_ALIGNMENT);

        JLabel crownLbl = new JLabel("👑  Chào mừng bạn đến với VIP!", SwingConstants.CENTER);
        crownLbl.setFont(Theme.fontBold(16));
        crownLbl.setForeground(Theme.VIP);
        crownLbl.setAlignmentX(CENTER_ALIGNMENT);

        // Nút quay lại xem phim
        JButton backBtn = new JButton("🎬  Bắt đầu xem phim") {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getModel().isRollover() ? Theme.ACCENT.brighter() : Theme.ACCENT);
                g2.fillRoundRect(0,0,getWidth(),getHeight(),12,12);
                g2.dispose(); super.paintComponent(g);
            }
        };
        backBtn.setFont(Theme.fontBold(14)); backBtn.setForeground(Color.WHITE);
        backBtn.setContentAreaFilled(false); backBtn.setBorderPainted(false);
        backBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        backBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 48));
        backBtn.setAlignmentX(CENTER_ALIGNMENT); backBtn.setFocusPainted(false);
        backBtn.addActionListener(e -> rootLayout.show(PaymentView.this, "FORM"));

        card.add(icon);
        card.add(Box.createVerticalStrut(16));
        card.add(titleLbl);
        card.add(Box.createVerticalStrut(8));
        card.add(subLbl);
        card.add(Box.createVerticalStrut(20));
        card.add(crownLbl);
        card.add(Box.createVerticalStrut(28));
        card.add(backBtn);

        successPanel.add(card);
        add(successPanel, "SUCCESS");
    }

    // ══════════════════════════════════════════
    //  XỬ LÝ THANH TOÁN
    // ══════════════════════════════════════════
    private void handlePay() {
        String info = "";
        if (selectedMethod.equals("Momo")) {
            info = momoPhoneField.getText().trim();
            if (info.isEmpty()) {
                showMessage("⚠️  Vui lòng nhập số điện thoại Momo.", Theme.WARNING);
                return;
            }
            if (!info.matches("0\\d{9}")) {
                showMessage("❌  Số điện thoại không hợp lệ (cần 10 số, bắt đầu bằng 0).", Theme.ERROR);
                return;
            }
        } else {
            String card   = visaCardField.getText().trim().replaceAll("\\s","");
            String expiry = visaExpiryField.getText().trim();
            String cvv    = visaCvvField.getText().trim();
            if (card.isEmpty() || expiry.isEmpty() || cvv.isEmpty()) {
                showMessage("⚠️  Vui lòng điền đầy đủ thông tin thẻ.", Theme.WARNING);
                return;
            }
            if (!card.matches("\\d{16}")) {
                showMessage("❌  Số thẻ không hợp lệ (cần đúng 16 chữ số).", Theme.ERROR);
                return;
            }
            if (!expiry.matches("(0[1-9]|1[0-2])/\\d{2}")) {
                showMessage("❌  Ngày hết hạn sai định dạng (MM/YY).", Theme.ERROR);
                return;
            }
            if (!cvv.matches("\\d{3}")) {
                showMessage("❌  CVV không hợp lệ (cần đúng 3 chữ số).", Theme.ERROR);
                return;
            }
            info = card + "|" + expiry + "|" + cvv;
        }
        if (paymentListener != null) paymentListener.onPay(selectedPackage, selectedMethod, info);
    }

    // ══════════════════════════════════════════
    //  PUBLIC METHODS
    // ══════════════════════════════════════════

    /** Hiển thị màn hình thành công */
    public void showPaymentSuccess() {
        rootLayout.show(this, "SUCCESS");
    }

    /** Hiển thị thông báo lỗi */
    public void showPaymentError() {
        showMessage("❌  Thanh toán thất bại. Vui lòng thử lại.", Theme.ERROR);
    }

    public void showMessage(String msg, Color color) {
        messageLabel.setText(msg); messageLabel.setForeground(color);
    }

    public void updateMemberInfo(String email, String status) {
        memberLabel.setText(email);
        statusLabel.setText("Trạng thái: " + status);
        statusLabel.setForeground("VIP".equalsIgnoreCase(status) ? Theme.VIP : Theme.TEXT_SECONDARY);
    }

    public void setPaymentListener(PaymentListener l) { this.paymentListener = l; }

    // ══════════════════════════════════════════
    //  HELPERS
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
        p.setOpaque(false); p.setBorder(BorderFactory.createEmptyBorder(14,16,14,16));
        p.setAlignmentX(LEFT_ALIGNMENT); return p;
    }

    private JPanel buildInputCard() {
        JPanel p = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Theme.BG_CARD); g2.fillRoundRect(0,0,getWidth(),getHeight(),12,12);
                g2.setColor(Theme.BORDER);  g2.drawRoundRect(0,0,getWidth()-1,getHeight()-1,12,12);
                g2.dispose();
            }
        };
        p.setOpaque(false); p.setBorder(BorderFactory.createEmptyBorder(16,18,16,18));
        return p;
    }

    private JTextField inputField(String placeholder) {
        JTextField f = new JTextField() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Theme.BG_INPUT); g2.fillRoundRect(0,0,getWidth(),getHeight(),8,8);
                g2.dispose(); super.paintComponent(g);
                if (getText().isEmpty() && !isFocusOwner()) {
                    Graphics2D pg = (Graphics2D) g.create();
                    pg.setColor(Theme.TEXT_MUTED); pg.setFont(getFont());
                    FontMetrics fm = pg.getFontMetrics();
                    pg.drawString(placeholder, getInsets().left,
                        (getHeight()+fm.getAscent()-fm.getDescent())/2);
                    pg.dispose();
                }
            }
        };
        f.setOpaque(false); f.setFont(Theme.fontPlain(13));
        f.setForeground(Theme.TEXT_PRIMARY); f.setCaretColor(Theme.ACCENT);
        f.setBorder(BorderFactory.createCompoundBorder(
            new LoginView.RoundBorder(Theme.BORDER, 8),
            BorderFactory.createEmptyBorder(8,10,8,10)));
        f.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        f.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) { f.repaint(); }
            public void focusLost(FocusEvent e)   { f.repaint(); }
        });
        return f;
    }

    private JPanel buildPlanCard(String dur, String price, String note, boolean hot) {
        JPanel c = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Theme.BG_CARD); g2.fillRoundRect(0,0,getWidth(),getHeight(),12,12);
                g2.dispose();
            }
        };
        c.setOpaque(false); c.setLayout(new BoxLayout(c, BoxLayout.Y_AXIS));
        c.setBorder(planBorder(false)); c.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        JLabel d=new JLabel(dur);   d.setFont(Theme.fontBold(14));  d.setForeground(Theme.TEXT_PRIMARY);
        JLabel p=new JLabel(price); p.setFont(Theme.fontBold(18));  p.setForeground(hot?Theme.VIP:Theme.ACCENT);
        JLabel n=new JLabel(note);  n.setFont(Theme.fontPlain(11)); n.setForeground(Theme.TEXT_SECONDARY);
        c.add(d); c.add(Box.createVerticalStrut(6)); c.add(p); c.add(Box.createVerticalStrut(4)); c.add(n);
        return c;
    }

    private JPanel buildMethodCard(String label) {
        JPanel mc = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Theme.BG_CARD); g2.fillRoundRect(0,0,getWidth(),getHeight(),10,10);
                g2.dispose();
            }
        };
        mc.setOpaque(false); mc.setBorder(methodBorder(false));
        mc.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        JLabel l=new JLabel(label); l.setFont(Theme.fontBold(13)); l.setForeground(Theme.TEXT_PRIMARY);
        mc.add(l); return mc;
    }

    private Border planBorder(boolean sel) {
        return BorderFactory.createCompoundBorder(
            new LoginView.RoundBorder(sel?Theme.ACCENT:Theme.BORDER,12),
            BorderFactory.createEmptyBorder(16,16,16,16));
    }
    private Border methodBorder(boolean sel) {
        return BorderFactory.createCompoundBorder(
            new LoginView.RoundBorder(sel?Theme.ACCENT:Theme.BORDER,10),
            BorderFactory.createEmptyBorder(10,18,10,18));
    }
}
