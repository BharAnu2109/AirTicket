package com.airticket.payment.strategy;

import com.airticket.common.pattern.PaymentStrategy;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class PayPalPaymentStrategy implements PaymentStrategy {
    
    @Override
    public PaymentResult processPayment(PaymentRequest request) {
        // Simulate PayPal processing
        try {
            Thread.sleep(1500); // Simulate processing time
            
            // Simulate success rate of 98%
            boolean success = Math.random() > 0.02;
            
            if (success) {
                String transactionId = "PP_" + UUID.randomUUID().toString();
                return new PaymentResult(true, transactionId, "PayPal payment processed successfully");
            } else {
                return new PaymentResult(false, null, "PayPal payment failed");
            }
            
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return new PaymentResult(false, null, "PayPal processing interrupted");
        }
    }
    
    @Override
    public boolean supports(String paymentMethod) {
        return "PAYPAL".equalsIgnoreCase(paymentMethod);
    }
}