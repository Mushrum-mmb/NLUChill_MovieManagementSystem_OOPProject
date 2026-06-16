public class Movie implements Subject{
//	thuoc tinh movie
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
//	getter setter
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getNameMovie() {
		return nameMovie;
	}
	public void setNameMovie(String nameMovie) {
		this.nameMovie = nameMovie;
	}
	public String getDirector() {
		return director;
	}
	public void setDirector(String director) {
		this.director = director;
	}
	public String getActor() {
		return actor;
	}
	public void setActor(String actor) {
		this.actor = actor;
	}
	public Category getCategory() {
		return category;
	}
	public void setCategory(Category category) {
		this.category = category;
	}
	public String getCountry() {
		return country;
	}
	public void setCountry(String country) {
		this.country = country;
	}
	public String getLink() {
		return link;
	}
	public void setLink(String link) {
		this.link = link;
	}
	public boolean isVip() {
		return isVip;
	}
	public void setVip(boolean isVip) {
		this.isVip = isVip;
	}
//	phuong thuc khac
	public void getDetails() {};
	public void watchMovie() {}
	@Override
	public void resister(Observer o) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void unResister(Observer o) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void notify(Observer o) {
		// TODO Auto-generated method stub
		
	};
	
}
