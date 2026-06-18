package controllers;
import java.util.Date;

import models.Member;
import models.Payment;
import models.PaymentStrategy;

public class PaymentController {

    private static final int[] PRICES    = {50000, 120000, 400000};
    private static final String[] LABELS = {"1 month", "3 months", "1 year"};

   
    public Payment processVIPUpgrade(Member member, int packageIndex, PaymentStrategy strategy) {
        int price = PRICES[packageIndex];
        System.out.println("Processing VIP upgrade: " + LABELS[packageIndex] + " = " + price + " VND");
            member.upgradeVIP();
            Payment payment = new Payment(0, null, price, "VIP-" + LABELS[packageIndex]);
            System.out.println("Payment recorded: " + payment);
            return payment;
        }
     
}
