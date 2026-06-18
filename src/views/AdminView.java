package views;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.FontMetrics;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.RenderingHints;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.util.ArrayList;
import java.util.List;
import models.*;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;

import controllers.AdminController;

public class AdminView extends JPanel{
	        private ActionListener listener;
	        private DefaultTableModel movieTableModel;
	        private DefaultTableModel userTableModel;
	        private JTable movieTable;
	        private JTable userTable;
	        private JLabel totalMoviesLbl, totalUsersLbl, vipUsersLbl, lockedUsersLbl;
	        private List<Movie>  movies = new ArrayList<>();
	        private List<Member> users  = new ArrayList<>();
	        private AdminController controller;
	        public AdminView(AdminController controller) {
	        	this.controller = controller;
	        	setBackground(Theme.BG_DARK); 
	        	setLayout(new BorderLayout()); 
	        	buildUI(); 
	        }

	        private void buildUI() {
	            JPanel header = new JPanel(new BorderLayout());
	            header.setBackground(Theme.BG_DARK); 
	            header.setBorder(BorderFactory.createEmptyBorder(24,32,12,32));
	            JLabel title = new JLabel("Xin chào Admin");
	            title.setFont(Theme.fontBold(22)); 
	            title.setForeground(Theme.TEXT_PRIMARY);
	            JLabel sub = new JLabel("Quản lý phim và tài khoản người dùng");
	            sub.setFont(Theme.fontPlain(13)); 
	            sub.setForeground(Theme.TEXT_SECONDARY);
	            JPanel tBox = new JPanel(); 
	            tBox.setOpaque(false);
	            tBox.setLayout(new BoxLayout(tBox, BoxLayout.Y_AXIS));
	            tBox.add(title); 
	            tBox.add(Box.createVerticalStrut(3)); 
	            tBox.add(sub);
	            header.add(tBox, BorderLayout.WEST);
	            JPanel top = new JPanel(new BorderLayout()); 
	            top.setBackground(Theme.BG_DARK);
	            top.add(header, BorderLayout.NORTH); 
	            top.add(buildStats(), BorderLayout.CENTER);
	            add(top, BorderLayout.NORTH); 
	            add(buildTabs(), BorderLayout.CENTER);
	        }

	        private JPanel buildStats() {
	            JPanel row = new JPanel(new GridLayout(1,4,12,0));
	            row.setBackground(Theme.BG_DARK); 
	            row.setBorder(BorderFactory.createEmptyBorder(0,32,16,32));
	            totalMoviesLbl=new JLabel("0"); 
	            totalUsersLbl=new JLabel("0");
	            vipUsersLbl=new JLabel("0"); 
	            lockedUsersLbl=new JLabel("0");
	            row.add(statCard("Tổng phim",   totalMoviesLbl, Theme.ACCENT));
	            row.add(statCard("Người dùng",  totalUsersLbl,  Theme.SUCCESS));
	            row.add(statCard("VIP",          vipUsersLbl,    Theme.VIP));
	            row.add(statCard("Bị khóa",      lockedUsersLbl, Theme.ERROR));
	            return row;
	        }

	        private JPanel statCard(String label, JLabel valLbl, Color accent) {
	            JPanel c = new JPanel() {
	                @Override protected void paintComponent(Graphics g) {
	                    Graphics2D g2=(Graphics2D)g.create();
	                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
	                    g2.setColor(Theme.BG_CARD); 
	                    g2.fillRoundRect(0,0,getWidth(),getHeight(),12,12);
	                    g2.setColor(accent); 
	                    g2.fillRoundRect(0,12,4,getHeight()-24,4,4); 
	                    g2.dispose();
	                }
	            };
	            c.setOpaque(false); 
	            c.setLayout(new BoxLayout(c,BoxLayout.Y_AXIS));
	            c.setBorder(BorderFactory.createEmptyBorder(16,20,16,16));
	            JLabel lbl = new JLabel(label); 
	            lbl.setFont(Theme.fontPlain(12)); 
	            lbl.setForeground(Theme.TEXT_SECONDARY);
	            valLbl.setFont(Theme.fontBold(28)); 
	            valLbl.setForeground(accent);
	            c.add(lbl); 
	            c.add(Box.createVerticalStrut(4)); 
	            c.add(valLbl); 
	            return c;
	        }

	        private JTabbedPane buildTabs() {
	            JTabbedPane tabs = new JTabbedPane();
	            tabs.setFont(Theme.fontBold(13)); 
	            tabs.setBorder(BorderFactory.createEmptyBorder(0,24,24,24));
	            tabs.addTab("Quản lý Phim",       buildMovieTab());
	            tabs.addTab("Quản lý Người dùng", buildUserTab());
	            return tabs;
	        }

