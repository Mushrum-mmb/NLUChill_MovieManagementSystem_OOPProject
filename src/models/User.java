package models;

public class User {
//	thuoc tinh user
	private int id;
	private String email;
	private String password;
	private String status;
//	constructor user
	public User(int id, String email, String password,String status) {
		super();
		this.status = status;
		this.id = id;
		this.email = email;
		this.password = password;
		
	}
//	getters setters
	
	public int getId() {
		return id;
	}
	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public void setId(int id) {
		this.id = id;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
//	phuong thuc khac
	public void login(String email, String password) {};
	public void resister(String email, String password) {};
	public void searchMovie(Movie movie) {};
	
}
