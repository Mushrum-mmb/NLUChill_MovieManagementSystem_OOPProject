package models;


public class VisaPayment implements PaymentStrategy {

    private String cardNumber;
    private String expiry;    
    private String cvv;       

    /** Constructor đầy đủ thông tin thẻ */
    public VisaPayment(String cardNumber, String expiry, String cvv) {
        this.cardNumber = cardNumber;
        this.expiry     = expiry;
        this.cvv        = cvv;
    }

    @Override
    public void pay(int amount) {
        System.out.println("[VisaPayment] Đang xử lý " + amount + " VND...");

        // Nếu chưa có thông tin thẻ → từ chối
        if (cardNumber == null || cardNumber.isEmpty()) {
            System.out.println("[VisaPayment] Chưa nhập thông tin thẻ!");
        }
        System.out.println("[VisaPayment]Thanh toán Visa thành công!");
    }

	public String getCardNumber() {
		return cardNumber;
	}

	public void setCardNumber(String cardNumber) {
		this.cardNumber = cardNumber;
	}

	public String getExpiry() {
		return expiry;
	}

	public void setExpiry(String expiry) {
		this.expiry = expiry;
	}

	public String getCvv() {
		return cvv;
	}

	public void setCvv(String cvv) {
		this.cvv = cvv;
	}
    
}
