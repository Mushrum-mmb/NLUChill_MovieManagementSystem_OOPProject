
public class Movie {
//	khai bao thuoc tinh class movie
	private int id;
	private String nameMovie;
	private String director;
	private String actor;
	private Category category;
	private String country;
	private String link;
	private boolean isVip;
//	constructor movie
	public Movie(int id, String nameMovie, String director, String actor, Category category, String country,
			String link, boolean isVip) {
		super();
		this.id = id;
		this.nameMovie = nameMovie;
		this.director = director;
		this.actor = actor;
		this.category = category;
		this.country = country;
		this.link = link;
		this.isVip = isVip;
		
	}
	
}
