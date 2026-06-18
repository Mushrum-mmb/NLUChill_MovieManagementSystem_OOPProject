package models;
import java.sql.Date;

public class Comment {
//	thuoc tinh comment
	private int id;
	private int idUser;
	private int idMovie;
	private String content;
	private Date createDate;
//	constructor comment
	public Comment(int id, int idUser, int idMovie, String content, Date createDate) {
		super();
		this.id = id;
		this.idUser = idUser;
		this.idMovie = idMovie;
		this.content = content;
		this.createDate = createDate;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
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
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public Date getCreateDate() {
		return createDate;
	}
	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}
	
}
