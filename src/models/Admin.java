package models;

import java.util.ArrayList;
import java.util.List;
import models.*;

public class Admin extends User{
	private List<Movie>  movies = new ArrayList<>();
    private List<Member> users  = new ArrayList<>();
//	constructor admin
	public Admin(int id, String email, String password, String status) {
		super(id, email, password,status);
		// TODO Auto-generated constructor stub
	}
//	phuong thuc khac
	public void addMovie(Movie movie) {
		movies.add(movie);
		System.out.println("Đã thêm phim: " + movie.getNameMovie());
	};
	public void deleteMovie(Movie movie) {
			movies.remove(movie);
            System.out.println("Đã xóa phim: " + movie.getNameMovie());
	}
	public void lockAccount(User user) {
		user.setStatus("Locked");
        System.out.println("Đã khóa tài khoản: " + user.getEmail());
	}
    public void UnlockAccount(User user) {
    	user.setStatus("Unlocked");
        System.out.println("Đã mở khóa tài khoản: " + user.getEmail());
    }
    public void WarnUser(User user, String reason) {
    	  System.out.println("Warning " + user.getEmail()+ ": " + reason);
    }
}
