package models;
import java.sql.Date;

public class Member extends User implements Observer{
//	thuoc tinh member
	private Date expiredVIP;
	//	constructor member
	public Member(int id, String email, String password, String status, Date expiredVIP) {
		super(id, email, password,status);
		this.expiredVIP = expiredVIP;
	}
//	getters setters
	public Date getExpiredVIP() {
		return expiredVIP;
	}
	public void setExpiredVIP(Date expiredVIP) {
		this.expiredVIP = expiredVIP;
	}
//	phuong thuc khac
	public void upgradeVIP() {}
	@Override
	public void update(String notification) {
		// TODO Auto-generated method stub
		
	};
}
