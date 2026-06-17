

import Model.*;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;

/**
 * MovieView — Màn hình danh sách phim.
 * Dialog xem phim tích hợp: video player + đánh giá sao + bình luận.
 */
public class MovieView extends JPanel {

    public interface MovieListener {
        void onSearch(String keyword);
        void onWatch(Movie movie);
        void onComment(Movie movie, String content);
        void onRate(Movie movie, int stars);
    }

    private MovieListener movieListener;
    private JTextField    searchField;
    private JPanel        movieGrid;
    private JLabel        resultLabel;

    // Dữ liệu bình luận & đánh giá (trong thực tế lấy từ DB)
    private Map<Integer, List<String>> commentMap = new HashMap<>();
    private Map<Integer, Integer>      ratingMap  = new HashMap<>();

    public MovieView() {
        setBackground(Theme.BG_DARK);
        setLayout(new BorderLayout());
        buildUI();
    }

    public void displayMovieList(List<Movie> movies) {
        movieGrid.removeAll();
        if (movies == null || movies.isEmpty()) {
            movieGrid.setLayout(new BorderLayout());
            JLabel empty = new JLabel("Không tìm thấy phim nào.", SwingConstants.CENTER);
            empty.setFont(Theme.fontPlain(15)); empty.setForeground(Theme.TEXT_MUTED);
            movieGrid.add(empty, BorderLayout.CENTER);
        } else {
            movieGrid.setLayout(new GridLayout(0, 3, 16, 16));
            for (Movie m : movies) movieGrid.add(buildMovieCard(m));
        }
        resultLabel.setText(movies != null ? movies.size() + " phim" : "0 phim");
        movieGrid.revalidate(); movieGrid.repaint();
    }

