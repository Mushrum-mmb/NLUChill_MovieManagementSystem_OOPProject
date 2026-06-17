import java.util.ArrayList;
import java.util.List;

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
	//Tao danhsach chua cac	member
	private List<Observer> ob;
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
		ob = new ArrayList<>();
		
	}
	@Override
	public void resister(Observer o) {
		// TODO Auto-generated method stub
		ob.add(o);
		System.out.println("Bạn đã đăng kí nhận thông báo thành công");
	}
	@Override
	public void unResister(Observer o) {
		// TODO Auto-generated method stub
		ob.remove(o);
		System.out.println("Bạn đã hủy đăng kí nhận thông báo thành công");
		
	}
	@Override
	public void notify(String nameMovie) {
		// TODO Auto-generated method stub
			for(Observer o: ob) {
				o.update("Phim mới;"+ nameMovie);
			}
		}
				
	//	getters setters
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
	public void getDetails() {
		System.out.println("Tên phim: " + nameMovie);
	    System.out.println("Diễn viên: " + actor);
	    System.out.println("Quốc gia: " + country);
	    System.out.println("Thể loại: " + category.getCategory());
		}
	public void watchMovie() {
		boolean isVip = false;
		if(isVip) {
			System.out.println("Đang xem phim VIP...");
			
		}else {
			System.out.println("Đang xem phim miễn phí...");
		}
		
	}
}


	
