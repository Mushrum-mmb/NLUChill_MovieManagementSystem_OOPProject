package controllers;

import java.util.List;

import models.Member;

public class LoginController{
	  private List<Member> userStore;
	  
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
		
	}