	        private JPanel buildMovieTab() {
	            JPanel p=new JPanel(new BorderLayout(0,10)); 
	            p.setBackground(Theme.BG_DARK); 
	            p.setBorder(BorderFactory.createEmptyBorder(14,8,8,8));
	            JPanel toolbar=new JPanel(new BorderLayout()); 
	            toolbar.setBackground(Theme.BG_DARK);
	            JLabel t=new JLabel("Danh sách phim"); 
	            t.setFont(Theme.fontBold(15)); 
	            t.setForeground(Theme.TEXT_PRIMARY);
	            JButton addBtn=accentBtn("Thêm",   Theme.ACCENT);
	            JButton editBtn=accentBtn("Sửa",   new Color(59,130,246));
	            JButton delBtn=accentBtn("Xóa",    Theme.ERROR);
	            addBtn.addActionListener(e->showAddMovie());
	            editBtn.addActionListener(e->showEditMovieDialog());
	            delBtn.addActionListener(e->deleteSelectedMovie());
	            JPanel btns=new JPanel(new FlowLayout(FlowLayout.RIGHT,8,0)); 
	            btns.setOpaque(false);
	            btns.add(delBtn); 
	            btns.add(editBtn); 
	            btns.add(addBtn);
	            toolbar.add(t,BorderLayout.WEST); 
	            toolbar.add(btns,BorderLayout.EAST);
	            String[] cols={"ID","Tên phim","Đạo diễn","Diễn viên","Thể loại","Quốc gia","Link","VIP"};
	            movieTableModel=new DefaultTableModel(cols,0){
	            	public boolean isCellEditable(int r,int c){
	            		return false;
	            	}
	            };
	            movieTable=styledTable(movieTableModel);
	            movieTable.getColumnModel().getColumn(0).setPreferredWidth(35);
	            movieTable.getColumnModel().getColumn(1).setPreferredWidth(170);
	            movieTable.getColumnModel().getColumn(7).setPreferredWidth(40);
	            p.add(toolbar,BorderLayout.NORTH); 
	            p.add(styledScroll(movieTable),BorderLayout.CENTER); 
	            return p;
	        }

