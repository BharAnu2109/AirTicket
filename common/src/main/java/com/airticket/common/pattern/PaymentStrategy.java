package com.airticket.common.pattern;

/**
 * Strategy Pattern for different payment processing strategies
 */
public interface PaymentStrategy {
    PaymentResult processPayment(PaymentRequest request);
    
    boolean supports(String paymentMethod);

    class PaymentRequest {
        private final Double amount;
        private final String currency;
        private final String paymentMethod;
        private final String cardNumber;
        private final String cardHolderName;
        
        public PaymentRequest(Double amount, String currency, String paymentMethod, 
                            String cardNumber, String cardHolderName) {
            this.amount = amount;
            this.currency = currency;
            this.paymentMethod = paymentMethod;
            this.cardNumber = cardNumber;
            this.cardHolderName = cardHolderName;
        }
        
        // Getters
        public Double getAmount() { return amount; }
        public String getCurrency() { return currency; }
        public String getPaymentMethod() { return paymentMethod; }
        public String getCardNumber() { return cardNumber; }
        public String getCardHolderName() { return cardHolderName; }
    }

    class PaymentResult {
        private final boolean success;
        private final String transactionId;
        private final String message;
        
        public PaymentResult(boolean success, String transactionId, String message) {
            this.success = success;
            this.transactionId = transactionId;
            this.message = message;
        }
        
        // Getters
        public boolean isSuccess() { return success; }
        public String getTransactionId() { return transactionId; }
        public String getMessage() { return message; }
    }
}