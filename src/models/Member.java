package models;
import java.util.Date;
import java.util.Calendar;

public class Member extends User implements Observer{
//	thuoc tinh member
	private String AccountStatus;
	private Date expiredVIP;
	//	constructor member
	public Member(int id, String email, String password, String accountStatus, Date expiredVIP) {
		super(id, email, password);
		AccountStatus = accountStatus;
		this.expiredVIP = expiredVIP;
	}
//	getters setters
	public String getAccountStatus() {
		return AccountStatus;
	}
	public void setAccountStatus(String accountStatus) {
		AccountStatus = accountStatus;
	}
	public Date getExpiredVIP() {
		return expiredVIP;
	}
	public void setExpiredVIP(Date expiredVIP) {
		this.expiredVIP = expiredVIP;
	}
//	phuong thuc khac
	public void upgradeVIP() {
		this.AccountStatus = "VIP";

    Calendar lich = Calendar.getInstance();

    // Trường hợp 1: Member chưa từng là VIP, hoặc VIP đã hết hạn
    // → tính 30 ngày kể từ HÔM NAY
    if (expiredVIP == null || expiredVIP.before(new Date())) {
        lich.setTime(new Date());
    } 
    // Trường hợp 2: Member đang là VIP, còn hạn
    // → cộng thêm 30 ngày vào hạn CŨ, không mất ngày còn lại
    else {
        lich.setTime(expiredVIP);
    }

    lich.add(Calendar.DAY_OF_MONTH, 30);
    this.expiredVIP = lich.getTime();
    }
	@Override
	public void update(String notification) {
		System.out.println("[MEMBER - " + getEmail() + "] " + notification);		
	};
}
