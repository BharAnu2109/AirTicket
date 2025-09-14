package com.airticket.booking.service;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "flight-service", path = "/api/v1/flights")
public interface FlightServiceClient {
    
    @PostMapping("/{id}/reserve-seats")
    boolean reserveSeats(@PathVariable("id") Long flightId, @RequestParam Integer numberOfSeats);
    
    @PostMapping("/{id}/release-seats")
    void releaseSeats(@PathVariable("id") Long flightId, @RequestParam Integer numberOfSeats);
    
    @GetMapping("/{id}")
    FlightInfo getFlightById(@PathVariable("id") Long flightId);
    
    class FlightInfo {
        private Long id;
        private String flightNumber;
        private String departureAirport;
        private String arrivalAirport;
        private Double basePrice;
        private Integer availableSeats;
        
        // Getters and setters
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getFlightNumber() { return flightNumber; }
        public void setFlightNumber(String flightNumber) { this.flightNumber = flightNumber; }
        public String getDepartureAirport() { return departureAirport; }
        public void setDepartureAirport(String departureAirport) { this.departureAirport = departureAirport; }
        public String getArrivalAirport() { return arrivalAirport; }
        public void setArrivalAirport(String arrivalAirport) { this.arrivalAirport = arrivalAirport; }
        public Double getBasePrice() { return basePrice; }
        public void setBasePrice(Double basePrice) { this.basePrice = basePrice; }
        public Integer getAvailableSeats() { return availableSeats; }
        public void setAvailableSeats(Integer availableSeats) { this.availableSeats = availableSeats; }
    }
}