package com.airticket.flight.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FlightSearchCriteria {
    private String departureAirport;
    private String arrivalAirport;
    private LocalDateTime departureDate;
    private LocalDateTime returnDate;
    private Integer passengers;
    private Double maxPrice;
    private String airlineCode;
    private Boolean directFlightOnly;
    private String sortBy; // PRICE, DURATION, DEPARTURE_TIME
    private String sortOrder; // ASC, DESC
}