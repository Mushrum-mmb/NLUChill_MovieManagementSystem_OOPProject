


import java.util.Date;

import Model.Member;
import Model.Payment;
import Model.PaymentStrategy;

public class PaymentController {

    private static final int[] PRICES    = {50000, 120000, 400000};
    private static final String[] LABELS = {"1 month", "3 months", "1 year"};

   
    public Payment processVIPUpgrade(Member member, int packageIndex, PaymentStrategy strategy) {
        int price = PRICES[packageIndex];
        System.out.println("Processing VIP upgrade: " + LABELS[packageIndex] + " = " + price + " VND");
            member.upgradeVIP();
            Payment payment = new Payment(0, new Date(), price, "VIP-" + LABELS[packageIndex]);
            System.out.println("Payment recorded: " + payment);
            return payment;
        }
     
}
