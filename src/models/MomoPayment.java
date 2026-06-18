package models;

public class MomoPayment implements PaymentStrategy {
    private String phoneNumber;
    /** Constructor với số điện thoại */
    public MomoPayment(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    @Override
    public void pay(int amount) {
        System.out.println("[MomoPayment] Đang xử lý " + amount + " VND...");

        // Nếu chưa có số điện thoại → từ chối
        if (phoneNumber == null || phoneNumber.isEmpty()) {
            System.out.println("[MomoPayment] Chưa nhập số điện thoại Momo!");
        }
        System.out.println("[MomoPayment] Thanh toán Momo thành công!");
        
    }

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}
    
}
