package models;

public class Admin extends User{
//	constructor admin
	public Admin(int id, String email, String password) {
		super(id, email, password);
		// TODO Auto-generated constructor stub
	}
//	phuong thuc khac
	public void addMovie(Movie movie) {};
	public void deleteMovie(Movie movie) {};
	public void lockAccount(User user) {};
	public void manager() {};
	
	
}
