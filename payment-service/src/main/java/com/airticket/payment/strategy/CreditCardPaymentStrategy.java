package com.airticket.payment.strategy;

import com.airticket.common.pattern.PaymentStrategy;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class CreditCardPaymentStrategy implements PaymentStrategy {
    
    @Override
    public PaymentResult processPayment(PaymentRequest request) {
        // Simulate credit card processing
        try {
            Thread.sleep(1000); // Simulate processing time
            
            // Basic validation
            if (request.getCardNumber() == null || request.getCardNumber().length() < 12) {
                return new PaymentResult(false, null, "Invalid card number");
            }
            
            // Simulate success rate of 95%
            boolean success = Math.random() > 0.05;
            
            if (success) {
                String transactionId = "CC_" + UUID.randomUUID().toString();
                return new PaymentResult(true, transactionId, "Payment processed successfully");
            } else {
                return new PaymentResult(false, null, "Card declined");
            }
            
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return new PaymentResult(false, null, "Payment processing interrupted");
        }
    }
    
    @Override
    public boolean supports(String paymentMethod) {
        return "CREDIT_CARD".equalsIgnoreCase(paymentMethod) || 
               "VISA".equalsIgnoreCase(paymentMethod) || 
               "MASTERCARD".equalsIgnoreCase(paymentMethod);
    }
}