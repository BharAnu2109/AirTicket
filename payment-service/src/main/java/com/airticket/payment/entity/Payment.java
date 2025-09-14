package com.airticket.payment.entity;

import com.airticket.common.dto.BaseDTO;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "payments")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class Payment extends BaseDTO {
    
    @Column(name = "booking_id", nullable = false)
    private Long bookingId;
    
    @Column(name = "amount", nullable = false)
    private Double amount;
    
    @Column(name = "currency", nullable = false)
    private String currency = "USD";
    
    @Column(name = "payment_method", nullable = false)
    private String paymentMethod;
    
    @Column(name = "transaction_id", unique = true)
    private String transactionId;
    
    @Enumerated(EnumType.STRING)
    private PaymentStatus status = PaymentStatus.PENDING;
    
    @Column(name = "payment_date")
    private LocalDateTime paymentDate;
    
    @Column(name = "card_last_four")
    private String cardLastFour;
    
    @Column(name = "card_holder_name")
    private String cardHolderName;
    
    @Column(name = "failure_reason")
    private String failureReason;
    
    @Column(name = "refund_amount")
    private Double refundAmount;
    
    @Column(name = "refund_date")
    private LocalDateTime refundDate;
    
    public enum PaymentStatus {
        PENDING, PROCESSING, COMPLETED, FAILED, REFUNDED, PARTIALLY_REFUNDED
    }
}