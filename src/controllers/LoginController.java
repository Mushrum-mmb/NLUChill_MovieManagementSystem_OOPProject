package controllers;

import java.awt.Color;
import java.util.List;

import models.Member;
import views.LoginView.AuthListener;

public class LoginController{
	  private List<Member> userStore;
	  private boolean      isLoginMode = true;
	public LoginController(List<Member> userStore) {
		super();
		this.userStore = userStore;
	}
	// đăng ký tài khoản
	public boolean registerUser(UserDTO data) {
        if (data.email == null || data.email.isEmpty()) return false;
        if (data.password == null || data.password.length() < 6) return false;
    //kiểm tra xem tài khoản đã tồn tại chưa
        boolean exists = userStore.stream()
            .anyMatch(u -> u.getEmail().equalsIgnoreCase(data.email));
        if (exists) return false;
        Member m = new Member(userStore.size() + 1, data.email, data.password, "Regular", null);
        userStore.add(m);
        System.out.println("Registered: " + data.email);
        return true;
    }
    // đăng nhập tài khoản
	public Member loginUser(LoginDTO credentials) {
		 if (credentials.email == null || credentials.password == null) return null;
	        return userStore.stream()
	            .filter(u -> u.getEmail().equalsIgnoreCase(credentials.email)
	                      && u.getPassword().equals(credentials.password))
	            .findFirst().orElse(null);
	    }
//  Logic giao diện đã có hoặc chưa có tài khoản
	 public void toggleMode() {
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
//		logic xử lý phần xác thực
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
	}


