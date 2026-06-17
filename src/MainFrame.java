

import Controller.*;

import Model.Category;
import Model.Member;
import Model.MomoPayment;
import Model.Movie;
import Model.Payment;
import Model.PaymentStrategy;
import Model.User;
import Model.VisaPayment;
import View.LoginView.AdminPanel;

import javax.swing.*;
import javax.swing.Timer;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;

public class MainFrame extends JFrame {

    private JPanel      mainContent;
    private CardLayout  cardLayout;
    private JPanel      sidebar;
    private JButton[]   navBtns;

    private LoginView   loginView;
    private MovieView   movieView;
    private PaymentView paymentView;

    private static final String[] USER_PAGES  = {"Movies", "VIP",   "Profile"};
    private static final String[] USER_ICONS  = {"🎬",    "💎",    "👤"};
    private static final String[] ADMIN_PAGES = {"Movies", "Admin", "Profile"};
    private static final String[] ADMIN_ICONS = {"🎬",    "🛠",    "👤"};

    private LoginController   loginController;
    private MovieController   movieController;
    private PaymentController paymentController;

    private Member       currentMember;
    private boolean      isAdmin = false;
    private List<Movie>  allMovies;
    private List<Member> allUsers;

    public MainFrame() {
        setTitle("CineStream — Movie Platform");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1150, 720);
        setMinimumSize(new Dimension(860, 580));
        setLocationRelativeTo(null);
        getContentPane().setBackground(Theme.BG_DARK);
        initData();
        buildLoginScreen();
    }

    // ════════════════════════════════════════════
    //  DỮ LIỆU MẪU
    // ════════════════════════════════════════════
    private void initData() {
        Category action   = new Category(1, "Action");
        Category drama    = new Category(2, "Drama");
        Category scifi    = new Category(3, "Sci-Fi");
        Category comedy   = new Category(4, "Comedy");
        Category thriller = new Category(5, "Thriller");

        allMovies = new ArrayList<>(Arrays.asList(
            new Movie(1, "The Dark Knight",   "Christopher Nolan",  "Christian Bale",      action,   "USA",     "/v/dk",  false),
            new Movie(2, "Inception",         "Christopher Nolan",  "Leonardo DiCaprio",   scifi,    "USA",     "/v/inc", true),
            new Movie(3, "Parasite",          "Bong Joon-ho",       "Song Kang-ho",        thriller, "Korea",   "/v/par", false),
            new Movie(4, "Interstellar",      "Christopher Nolan",  "Matthew McConaughey", scifi,    "USA",     "/v/int", true),
            new Movie(5, "The Godfather",     "F.F. Coppola",       "Marlon Brando",       drama,    "USA",     "/v/gf",  false),
            new Movie(6, "Avengers: Endgame", "Russo Brothers",     "Robert Downey Jr.",   action,   "USA",     "/v/av",  true),
            new Movie(7, "Spirited Away",     "Hayao Miyazaki",     "Daveigh Chase",       drama,    "Japan",   "/v/sa",  false),
            new Movie(8, "Grand Budapest",    "Wes Anderson",       "Ralph Fiennes",       comedy,   "Germany", "/v/gb",  true),
            new Movie(9, "Oppenheimer",       "Christopher Nolan",  "Cillian Murphy",      drama,    "USA",     "/v/op",  true)
        ));

        allUsers = new ArrayList<>(Arrays.asList(
            new Member(1, "phuonghuyen@gmail.com",   "pass123", "Regular", null),
            new Member(2, "bob@email.com",     "pass123", "VIP",     null),
            new Member(3, "charlie@email.com", "pass123", "Regular", null),
            new Member(4, "diana@email.com",   "pass123", "VIP",     null),
            new Member(5, "eve@email.com",     "pass123", "LOCKED",  null)
        ));

        // Đăng ký Observer: alice theo dõi 2 phim đầu
        allMovies.get(0).register(allUsers.get(0));
        allMovies.get(1).register(allUsers.get(0));

        loginController   = new LoginController(allUsers);
        movieController   = new MovieController(allMovies);
        paymentController = new PaymentController();
        currentMember     = allUsers.get(0);
    }

    // ════════════════════════════════════════════
    //  MÀN HÌNH ĐĂNG NHẬP
    // ════════════════════════════════════════════
    private void buildLoginScreen() {
        getContentPane().removeAll();
        setLayout(new BorderLayout());
        loginView = new LoginView();
        loginView.setAuthListener(new LoginView.AuthListener() {
            @Override
            public void onLogin(String email, String password) {
                if (email.equals("admin@gmail.com") && password.equals("admin123")) {
                    isAdmin = true;
                    currentMember = new Member(0, email, password, "Admin", null);
                    showMainApp(); return;
                }
                Member found = loginController.loginUser(new LoginDTO(email, password));
                if (found == null) {
                    loginView.displayErrorMessage("Sai email hoặc mật khẩu."); return;
                }
                if ("LOCKED".equalsIgnoreCase(found.getAccountStatus())) {
                    loginView.displayErrorMessage("Tài khoản bị khóa. Liên hệ admin."); return;
                }
                isAdmin = false; currentMember = found; showMainApp();
            }
            @Override
            public void onRegister(String name, String email, String password) {
                boolean ok = loginController.registerUser(new UserDTO(name, email, password));
                if (!ok) { loginView.displayErrorMessage("Email tồn tại hoặc mật khẩu < 6 ký tự."); return; }
                currentMember = loginController.loginUser(new LoginDTO(email, password));
                isAdmin = false;
                loginView.showMessage("✅ Đăng ký thành công!", Theme.SUCCESS);
                Timer t = new Timer(900, e -> showMainApp()); t.setRepeats(false); t.start();
            }
        });
        add(loginView, BorderLayout.CENTER);
        revalidate(); repaint();
    }

    // ════════════════════════════════════════════
    //  ỨNG DỤNG CHÍNH
    // ════════════════════════════════════════════
    private void showMainApp() {
        getContentPane().removeAll();
        setLayout(new BorderLayout(0, 0));

        String[] pages = isAdmin ? ADMIN_PAGES : USER_PAGES;
        String[] icons = isAdmin ? ADMIN_ICONS : USER_ICONS;

        sidebar = buildSidebar(pages, icons);
        add(sidebar, BorderLayout.WEST);

        cardLayout  = new CardLayout();
        mainContent = new JPanel(cardLayout);
        mainContent.setBackground(Theme.BG_DARK);

        // ── Movie View ──
        movieView = new MovieView();
        movieView.setMovieListener(new MovieView.MovieListener() {
            @Override public void onSearch(String kw) {
                movieView.displayMovieList(movieController.searchMovie(kw));
            }
            @Override public void onWatch(Movie movie) {
                boolean ok = movieController.playMovie(movie.getId(), currentMember);
                if (!ok) {
                    int opt = JOptionPane.showConfirmDialog(MainFrame.this,
                        "<html><b>" + movie.getNameMovie() + "</b> yêu cầu VIP.<br>Nâng cấp ngay?</html>",
                        "Yêu cầu VIP", JOptionPane.YES_NO_OPTION);
                    if (opt == JOptionPane.YES_OPTION) switchPage(isAdmin ? "Admin" : "VIP");
                } else {
                    // Mở dialog xem phim với rating + comment
                    movieView.showPlayer(movie, true);
                }
            }
            @Override public void onComment(Movie movie, String content) {
                System.out.println("[App] Bình luận phim '" + movie.getNameMovie() + "': " + content);
            }
            @Override public void onRate(Movie movie, int stars) {
                System.out.println("[App] Đánh giá phim '" + movie.getNameMovie() + "': " + stars + " sao");
                movieView.displayMovieList(movieController.getAllMovies());
            }
        });
        movieView.displayMovieList(allMovies);
        mainContent.add(movieView, "Movies");

        if (isAdmin) {
            // ── Admin Panel ──
            AdminPanel adminPanel = new LoginView.AdminPanel();
            adminPanel.loadMovies(allMovies);
            adminPanel.loadUsers(allUsers);
            adminPanel.setAdminListener(new LoginView.AdminPanel.AdminListener() {
                @Override public void onAddMovie(Movie m) {
                    allMovies.add(m);
                    movieController = new MovieController(allMovies);
                    adminPanel.loadMovies(allMovies);
                    movieView.displayMovieList(allMovies);
                    showToast("✅ Đã thêm: " + m.getNameMovie());
                }
                @Override public void onDeleteMovie(Movie m) {
                    allMovies.removeIf(x -> x.getId() == m.getId());
                    movieController = new MovieController(allMovies);
                    adminPanel.loadMovies(allMovies);
                    movieView.displayMovieList(allMovies);
                    showToast("🗑 Đã xóa phim.");
                }
                @Override public void onUpdateMovie(Movie m) {
                    movieController = new MovieController(allMovies);
                    adminPanel.loadMovies(allMovies);
                    movieView.displayMovieList(allMovies);
                    showToast("✏️ Đã cập nhật: " + m.getNameMovie());
                }
                @Override public void onLockAccount(User u) {
                    showToast("🔒 Đã khóa: " + u.getEmail());
                }
                @Override public void onUnlockAccount(User u) {
                    showToast("🔓 Đã mở khóa: " + u.getEmail());
                }
                @Override public void onWarnUser(User u, String reason) {
                    showToast("⚠️ Đã cảnh báo: " + u.getEmail());
                }
            });
            mainContent.add(adminPanel, "Admin");
        } else {
            // ── Payment View ──
            paymentView = new PaymentView();
            paymentView.updateMemberInfo(currentMember.getEmail(), currentMember.getAccountStatus());
            paymentView.setPaymentListener((pkgIdx, method, paymentInfo) -> {
                PaymentStrategy strategy;
                if (method.equals("Momo")) {
                    MomoPayment momo = new MomoPayment();
                    momo.setPhoneNumber(paymentInfo);
                    strategy = momo;
                } else {
                    String[] parts = paymentInfo.split("\\|");
                    VisaPayment visa = new VisaPayment();
                    visa.setCardNumber(parts[0]);
                    visa.setExpiry(parts[1]);
                    visa.setCvv(parts[2]);
                    strategy = visa;
                }
                Payment p = paymentController.processVIPUpgrade(currentMember, pkgIdx, strategy);
                if (p != null) {
                    paymentView.updateMemberInfo(currentMember.getEmail(), currentMember.getAccountStatus());
                    paymentView.showPaymentSuccess();
                } else {
                    paymentView.showPaymentError();
                }
            });
            mainContent.add(paymentView, "VIP");
        }

        mainContent.add(buildProfilePanel(), "Profile");
        add(mainContent, BorderLayout.CENTER);
        revalidate(); repaint();
        switchPage("Movies");
    }

    // ════════════════════════════════════════════
    //  SIDEBAR
    // ════════════════════════════════════════════
    private JPanel buildSidebar(String[] pages, String[] icons) {
        JPanel sb = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setColor(Theme.BG_SIDEBAR); g2.fillRect(0,0,getWidth(),getHeight());
                g2.setColor(Theme.BORDER); g2.drawLine(getWidth()-1,0,getWidth()-1,getHeight());
                g2.dispose();
            }
        };
        sb.setOpaque(false); sb.setLayout(new BoxLayout(sb, BoxLayout.Y_AXIS));
        sb.setPreferredSize(new Dimension(204, 0)); sb.setBorder(BorderFactory.createEmptyBorder(24,0,24,0));

        JLabel brand = new JLabel("  🎬 CineStream");
        brand.setFont(Theme.fontBold(16)); brand.setForeground(Theme.TEXT_PRIMARY);
        brand.setAlignmentX(LEFT_ALIGNMENT);
        brand.setBorder(BorderFactory.createEmptyBorder(0, 20, isAdmin ? 6 : 20, 20));
        sb.add(brand);

        if (isAdmin) {
            JLabel badge = new JLabel("  🛠 Admin Mode");
            badge.setFont(Theme.fontBold(11)); badge.setForeground(Theme.VIP);
            badge.setAlignmentX(LEFT_ALIGNMENT);
            badge.setBorder(BorderFactory.createEmptyBorder(0,20,16,20));
            sb.add(badge);
        }

        JSeparator sep = new JSeparator();
        sep.setForeground(Theme.BORDER); sep.setMaximumSize(new Dimension(Integer.MAX_VALUE,1));
        sb.add(sep); sb.add(Box.createVerticalStrut(14));

        navBtns = new JButton[pages.length];
        for (int i = 0; i < pages.length; i++) {
            navBtns[i] = createNavBtn(icons[i] + "  " + pages[i], pages[i]);
            sb.add(navBtns[i]); sb.add(Box.createVerticalStrut(3));
        }

        sb.add(Box.createVerticalGlue());

        JLabel userLbl = new JLabel("  " + currentMember.getEmail());
        userLbl.setFont(Theme.fontPlain(11)); userLbl.setForeground(Theme.TEXT_MUTED);
        userLbl.setBorder(BorderFactory.createEmptyBorder(0,20,10,20));
        sb.add(userLbl);

        JButton logout = createNavBtn("🚪  Đăng xuất", null);
        logout.addActionListener(e -> buildLoginScreen());
        sb.add(logout);
        return sb;
    }

    private JButton createNavBtn(String label, String page) {
        JButton btn = new JButton(label) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                boolean sel = Theme.ACCENT_GLOW.equals(getBackground());
                if (sel) {
                    g2.setColor(Theme.ACCENT_GLOW); g2.fillRoundRect(8,2,getWidth()-16,getHeight()-4,8,8);
                    g2.setColor(Theme.ACCENT); g2.fillRoundRect(0,8,4,getHeight()-16,4,4);
                } else if (getModel().isRollover()) {
                    g2.setColor(new Color(99,102,241,25)); g2.fillRoundRect(8,2,getWidth()-16,getHeight()-4,8,8);
                }
                g2.dispose(); super.paintComponent(g);
            }
        };
        btn.setFont(Theme.fontBold(13)); btn.setForeground(Theme.TEXT_SECONDARY);
        btn.setContentAreaFilled(false); btn.setBorderPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setBorder(BorderFactory.createEmptyBorder(10,24,10,16));
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE,44)); btn.setFocusPainted(false);
        if (page != null) btn.addActionListener(e -> switchPage(page));
        return btn;
    }

    private void switchPage(String page) {
        cardLayout.show(mainContent, page);
        String[] pages = isAdmin ? ADMIN_PAGES : USER_PAGES;
        for (int i = 0; i < navBtns.length; i++) {
            boolean sel = pages[i].equals(page);
            navBtns[i].setBackground(sel ? Theme.ACCENT_GLOW : null);
            navBtns[i].setForeground(sel ? Theme.TEXT_PRIMARY : Theme.TEXT_SECONDARY);
            navBtns[i].repaint();
        }
    }

    // ════════════════════════════════════════════
    //  PROFILE PANEL
    // ════════════════════════════════════════════
    private LoginView.UserPanel userPanel; // giữ tham chiếu để refresh sau thay đổi

    private JPanel buildProfilePanel() {
        // Admin: hiển thị card đơn giản (chỉ xem, không chỉnh sửa)
        if (isAdmin) {
            JPanel wrap = new JPanel(new GridBagLayout()); wrap.setBackground(Theme.BG_DARK);
            JPanel card = new JPanel() {
                @Override protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(Theme.BG_CARD); g2.fillRoundRect(0,0,getWidth(),getHeight(),16,16);
                    g2.setColor(Theme.BORDER);  g2.drawRoundRect(0,0,getWidth()-1,getHeight()-1,16,16);
                    g2.dispose();
                }
            };
            card.setOpaque(false); card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
            card.setPreferredSize(new Dimension(380,280)); card.setBorder(BorderFactory.createEmptyBorder(36,40,36,40));
            JLabel av = new JLabel("🛠", SwingConstants.CENTER);
            av.setFont(new Font("SansSerif",Font.PLAIN,60)); av.setAlignmentX(CENTER_ALIGNMENT);
            JLabel em = new JLabel(currentMember.getEmail(), SwingConstants.CENTER);
            em.setFont(Theme.fontBold(17)); em.setForeground(Theme.TEXT_PRIMARY); em.setAlignmentX(CENTER_ALIGNMENT);
            JLabel ro = new JLabel("Administrator", SwingConstants.CENTER);
            ro.setFont(Theme.fontPlain(13)); ro.setForeground(Theme.TEXT_SECONDARY); ro.setAlignmentX(CENTER_ALIGNMENT);
            JSeparator sep = new JSeparator(); sep.setForeground(Theme.BORDER); sep.setMaximumSize(new Dimension(Integer.MAX_VALUE,1));
            JLabel st = new JLabel("Full Access", SwingConstants.CENTER);
            st.setFont(Theme.fontBold(22)); st.setForeground(Theme.VIP); st.setAlignmentX(CENTER_ALIGNMENT);
            card.add(av); card.add(Box.createVerticalStrut(12)); card.add(em);
            card.add(Box.createVerticalStrut(6)); card.add(ro);
            card.add(Box.createVerticalStrut(20)); card.add(sep); card.add(Box.createVerticalStrut(20)); card.add(st);
            wrap.add(card); return wrap;
        }

        // User: dùng UserPanel đầy đủ (cập nhật, đổi mật khẩu, xóa tài khoản)
        userPanel = new LoginView.UserPanel();
        userPanel.loadMember(currentMember);
        userPanel.setUserListener(new LoginView.UserPanel.UserListener() {

            @Override
            public void onUpdateProfile(Member member, String newName, String newEmail) {
                // Cập nhật sidebar email hiển thị
                revalidate(); repaint();
                showToast("✅ Cập nhật thông tin thành công!");
            }

            @Override
            public void onChangePassword(Member member, String oldPass, String newPass) {
                showToast("🔑 Đổi mật khẩu thành công!");
            }

            @Override
            public void onDeleteAccount(Member member) {
                // Xóa khỏi danh sách users
                allUsers.removeIf(u -> u.getId() == member.getId());
                showToast("🗑 Tài khoản đã được xóa.");
                // Tự động đăng xuất sau 1.5 giây
                Timer t = new Timer(1500, e -> buildLoginScreen());
                t.setRepeats(false); t.start();
            }
        });
        return userPanel;
    }

    // ── Toast notification ──
    private void showToast(String msg) {
        JWindow toast = new JWindow(this);
        JLabel lbl = new JLabel("  " + msg + "  ");
        lbl.setFont(Theme.fontBold(13)); lbl.setForeground(Color.WHITE);
        lbl.setOpaque(true); lbl.setBackground(new Color(30,38,65));
        lbl.setBorder(BorderFactory.createCompoundBorder(
            new LoginView.RoundBorder(Theme.ACCENT,10), BorderFactory.createEmptyBorder(10,16,10,16)));
        toast.add(lbl); toast.pack();
        Point loc = getLocation();
        toast.setLocation(loc.x+(getWidth()-toast.getWidth())/2, loc.y+getHeight()-70);
        toast.setVisible(true);
        Timer t = new Timer(2200, e -> toast.dispose()); t.setRepeats(false); t.start();
    }
}
