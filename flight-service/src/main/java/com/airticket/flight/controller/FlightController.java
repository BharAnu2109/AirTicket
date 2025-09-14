package com.airticket.flight.controller;

import com.airticket.common.dto.ApiResponse;
import com.airticket.common.util.FlightGraph;
import com.airticket.flight.dto.FlightDTO;
import com.airticket.flight.dto.FlightSearchCriteria;
import com.airticket.flight.entity.Flight;
import com.airticket.flight.service.FlightService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1/flights")
@RequiredArgsConstructor
@Slf4j
public class FlightController {
    
    private final FlightService flightService;
    
    @PostMapping
    public ResponseEntity<ApiResponse<FlightDTO>> createFlight(@Valid @RequestBody FlightDTO flightDTO) {
        log.info("Received request to create flight: {}", flightDTO.getFlightNumber());
        FlightDTO createdFlight = flightService.createFlight(flightDTO);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Flight created successfully", createdFlight));
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<FlightDTO>> getFlightById(@PathVariable Long id) {
        FlightDTO flight = flightService.getFlightById(id);
        return ResponseEntity.ok(ApiResponse.success(flight));
    }
    
    @GetMapping("/number/{flightNumber}")
    public ResponseEntity<ApiResponse<FlightDTO>> getFlightByNumber(@PathVariable String flightNumber) {
        FlightDTO flight = flightService.getFlightByNumber(flightNumber);
        return ResponseEntity.ok(ApiResponse.success(flight));
    }
    
    @PostMapping("/search")
    public ResponseEntity<ApiResponse<List<FlightDTO>>> searchFlights(@RequestBody FlightSearchCriteria criteria) {
        log.info("Searching flights with criteria: {}", criteria);
        List<FlightDTO> flights = flightService.searchFlights(criteria);
        return ResponseEntity.ok(ApiResponse.success("Flights found", flights));
    }
    
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<FlightDTO>>> searchFlightsWithParams(
            @RequestParam String departure,
            @RequestParam String arrival,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime departureDate,
            @RequestParam(defaultValue = "1") Integer passengers,
            @RequestParam(required = false) Double maxPrice,
            @RequestParam(required = false) String airlineCode,
            @RequestParam(defaultValue = "DEPARTURE_TIME") String sortBy,
            @RequestParam(defaultValue = "ASC") String sortOrder) {
        
        FlightSearchCriteria criteria = new FlightSearchCriteria();
        criteria.setDepartureAirport(departure);
        criteria.setArrivalAirport(arrival);
        criteria.setDepartureDate(departureDate);
        criteria.setPassengers(passengers);
        criteria.setMaxPrice(maxPrice);
        criteria.setAirlineCode(airlineCode);
        criteria.setSortBy(sortBy);
        criteria.setSortOrder(sortOrder);
        
        List<FlightDTO> flights = flightService.searchFlights(criteria);
        return ResponseEntity.ok(ApiResponse.success("Flights found", flights));
    }
    
    @GetMapping("/route")
    public ResponseEntity<ApiResponse<FlightGraph.RouteResult>> findOptimalRoute(
            @RequestParam String departure,
            @RequestParam String arrival) {
        FlightGraph.RouteResult route = flightService.findOptimalRoute(departure, arrival);
        return ResponseEntity.ok(ApiResponse.success("Optimal route found", route));
    }
    
    @GetMapping("/airports/search")
    public ResponseEntity<ApiResponse<List<String>>> searchAirports(@RequestParam String prefix) {
        List<String> airports = flightService.searchAirports(prefix);
        return ResponseEntity.ok(ApiResponse.success("Airports found", airports));
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<FlightDTO>> updateFlight(@PathVariable Long id, 
                                                              @Valid @RequestBody FlightDTO flightDTO) {
        FlightDTO updatedFlight = flightService.updateFlight(id, flightDTO);
        return ResponseEntity.ok(ApiResponse.success("Flight updated successfully", updatedFlight));
    }
    
    @PatchMapping("/{id}/status")
    public ResponseEntity<ApiResponse<FlightDTO>> updateFlightStatus(@PathVariable Long id, 
                                                                    @RequestParam Flight.FlightStatus status) {
        FlightDTO updatedFlight = flightService.updateFlightStatus(id, status);
        return ResponseEntity.ok(ApiResponse.success("Flight status updated", updatedFlight));
    }
    
    @PostMapping("/{id}/reserve-seats")
    public ResponseEntity<ApiResponse<Boolean>> reserveSeats(@PathVariable Long id, 
                                                           @RequestParam Integer numberOfSeats) {
        boolean success = flightService.reserveSeats(id, numberOfSeats);
        if (success) {
            return ResponseEntity.ok(ApiResponse.success("Seats reserved successfully", true));
        } else {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Insufficient available seats"));
        }
    }
    
    @PostMapping("/{id}/release-seats")
    public ResponseEntity<ApiResponse<Void>> releaseSeats(@PathVariable Long id, 
                                                         @RequestParam Integer numberOfSeats) {
        flightService.releaseSeats(id, numberOfSeats);
        return ResponseEntity.ok(ApiResponse.success("Seats released successfully", null));
    }
}