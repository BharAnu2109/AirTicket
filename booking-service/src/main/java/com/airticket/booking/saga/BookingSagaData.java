package com.airticket.booking.saga;

import com.airticket.booking.entity.Booking;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookingSagaData {
    private Long bookingId;
    private String sagaId;
    private Long userId;
    private Long flightId;
    private String passengerName;
    private String passengerEmail;
    private String passengerPhone;
    private Integer numberOfSeats;
    private Double totalAmount;
    private String paymentMethod;
    private String cardNumber;
    private String cardHolderName;
    private String specialRequirements;
    private Long paymentId;
    private String transactionId;
    private String failureReason;
    private Booking.BookingStatus currentStatus;
}