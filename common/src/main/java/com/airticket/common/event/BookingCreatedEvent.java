package com.airticket.common.event;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
public class BookingCreatedEvent extends BaseEvent {
    private Long bookingId;
    private Long userId;
    private Long flightId;
    private String passengerName;
    private String passengerEmail;
    private Double totalAmount;
    private String status;

    public BookingCreatedEvent() {
        super();
    }

    public BookingCreatedEvent(Long bookingId, Long userId, Long flightId, 
                              String passengerName, String passengerEmail, 
                              Double totalAmount, String status) {
        super();
        this.bookingId = bookingId;
        this.userId = userId;
        this.flightId = flightId;
        this.passengerName = passengerName;
        this.passengerEmail = passengerEmail;
        this.totalAmount = totalAmount;
        this.status = status;
    }
}