package com.airticket.flight.repository;

import com.airticket.flight.entity.Flight;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface FlightRepository extends JpaRepository<Flight, Long> {
    
    Optional<Flight> findByFlightNumber(String flightNumber);
    
    List<Flight> findByDepartureAirportAndArrivalAirport(String departureAirport, String arrivalAirport);
    
    @Query("SELECT f FROM Flight f WHERE f.departureAirport = :departure " +
           "AND f.arrivalAirport = :arrival " +
           "AND DATE(f.departureTime) = DATE(:departureDate) " +
           "AND f.availableSeats >= :passengers " +
           "AND f.status = 'SCHEDULED'")
    List<Flight> searchFlights(@Param("departure") String departureAirport,
                              @Param("arrival") String arrivalAirport,
                              @Param("departureDate") LocalDateTime departureDate,
                              @Param("passengers") Integer passengers);
    
    @Query("SELECT f FROM Flight f WHERE f.departureAirport = :departure " +
           "AND f.arrivalAirport = :arrival " +
           "AND f.departureTime BETWEEN :startDate AND :endDate " +
           "AND f.availableSeats >= :passengers " +
           "AND f.status = 'SCHEDULED' " +
           "AND (:maxPrice IS NULL OR f.basePrice <= :maxPrice) " +
           "AND (:airlineCode IS NULL OR f.airlineCode = :airlineCode)")
    Page<Flight> searchFlightsWithFilters(@Param("departure") String departureAirport,
                                         @Param("arrival") String arrivalAirport,
                                         @Param("startDate") LocalDateTime startDate,
                                         @Param("endDate") LocalDateTime endDate,
                                         @Param("passengers") Integer passengers,
                                         @Param("maxPrice") Double maxPrice,
                                         @Param("airlineCode") String airlineCode,
                                         Pageable pageable);
    
    List<Flight> findByAirlineCode(String airlineCode);
    
    List<Flight> findByStatus(Flight.FlightStatus status);
    
    @Query("SELECT f FROM Flight f WHERE f.departureTime BETWEEN :start AND :end")
    List<Flight> findFlightsByDateRange(@Param("start") LocalDateTime start, 
                                       @Param("end") LocalDateTime end);
    
    @Query("SELECT DISTINCT f.departureAirport FROM Flight f ORDER BY f.departureAirport")
    List<String> findAllDepartureAirports();
    
    @Query("SELECT DISTINCT f.arrivalAirport FROM Flight f ORDER BY f.arrivalAirport")
    List<String> findAllArrivalAirports();
}