	        private JPanel buildUserTab() {
	            JPanel p=new JPanel(new BorderLayout(0,10)); 
	            p.setBackground(Theme.BG_DARK); 
	            p.setBorder(BorderFactory.createEmptyBorder(14,8,8,8));
	            JPanel toolbar=new JPanel(new BorderLayout()); 
	            toolbar.setBackground(Theme.BG_DARK);
	            JLabel t=new JLabel("Tài khoản người dùng"); 
	            t.setFont(Theme.fontBold(15)); 
	            t.setForeground(Theme.TEXT_PRIMARY);
	            JButton lockBtn=accentBtn("Khóa",       Theme.ERROR);
	            JButton unlockBtn=accentBtn("Mở khóa",  Theme.SUCCESS);
	            JButton warnBtn=accentBtn("Cảnh báo",   new Color(202,138,4));
	            lockBtn.addActionListener(e->toggleLock(true));
	            unlockBtn.addActionListener(e->toggleLock(false));
	            warnBtn.addActionListener(e->warnSelectedUser());
	            JPanel btns=new JPanel(new FlowLayout(FlowLayout.RIGHT,8,0)); 
	            btns.setOpaque(false);
	            btns.add(warnBtn); 
	            btns.add(lockBtn); 
	            btns.add(unlockBtn);
	            toolbar.add(t,BorderLayout.WEST); 
	            toolbar.add(btns,BorderLayout.EAST);
	            String[] cols={"ID","Email","Trạng thái","Hết hạn VIP","Bị khóa"};
	            userTableModel=new DefaultTableModel(cols,0){
	            	public boolean isCellEditable(int r,int c){
	            		return false;
	            	}
	            };
	            userTable=styledTable(userTableModel);
	            userTable.getColumnModel().getColumn(0).setPreferredWidth(40);
	            userTable.getColumnModel().getColumn(1).setPreferredWidth(220);
	            p.add(toolbar,BorderLayout.NORTH); 
	            p.add(styledScroll(userTable),BorderLayout.CENTER); 
	            return p;
	        }
	        public void showAddMovie() {
	            JTextField idF = new JTextField();
	            JTextField nameF = new JTextField();
	            JTextField directorF = new JTextField();
	            JTextField actorF = new JTextField();
	            JTextField categoryF = new JTextField();
	            JTextField countryF = new JTextField();
	            JTextField linkF = new JTextField();
	            JCheckBox vipBox = new JCheckBox("Phim VIP");
	            JPanel panel = new JPanel(new GridLayout(0, 2, 5, 5));
	            panel.add(new JLabel("ID"));
	            panel.add(idF);

	            panel.add(new JLabel("Tên phim"));
	            panel.add(nameF);

	            panel.add(new JLabel("Đạo diễn"));
	            panel.add(directorF);

	            panel.add(new JLabel("Diễn viên"));
	            panel.add(actorF);

	            panel.add(new JLabel("Thể loại"));
	            panel.add(categoryF);

	            panel.add(new JLabel("Quốc gia"));
	            panel.add(countryF);

	            panel.add(new JLabel("Link"));
	            panel.add(linkF);

	            panel.add(new JLabel(""));
	            panel.add(vipBox);
	            int result = JOptionPane.showConfirmDialog(
	                    this,
	                    panel,
	                    "Thêm phim",
	                    JOptionPane.OK_CANCEL_OPTION);

	            if (result != JOptionPane.OK_OPTION)
	                return;
	            try {
	                Movie movie = new Movie(
	                        Integer.parseInt(idF.getText()),
	                        nameF.getText(),
	                        directorF.getText(),
	                        actorF.getText(),
	                        new Category(categoryF.getText(),Integer.parseInt(idF.getText())))
	                        countryF.getText(),
	                        linkF.getText(),
	                        vipBox.isSelected());
	                controller.addMovie(movie);
	                JOptionPane.showMessageDialog(
	                        this,
	                        "Thêm phim thành công");

	            } catch (Exception ex) {

	                JOptionPane.showMessageDialog(
	                        this,
	                        "Dữ liệu không hợp lệ");
	            }
	        }
	        private void showEditMovieDialog() {
	            int row=movieTable.getSelectedRow();
	            if(row<0){JOptionPane.showMessageDialog(this,"Vui lòng chọn phim cần sửa.","Chưa chọn",JOptionPane.WARNING_MESSAGE); 
	            	return;
	            }
	            Movie target = movies.stream().filter(
	            		m->m.getNameMovie().equals(
	            				movieTableModel.getValueAt(row,1)
	            			)).findFirst().orElse(null);
	            if(target==null) return;
	            JDialog d = new JDialog(
	            		(Frame) SwingUtilities.getWindowAncestor(this),"Cập nhật phim",true
	            );
	            d.setSize(480,510); 
	            d.setLocationRelativeTo(this); 
	            d.getContentPane().setBackground(Theme.BG_CARD);
	            JPanel form=new JPanel(); 
	            form.setBackground(Theme.BG_CARD);
	            form.setLayout(new BoxLayout(form,BoxLayout.Y_AXIS)); 
	            form.setBorder(BorderFactory.createEmptyBorder(28,32,28,32));
	            JLabel title=new JLabel("Cập nhật phim"); 
	            title.setFont(Theme.fontBold(18)); 
	            title.setForeground(Theme.TEXT_PRIMARY); 
	            title.setAlignmentX(LEFT_ALIGNMENT);
	            JTextField nF=dF("Tên phim"),
	            		dirF=dF("Đạo diễn"),
	            		actF=dF("Diễn viên"),
	            		cntF=dF("Quốc gia"),
	            		catF=dF("Thể loại"),
	            		lnkF=dF("Link");
	            nF.setText(target.getNameMovie()); 
	            dirF.setText(target.getDirector()); 
	            actF.setText(target.getActor());
	            cntF.setText(target.getCountry()); 
	            if(target.getCategory()!=null) {
	            	catF.setText(target.getCategory().getCategory());
	            }
	        	lnkF.setText(target.getLink());
	            JCheckBox vip=new JCheckBox("Chỉ VIP"); 
	            vip.setSelected(target.isVip()); 
	            vip.setFont(Theme.fontPlain(13)); 
	            vip.setForeground(Theme.TEXT_PRIMARY); 
	            vip.setBackground(Theme.BG_CARD); 
	            vip.setAlignmentX(LEFT_ALIGNMENT);
	            JLabel err=new JLabel(" "); 
	            err.setFont(Theme.fontPlain(12)); 
	            err.setForeground(Theme.ERROR); 
	            err.setAlignmentX(LEFT_ALIGNMENT);
	            JButton save=accentBtn("Lưu thay đổi",new Color(59,130,246)); 
	            save.setMaximumSize(new Dimension(Integer.MAX_VALUE,44)); 
	            save.setAlignmentX(LEFT_ALIGNMENT);
	            save.addActionListener(e->{
	                if(nF.getText().trim().isEmpty()){
	                	err.setText("Tên phim không được trống."); 
	                	return;
	                }
	                target.setNameMovie(nF.getText().trim()); 
	                target.setDirector(dirF.getText().trim()); 
	                target.setActor(actF.getText().trim());
	                target.setCountry(cntF.getText().trim()); 
	                target.setCategory(
	                		new Category(catF.getText().trim(),target.getCategory()!=null?target.getCategory().getId():0)
	                );
	                target.setLink(lnkF.getText().trim()); 
	                target.setVip(vip.isSelected());
	                if(listener!=null) listener.onUpdateMovie(target); 
	                d.dispose();
	            });
	            form.add(title); 
	            form.add(Box.createVerticalStrut(18));
	            for(Object[] r:new Object[][]{
	            	{" Tên phim",nF},
	            	{"Đạo diễn",dirF},
	            	{"Diễn viên",actF},
	            	{"Quốc gia",cntF},
	            	{"Thể loại",catF},
	            	{"Link",lnkF}
	            }){ 
	            	form.add(fRow((String)r[0],(JTextField)r[1])); 
	            	form.add(Box.createVerticalStrut(8)); 
	            }
	            form.add(vip); 
	            form.add(Box.createVerticalStrut(6)); 
	            form.add(err); 
	            form.add(Box.createVerticalStrut(12)); 
	            form.add(save);
	            JScrollPane sp=new JScrollPane(form); 
	            sp.setBorder(null); 
	            sp.getViewport().setBackground(Theme.BG_CARD);
	            d.add(sp); 
	            d.setVisible(true);
	        }

	        public void deleteSelectedMovie() {
	            int row = movieTable.getSelectedRow();
	            if (row < 0) {
	                JOptionPane.showMessageDialog(
	                    this,"Vui lòng chọn phim cần xóa.");
	                return;
	            }
	            if (listener != null) {
	                listener.onDeleteMovie(row);
	            }
	        }
	        }
	        private void warnSelectedUser() {
	            int row=userTable.getSelectedRow();
	            if(row<0){
	            	JOptionPane.showMessageDialog(this,"Vui lòng chọn người dùng.","Chưa chọn",JOptionPane.WARNING_MESSAGE); 
	            	return;
	            }
	            String email= (String) userTableModel.getValueAt(row,1);
	            Member target=users.stream().filter(
	            		u->u.getEmail().equals(email)).findFirst().orElse(null);
	            if(target==null) return;
	            String[] reasons={"Bình luận xúc phạm","Spam bình luận","Nội dung không phù hợp","Vi phạm cộng đồng"};
	            String reason= (String) JOptionPane.showInputDialog(this,
	            		"Lý do cảnh báo cho: "+ email,
	            		"⚠️  Cảnh báo",
	            		JOptionPane.WARNING_MESSAGE,
	            		null,
	            		reasons,
	            		reasons[0]
	            	);
	            if(reason!=null && listener!=null) listener.onWarnUser(target,reason);
	        }

	        private void toggleLock(boolean lock) {
	            int row=userTable.getSelectedRow();
	            if(row<0){
	            	JOptionPane.showMessageDialog(this,"Vui lòng chọn người dùng.","Chưa chọn",JOptionPane.WARNING_MESSAGE); 
	            	return;
	            }
	            String email = (String) userTableModel.getValueAt(row,1);
	            Member t = users.stream().filter(
	            		u->u.getEmail().equals(email)).findFirst().orElse(null);
	            if(t==null) return;
	            t.setAccountStatus(lock?"LOCKED":"Regular");
	            if(listener!=null){
	            	if(lock)listener.onLockAccount(t); 
	            	else listener.onUnlockAccount(t);
	            }
	            refreshUserTable();
	        }
	        private void updateStats() {
	            totalUsersLbl.setText(
	            		String.valueOf(
	            				users.size()
	            		)
	            );
	            vipUsersLbl.setText(
	            		String.valueOf(
	            				users.stream().filter(u->"VIP".equalsIgnoreCase(u.getStatus())).count()
	            		)
	            );
	            lockedUsersLbl.setText(
	            		String.valueOf(
	            				users.stream().filter(u->"LOCKED".equalsIgnoreCase(u.getStatus())).count()
	            		)
	            );
	        }
	        public void setAdminListener(ActionListener l) { 
	        	this.listener=l; 
	        }
	        private JTable styledTable(DefaultTableModel model) {
	            JTable t = new JTable(model){
	                @Override public Component prepareRenderer(TableCellRenderer r,int row,int col){
	                    Component c = super.prepareRenderer(r,row,col);
	                    if(isRowSelected(row)){
	                    	c.setBackground(Theme.ACCENT_GLOW);
	                    	c.setForeground(Theme.TEXT_PRIMARY);
	                    }
	                    else{
	                    	c.setBackground(row%2==0?Theme.BG_CARD:Theme.BG_DARK);
	                    	c.setForeground(Theme.TEXT_PRIMARY);
	                    }
	                    if(c instanceof JLabel) {
	                    	((JLabel)c).setBorder(BorderFactory.createEmptyBorder(0,10,0,10));
	                    }
	                    return c;
	                }
	            };
	            t.setBackground(Theme.BG_CARD); 
	            t.setForeground(Theme.TEXT_PRIMARY); 
	            t.setFont(Theme.fontPlain(13)); 
	            t.setRowHeight(40);
	            t.setShowGrid(false); 
	            t.setIntercellSpacing(new Dimension(0,1)); 
	            t.setSelectionBackground(Theme.ACCENT_GLOW); 
	            t.setFillsViewportHeight(true);
	            t.getTableHeader().setBackground(Theme.BG_SIDEBAR); 
	            t.getTableHeader().setForeground(Theme.TEXT_SECONDARY);
	            t.getTableHeader().setFont(Theme.fontBold(12)); 
	            t.getTableHeader().setReorderingAllowed(false);
	            return t;
	        }

	        private JScrollPane styledScroll(JTable t) {
	            JScrollPane sp = new JScrollPane(t); 
	            sp.setBackground(Theme.BG_CARD); 
	            sp.getViewport().setBackground(Theme.BG_CARD);
	            sp.setBorder(new RoundBorder(Theme.BORDER,10)); 
	            sp.getVerticalScrollBar().setUnitIncrement(12); 
	            return sp;
	        }

	        private JButton accentBtn(String text,Color color) {
	            JButton b=new JButton(text){
	                @Override protected void paintComponent(Graphics g){
	                    Graphics2D g2=(Graphics2D)g.create(); 
	                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
	                    Color bg=getModel().isRollover()?color.brighter():color;
	                    g2.setColor(new Color(bg.getRed(),bg.getGreen(),bg.getBlue(),210)); 
	                    g2.fillRoundRect(0,0,getWidth(),getHeight(),8,8);
	                    g2.dispose(); 
	                    super.paintComponent(g);
	                }
	            };
	            b.setFont(Theme.fontBold(12)); 
	            b.setForeground(Color.WHITE); 
	            b.setContentAreaFilled(false); 
	            b.setBorderPainted(false);
	            b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)); 
	            b.setBorder(BorderFactory.createEmptyBorder(8,14,8,14)); b.setFocusPainted(false);
	            return b;
	        }

	        private JTextField dF(String ph) {
	            JTextField f = new JTextField(){
	                @Override protected void paintComponent(Graphics g){
	                    Graphics2D g2=(Graphics2D)g.create(); 
	                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
	                    g2.setColor(Theme.BG_INPUT); 
	                    g2.fillRoundRect(0,0,getWidth(),getHeight(),8,8); 
	                    g2.dispose(); 
	                    super.paintComponent(g);
	                    if(getText().isEmpty()&&!isFocusOwner()){
	                        Graphics2D pg=(Graphics2D)g.create(); 
	                        pg.setColor(Theme.TEXT_MUTED); 
	                        pg.setFont(getFont());
	                        FontMetrics fm=pg.getFontMetrics(); 
	                        pg.drawString(ph,getInsets().left,(getHeight()+fm.getAscent()-fm.getDescent())/2); 
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
	            		BorderFactory.createEmptyBorder(8,10,8,10))
	            );
	            f.addFocusListener(new FocusAdapter(){
	            	public void focusGained(FocusEvent e){
	            		f.repaint();
	            	}
	            	public void focusLost(FocusEvent e){
	            		f.repaint();
	            	}
	            });
	            return f;
	        }

	        private JPanel fRow(String label,JTextField field) {
	            JPanel row = new JPanel(new BorderLayout(8,0)); 
	            row.setOpaque(false);
	            row.setMaximumSize(new Dimension(Integer.MAX_VALUE,44)); 
	            row.setAlignmentX(LEFT_ALIGNMENT);
	            JLabel lbl=new JLabel(label); 
	            lbl.setFont(Theme.fontPlain(12)); 
	            lbl.setForeground(Theme.TEXT_SECONDARY); 
	            lbl.setPreferredSize(new Dimension(72,0));
	            row.add(lbl,BorderLayout.WEST); 
	            row.add(field,BorderLayout.CENTER); 
	            return row;
	        }
	    }
}
