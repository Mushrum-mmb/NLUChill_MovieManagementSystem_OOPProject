package models;

public class Rating {
//	thuoc tinh rating
	private int stars;
	private int idUser;
	private int idMovie;
//	constructor rating
	public Rating(int stars, int idUser, int idMovie) {
		super();
		this.stars = stars;
		this.idUser = idUser;
		this.idMovie = idMovie;
	}
//	getters setters
	public int getStars() {
		return stars;
	}
	public void setStars(int stars) {
		this.stars = stars;
	}
	public int getIdUser() {
		return idUser;
	}
	public void setIdUser(int idUser) {
		this.idUser = idUser;
	}
	public int getIdMovie() {
		return idMovie;
	}
	public void setIdMovie(int idMovie) {
		this.idMovie = idMovie;
	}
	
}
