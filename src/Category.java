
public class Category {
//	khai bao thuoc tinh
	private String category;
	private int id;
//	constructor category
	public Category(String category, int id) {
		super();
		this.category = category;
		this.id = id;
	}

	//	getter setter
	public String getCategory() {
		return category;
	}
	public void setCategory(String category) {
		this.category = category;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	
}
