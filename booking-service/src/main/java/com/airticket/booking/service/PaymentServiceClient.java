package com.airticket.booking.service;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "payment-service", path = "/api/v1/payments")
public interface PaymentServiceClient {
    
    @PostMapping("/process")
    PaymentResponse processPayment(@RequestBody PaymentRequest request);
    
    @PostMapping("/{id}/refund")
    void refundPayment(@PathVariable("id") Long paymentId);
    
    class PaymentRequest {
        private Long bookingId;
        private Double amount;
        private String paymentMethod;
        private String cardNumber;
        private String cardHolderName;
        private String expiryDate;
        private String cvv;
        
        // Getters and setters
        public Long getBookingId() { return bookingId; }
        public void setBookingId(Long bookingId) { this.bookingId = bookingId; }
        public Double getAmount() { return amount; }
        public void setAmount(Double amount) { this.amount = amount; }
        public String getPaymentMethod() { return paymentMethod; }
        public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }
        public String getCardNumber() { return cardNumber; }
        public void setCardNumber(String cardNumber) { this.cardNumber = cardNumber; }
        public String getCardHolderName() { return cardHolderName; }
        public void setCardHolderName(String cardHolderName) { this.cardHolderName = cardHolderName; }
        public String getExpiryDate() { return expiryDate; }
        public void setExpiryDate(String expiryDate) { this.expiryDate = expiryDate; }
        public String getCvv() { return cvv; }
        public void setCvv(String cvv) { this.cvv = cvv; }
    }
    
    class PaymentResponse {
        private boolean success;
        private Long paymentId;
        private String transactionId;
        private String message;
        
        // Getters and setters
        public boolean isSuccess() { return success; }
        public void setSuccess(boolean success) { this.success = success; }
        public Long getPaymentId() { return paymentId; }
        public void setPaymentId(Long paymentId) { this.paymentId = paymentId; }
        public String getTransactionId() { return transactionId; }
        public void setTransactionId(String transactionId) { this.transactionId = transactionId; }
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
    }
}