package models;

import java.util.Date;

public class Payment {
    private int    id;
    private Date   paymentDate;
    private int    price;
    private String service;
    public Payment(int id, Date paymentDate, int price, String service) {
        this.id = id; 
        this.paymentDate = paymentDate;
        this.price = price; 
        this.service = service;
    }
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public Date getPaymentDate() {
		return paymentDate;
	}
	public void setPaymentDate(Date paymentDate) {
		this.paymentDate = paymentDate;
	}
	public int getPrice() {
		return price;
	}
	public void setPrice(int price) {
		this.price = price;
	}
	public String getService() {
		return service;
	}
	public void setService(String service) {
		this.service = service;
	}

    
}
