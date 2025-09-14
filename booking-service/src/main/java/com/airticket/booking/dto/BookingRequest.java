package com.airticket.booking.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookingRequest {
    
    @NotNull(message = "User ID is required")
    private Long userId;
    
    @NotNull(message = "Flight ID is required")
    private Long flightId;
    
    @NotBlank(message = "Passenger name is required")
    @Size(max = 100, message = "Passenger name should not exceed 100 characters")
    private String passengerName;
    
    @Email(message = "Valid email is required")
    @NotBlank(message = "Passenger email is required")
    private String passengerEmail;
    
    private String passengerPhone;
    
    @NotNull(message = "Number of seats is required")
    @Min(value = 1, message = "At least 1 seat must be booked")
    @Max(value = 9, message = "Maximum 9 seats can be booked at once")
    private Integer numberOfSeats;
    
    private String specialRequirements;
    
    // Payment information
    @NotBlank(message = "Payment method is required")
    private String paymentMethod;
    
    private String cardNumber;
    private String cardHolderName;
    private String expiryDate;
    private String cvv;
}