package com.airticket.common.event;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class PaymentProcessedEvent extends BaseEvent {
    private Long paymentId;
    private Long bookingId;
    private Double amount;
    private String status; // SUCCESS, FAILED
    private String paymentMethod;
    private String transactionId;

    public PaymentProcessedEvent() {
        super();
    }

    public PaymentProcessedEvent(Long paymentId, Long bookingId, Double amount, 
                                String status, String paymentMethod, String transactionId) {
        super();
        this.paymentId = paymentId;
        this.bookingId = bookingId;
        this.amount = amount;
        this.status = status;
        this.paymentMethod = paymentMethod;
        this.transactionId = transactionId;
    }
}