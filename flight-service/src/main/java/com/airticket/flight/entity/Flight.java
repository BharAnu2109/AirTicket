package com.airticket.flight.entity;

import com.airticket.common.dto.BaseDTO;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "flights")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class Flight extends BaseDTO {
    
    @Column(name = "flight_number", unique = true, nullable = false)
    private String flightNumber;
    
    @Column(name = "airline_code", nullable = false)
    private String airlineCode;
    
    @Column(name = "airline_name", nullable = false)
    private String airlineName;
    
    @Column(name = "departure_airport", nullable = false)
    private String departureAirport;
    
    @Column(name = "arrival_airport", nullable = false)
    private String arrivalAirport;
    
    @Column(name = "departure_time", nullable = false)
    private LocalDateTime departureTime;
    
    @Column(name = "arrival_time", nullable = false)
    private LocalDateTime arrivalTime;
    
    @Column(name = "base_price", nullable = false)
    private Double basePrice;
    
    @Column(name = "total_seats", nullable = false)
    private Integer totalSeats;
    
    @Column(name = "available_seats", nullable = false)
    private Integer availableSeats;
    
    @Column(name = "aircraft_type")
    private String aircraftType;
    
    @Enumerated(EnumType.STRING)
    private FlightStatus status = FlightStatus.SCHEDULED;
    
    @Column(name = "gate_number")
    private String gateNumber;
    
    @Column(name = "duration_minutes")
    private Integer durationMinutes;
    
    public enum FlightStatus {
        SCHEDULED, DELAYED, BOARDING, DEPARTED, ARRIVED, CANCELLED
    }
}