    /** Mở dialog xem phim với video player + đánh giá + bình luận */
    public void showPlayer(Movie movie, boolean isMember) {
        JDialog dialog = new JDialog(
            (Frame) SwingUtilities.getWindowAncestor(this),
            "🎬  " + movie.getNameMovie(), true);
        dialog.setSize(680, 680);
        dialog.setLocationRelativeTo(this);
        dialog.getContentPane().setBackground(Theme.BG_DARK);

        JPanel root = new JPanel(new BorderLayout(0, 0));
        root.setBackground(Theme.BG_DARK);
        root.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // ── VIDEO PLAYER ──
        JPanel screen = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(0,0,Theme.ACCENT_DARK,0,getHeight(),Theme.BG_DARK);
                g2.setPaint(gp); g2.fillRoundRect(0,0,getWidth(),getHeight(),12,12);
                g2.setColor(Color.WHITE); g2.setFont(new Font("SansSerif",Font.PLAIN,48));
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString("▶",(getWidth()-fm.stringWidth("▶"))/2, getHeight()/2+16);
                g2.setFont(Theme.fontBold(13));
                String nm = "Đang phát: " + movie.getNameMovie();
                g2.drawString(nm,(getWidth()-g2.getFontMetrics().stringWidth(nm))/2,getHeight()-16);
                g2.dispose();
            }
        };
        screen.setPreferredSize(new Dimension(0, 200));

        JPanel ctrl = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 0));
        ctrl.setBackground(Theme.BG_DARK);
        for (String ic : new String[]{"⏮","⏪","⏸","⏩","⏭"}) {
            JButton b = new JButton(ic);
            b.setFont(new Font("SansSerif",Font.PLAIN,16));
            b.setForeground(Theme.TEXT_PRIMARY); b.setBackground(Theme.BG_CARD);
            b.setBorder(BorderFactory.createEmptyBorder(6,12,6,12)); b.setFocusPainted(false);
            ctrl.add(b);
        }

        JPanel playerSection = new JPanel(new BorderLayout(0,8));
        playerSection.setBackground(Theme.BG_DARK);
        playerSection.add(screen, BorderLayout.CENTER);
        playerSection.add(ctrl,   BorderLayout.SOUTH);

        // ── ĐÁNH GIÁ & BÌNH LUẬN ──
        JPanel reviewSection = new JPanel();
        reviewSection.setBackground(Theme.BG_DARK);
        reviewSection.setLayout(new BoxLayout(reviewSection, BoxLayout.Y_AXIS));

        // Tiêu đề đánh giá
        JLabel ratingTitle = new JLabel("⭐  Đánh giá của bạn");
        ratingTitle.setFont(Theme.fontBold(14)); ratingTitle.setForeground(Theme.TEXT_PRIMARY);
        ratingTitle.setAlignmentX(LEFT_ALIGNMENT);

        // Chọn sao
        int currentRating = ratingMap.getOrDefault(movie.getId(), 0);
        JPanel starPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        starPanel.setBackground(Theme.BG_DARK);
        JButton[] stars = new JButton[5];
        for (int i = 0; i < 5; i++) {
            final int starVal = i + 1;
            stars[i] = new JButton(starVal <= currentRating ? "★" : "☆");
            stars[i].setFont(new Font("SansSerif", Font.PLAIN, 24));
            stars[i].setForeground(starVal <= currentRating ? Theme.VIP : Theme.TEXT_MUTED);
            stars[i].setBackground(null); stars[i].setBorderPainted(false);
            stars[i].setContentAreaFilled(false); stars[i].setFocusPainted(false);
            stars[i].setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            stars[i].addActionListener(e -> {
                ratingMap.put(movie.getId(), starVal);
                for (int j = 0; j < 5; j++) {
                    stars[j].setText(j < starVal ? "★" : "☆");
                    stars[j].setForeground(j < starVal ? Theme.VIP : Theme.TEXT_MUTED);
                }
                if (movieListener != null) movieListener.onRate(movie, starVal);
            });
            starPanel.add(stars[i]);
        }

        // Phân cách
        JSeparator sep1 = new JSeparator();
        sep1.setForeground(Theme.BORDER); sep1.setMaximumSize(new Dimension(Integer.MAX_VALUE,1));

        // Tiêu đề bình luận
        JLabel commentTitle = new JLabel("💬  Bình luận");
        commentTitle.setFont(Theme.fontBold(14)); commentTitle.setForeground(Theme.TEXT_PRIMARY);
        commentTitle.setAlignmentX(LEFT_ALIGNMENT);

        // Danh sách bình luận hiện có
        JPanel commentList = new JPanel();
        commentList.setBackground(Theme.BG_DARK);
        commentList.setLayout(new BoxLayout(commentList, BoxLayout.Y_AXIS));
        List<String> comments = commentMap.getOrDefault(movie.getId(), new ArrayList<>());
        for (String c : comments) addCommentRow(commentList, c);

        JScrollPane commentScroll = new JScrollPane(commentList);
        commentScroll.setBackground(Theme.BG_DARK);
        commentScroll.getViewport().setBackground(Theme.BG_DARK);
        commentScroll.setBorder(new LoginView.RoundBorder(Theme.BORDER, 8));
        commentScroll.setPreferredSize(new Dimension(0, 100));
        commentScroll.getVerticalScrollBar().setUnitIncrement(8);

        // Ô nhập bình luận mới
        JPanel inputRow = new JPanel(new BorderLayout(8, 0));
        inputRow.setBackground(Theme.BG_DARK);
        inputRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        inputRow.setAlignmentX(LEFT_ALIGNMENT);

        JTextField commentInput = new JTextField() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Theme.BG_INPUT); g2.fillRoundRect(0,0,getWidth(),getHeight(),8,8);
                g2.dispose(); super.paintComponent(g);
                if (getText().isEmpty() && !isFocusOwner()) {
                    Graphics2D pg = (Graphics2D) g.create();
                    pg.setColor(Theme.TEXT_MUTED); pg.setFont(getFont());
                    FontMetrics fm = pg.getFontMetrics();
                    pg.drawString("Nhập bình luận...", getInsets().left,
                        (getHeight()+fm.getAscent()-fm.getDescent())/2);
                    pg.dispose();
                }
            }
        };
        commentInput.setOpaque(false); commentInput.setFont(Theme.fontPlain(13));
        commentInput.setForeground(Theme.TEXT_PRIMARY); commentInput.setCaretColor(Theme.ACCENT);
        commentInput.setBorder(BorderFactory.createCompoundBorder(
            new LoginView.RoundBorder(Theme.BORDER,8),
            BorderFactory.createEmptyBorder(8,10,8,10)));
        commentInput.addFocusListener(new FocusAdapter(){
            public void focusGained(FocusEvent e){commentInput.repaint();}
            public void focusLost(FocusEvent e){commentInput.repaint();}
        });

        JButton sendBtn = new JButton("Gửi") {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getModel().isRollover()?Theme.ACCENT.brighter():Theme.ACCENT);
                g2.fillRoundRect(0,0,getWidth(),getHeight(),8,8);
                g2.dispose(); super.paintComponent(g);
            }
        };
        sendBtn.setFont(Theme.fontBold(12)); sendBtn.setForeground(Color.WHITE);
        sendBtn.setContentAreaFilled(false); sendBtn.setBorderPainted(false);
        sendBtn.setPreferredSize(new Dimension(64, 40));
        sendBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        sendBtn.setFocusPainted(false);

        Runnable sendComment = () -> {
            String text = commentInput.getText().trim();
            if (text.isEmpty()) return;
            commentMap.computeIfAbsent(movie.getId(), k -> new ArrayList<>()).add(text);
            addCommentRow(commentList, text);
            commentList.revalidate(); commentList.repaint();
            commentInput.setText("");
            if (movieListener != null) movieListener.onComment(movie, text);
        };
        sendBtn.addActionListener(e -> sendComment.run());
        commentInput.addActionListener(e -> sendComment.run());

        inputRow.add(commentInput, BorderLayout.CENTER);
        inputRow.add(sendBtn,      BorderLayout.EAST);

        reviewSection.add(Box.createVerticalStrut(12));
        reviewSection.add(ratingTitle);
        reviewSection.add(Box.createVerticalStrut(8));
        reviewSection.add(starPanel);
        reviewSection.add(Box.createVerticalStrut(12));
        reviewSection.add(sep1);
        reviewSection.add(Box.createVerticalStrut(12));
        reviewSection.add(commentTitle);
        reviewSection.add(Box.createVerticalStrut(8));
        reviewSection.add(commentScroll);
        reviewSection.add(Box.createVerticalStrut(8));
        reviewSection.add(inputRow);

        root.add(playerSection,  BorderLayout.NORTH);
        root.add(reviewSection,  BorderLayout.CENTER);
        dialog.add(root);
        dialog.setVisible(true);
    }

    private void addCommentRow(JPanel list, String text) {
        JPanel row = new JPanel(new BorderLayout(8, 0));
        row.setBackground(Theme.BG_CARD);
        row.setBorder(BorderFactory.createCompoundBorder(
            new LoginView.RoundBorder(Theme.BORDER, 8),
            BorderFactory.createEmptyBorder(8,12,8,12)));
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));

        JLabel avatar = new JLabel("👤");
        avatar.setFont(Theme.fontPlain(14));
        JLabel content = new JLabel(text);
        content.setFont(Theme.fontPlain(13)); content.setForeground(Theme.TEXT_PRIMARY);

        row.add(avatar, BorderLayout.WEST);
        row.add(content, BorderLayout.CENTER);

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(Theme.BG_DARK);
        wrapper.setBorder(BorderFactory.createEmptyBorder(0,0,6,0));
        wrapper.add(row);
        list.add(wrapper);
    }

    // ── Build UI ──
    private void buildUI() {
        JPanel header = new JPanel(new BorderLayout(16, 0));
        header.setBackground(Theme.BG_DARK);
        header.setBorder(BorderFactory.createEmptyBorder(24, 32, 12, 32));

        JLabel title = new JLabel("🎬  Danh sách phim");
        title.setFont(Theme.fontBold(22)); title.setForeground(Theme.TEXT_PRIMARY);

        JPanel searchBar = new JPanel(new BorderLayout()) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Theme.BG_INPUT); g2.fillRoundRect(0,0,getWidth(),getHeight(),12,12);
                g2.setColor(Theme.BORDER);   g2.drawRoundRect(0,0,getWidth()-1,getHeight()-1,12,12);
                g2.dispose();
            }
        };
        searchBar.setOpaque(false); searchBar.setPreferredSize(new Dimension(300,40));

        searchField = new JTextField() {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (getText().isEmpty() && !isFocusOwner()) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setColor(Theme.TEXT_MUTED); g2.setFont(getFont());
                    FontMetrics fm = g2.getFontMetrics();
                    g2.drawString("Tìm phim, đạo diễn...",4,(getHeight()+fm.getAscent()-fm.getDescent())/2);
                    g2.dispose();
                }
            }
        };
        searchField.setOpaque(false); searchField.setBorder(BorderFactory.createEmptyBorder(0,8,0,6));
        searchField.setFont(Theme.fontPlain(13)); searchField.setForeground(Theme.TEXT_PRIMARY);
        searchField.setCaretColor(Theme.ACCENT); searchField.addActionListener(e->doSearch());

        JButton searchBtn = accentBtn("Tìm", Theme.ACCENT);
        searchBtn.setPreferredSize(new Dimension(64,40)); searchBtn.addActionListener(e->doSearch());

        searchBar.add(new JLabel("  🔍"), BorderLayout.WEST);
        searchBar.add(searchField, BorderLayout.CENTER);
        searchBar.add(searchBtn,   BorderLayout.EAST);

        header.add(title, BorderLayout.WEST); header.add(searchBar, BorderLayout.EAST);

        resultLabel = new JLabel("0 phim");
        resultLabel.setFont(Theme.fontPlain(12)); resultLabel.setForeground(Theme.TEXT_SECONDARY);
        resultLabel.setBorder(BorderFactory.createEmptyBorder(0,32,10,32));

        JPanel top = new JPanel(new BorderLayout());
        top.setBackground(Theme.BG_DARK); top.add(header,BorderLayout.NORTH); top.add(resultLabel,BorderLayout.SOUTH);

        movieGrid = new JPanel(new GridLayout(0,3,16,16));
        movieGrid.setBackground(Theme.BG_DARK);

        JScrollPane sp = new JScrollPane(movieGrid);
        sp.setBackground(Theme.BG_DARK); sp.getViewport().setBackground(Theme.BG_DARK);
        sp.setBorder(BorderFactory.createEmptyBorder(0,32,32,32));
        sp.getVerticalScrollBar().setUnitIncrement(16);
        sp.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        add(top, BorderLayout.NORTH); add(sp, BorderLayout.CENTER);
    }

    private void doSearch() { if (movieListener!=null) movieListener.onSearch(searchField.getText().trim()); }

    private JPanel buildMovieCard(Movie movie) {
        JPanel card = new JPanel() {
            boolean hover=false;
            { addMouseListener(new MouseAdapter(){
                public void mouseEntered(MouseEvent e){hover=true;  repaint();}
                public void mouseExited(MouseEvent e) {hover=false; repaint();}
                public void mouseClicked(MouseEvent e){if(movieListener!=null) movieListener.onWatch(movie);}
            }); }
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(hover?Theme.BG_HOVER:Theme.BG_CARD);
                g2.fillRoundRect(0,0,getWidth(),getHeight(),14,14);
                g2.setColor(Theme.BORDER); g2.drawRoundRect(0,0,getWidth()-1,getHeight()-1,14,14);
                g2.dispose();
            }
        };
        card.setOpaque(false); card.setLayout(new BorderLayout());
        card.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        JPanel thumb = new JPanel(){
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2=(Graphics2D)g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp=new GradientPaint(0,0,Theme.ACCENT_DARK,0,getHeight(),new Color(10,12,20));
                g2.setPaint(gp); g2.fillRoundRect(0,0,getWidth(),getHeight(),14,0);
                g2.setFont(new Font("SansSerif",Font.PLAIN,36));
                FontMetrics fm=g2.getFontMetrics(); String ic=movie.isVip()?"👑":"🎥";
                g2.drawString(ic,(getWidth()-fm.stringWidth(ic))/2,getHeight()/2+12);
                // Hiển thị rating nếu có
                int r = ratingMap.getOrDefault(movie.getId(),0);
                if (r>0) {
                    g2.setColor(Theme.VIP); g2.setFont(Theme.fontBold(12));
                    StringBuilder sb=new StringBuilder();
                    for(int i=0;i<r;i++) sb.append("★");
                    g2.drawString(sb.toString(),8,getHeight()-8);
                }
                g2.dispose();
            }
        };
        thumb.setOpaque(false); thumb.setPreferredSize(new Dimension(0,130));

        JPanel info = new JPanel();
        info.setOpaque(false); info.setLayout(new BoxLayout(info, BoxLayout.Y_AXIS));
        info.setBorder(BorderFactory.createEmptyBorder(10,14,14,14));

        JLabel name = new JLabel(movie.getNameMovie());
        name.setFont(Theme.fontBold(13)); name.setForeground(Theme.TEXT_PRIMARY);

        JLabel dir = new JLabel("Dir: " + movie.getDirector());
        dir.setFont(Theme.fontPlain(11)); dir.setForeground(Theme.TEXT_SECONDARY);

        // Số bình luận
        int cCount = commentMap.getOrDefault(movie.getId(), Collections.emptyList()).size();
        JLabel commentCount = new JLabel("💬 " + cCount + " bình luận");
        commentCount.setFont(Theme.fontPlain(10)); commentCount.setForeground(Theme.TEXT_MUTED);

        JPanel tags = new JPanel(new FlowLayout(FlowLayout.LEFT,4,0));
        tags.setOpaque(false);
        if (movie.getCategory()!=null) tags.add(makeTag(movie.getCategory().getCategory(),Theme.BG_HOVER,Theme.TEXT_SECONDARY));
        if (movie.isVip())             tags.add(makeTag("VIP",new Color(92,67,10),Theme.VIP));

        JButton watchBtn = accentBtn("▶  Xem", Theme.ACCENT);
        watchBtn.setMaximumSize(new Dimension(90,28)); watchBtn.setAlignmentX(LEFT_ALIGNMENT);
        watchBtn.addActionListener(e->{if(movieListener!=null)movieListener.onWatch(movie);});

        info.add(name); info.add(Box.createVerticalStrut(3));
        info.add(dir);  info.add(Box.createVerticalStrut(3));
        info.add(commentCount); info.add(Box.createVerticalStrut(6));
        info.add(tags); info.add(Box.createVerticalStrut(8)); info.add(watchBtn);

        card.add(thumb,BorderLayout.NORTH); card.add(info,BorderLayout.CENTER);
        return card;
    }

    private JLabel makeTag(String text, Color bg, Color fg) {
        JLabel t = new JLabel(text) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2=(Graphics2D)g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(bg); g2.fillRoundRect(0,0,getWidth(),getHeight(),6,6);
                g2.dispose(); super.paintComponent(g);
            }
        };
        t.setFont(Theme.fontBold(10)); t.setForeground(fg); t.setOpaque(false);
        t.setBorder(BorderFactory.createEmptyBorder(3,7,3,7)); return t;
    }

    private JButton accentBtn(String text, Color color) {
        JButton b = new JButton(text){
            @Override protected void paintComponent(Graphics g){
                Graphics2D g2=(Graphics2D)g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getModel().isRollover()?color.brighter():color);
                g2.fillRoundRect(0,0,getWidth(),getHeight(),8,8);
                g2.dispose(); super.paintComponent(g);
            }
        };
        b.setFont(Theme.fontBold(11)); b.setForeground(Color.WHITE);
        b.setContentAreaFilled(false); b.setBorderPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)); b.setFocusPainted(false);
        return b;
    }

    public void setMovieListener(MovieListener l) { this.movieListener = l; }
}
