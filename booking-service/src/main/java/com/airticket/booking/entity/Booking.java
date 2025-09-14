package com.airticket.booking.entity;

import com.airticket.common.dto.BaseDTO;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "bookings")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class Booking extends BaseDTO {
    
    @Column(name = "booking_reference", unique = true, nullable = false)
    private String bookingReference;
    
    @Column(name = "user_id", nullable = false)
    private Long userId;
    
    @Column(name = "flight_id", nullable = false)
    private Long flightId;
    
    @Column(name = "passenger_name", nullable = false)
    private String passengerName;
    
    @Column(name = "passenger_email", nullable = false)
    private String passengerEmail;
    
    @Column(name = "passenger_phone")
    private String passengerPhone;
    
    @Column(name = "number_of_seats", nullable = false)
    private Integer numberOfSeats;
    
    @Column(name = "total_amount", nullable = false)
    private Double totalAmount;
    
    @Enumerated(EnumType.STRING)
    private BookingStatus status = BookingStatus.PENDING;
    
    @Column(name = "booking_date", nullable = false)
    private LocalDateTime bookingDate;
    
    @Column(name = "payment_id")
    private Long paymentId;
    
    @Column(name = "seat_numbers")
    private String seatNumbers;
    
    @Column(name = "special_requirements")
    private String specialRequirements;
    
    // Saga related fields
    @Column(name = "saga_id")
    private String sagaId;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "saga_status")
    private SagaStatus sagaStatus = SagaStatus.STARTED;
    
    public enum BookingStatus {
        PENDING, CONFIRMED, CANCELLED, COMPLETED, FAILED
    }
    
    public enum SagaStatus {
        STARTED, SEATS_RESERVED, PAYMENT_PROCESSED, NOTIFICATION_SENT, COMPLETED, COMPENSATING, FAILED
    }
}