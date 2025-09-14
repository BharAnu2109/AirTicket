package com.airticket.flight.dto;

import com.airticket.common.dto.BaseDTO;
import com.airticket.flight.entity.Flight;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class FlightDTO extends BaseDTO {
    
    @NotBlank(message = "Flight number is required")
    private String flightNumber;
    
    @NotBlank(message = "Airline code is required")
    private String airlineCode;
    
    @NotBlank(message = "Airline name is required")
    private String airlineName;
    
    @NotBlank(message = "Departure airport is required")
    private String departureAirport;
    
    @NotBlank(message = "Arrival airport is required")
    private String arrivalAirport;
    
    @NotNull(message = "Departure time is required")
    private LocalDateTime departureTime;
    
    @NotNull(message = "Arrival time is required")
    private LocalDateTime arrivalTime;
    
    @NotNull(message = "Base price is required")
    @Positive(message = "Base price must be positive")
    private Double basePrice;
    
    @NotNull(message = "Total seats is required")
    @Positive(message = "Total seats must be positive")
    private Integer totalSeats;
    
    private Integer availableSeats;
    private String aircraftType;
    private Flight.FlightStatus status;
    private String gateNumber;
    private Integer durationMinutes;
}