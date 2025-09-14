package com.airticket.flight.service;

import com.airticket.common.exception.BusinessException;
import com.airticket.common.exception.ResourceNotFoundException;
import com.airticket.common.util.FlightGraph;
import com.airticket.common.util.Trie;
import com.airticket.flight.dto.FlightDTO;
import com.airticket.flight.dto.FlightSearchCriteria;
import com.airticket.flight.entity.Flight;
import com.airticket.flight.repository.FlightRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class FlightService {
    
    private final FlightRepository flightRepository;
    private final FlightMapper flightMapper;
    private final Trie<String> airportTrie = new Trie<>();
    private final FlightGraph flightGraph = new FlightGraph();
    
    @PostConstruct
    public void initializeDataStructures() {
        // Initialize airport search trie
        List<String> airports = flightRepository.findAllDepartureAirports();
        airports.addAll(flightRepository.findAllArrivalAirports());
        airports.forEach(airport -> airportTrie.insert(airport, airport));
        
        // Initialize flight route graph
        List<Flight> flights = flightRepository.findAll();
        flights.forEach(flight -> {
            flightGraph.addRoute(
                flight.getDepartureAirport(),
                flight.getArrivalAirport(),
                flight.getBasePrice(),
                flight.getDurationMinutes()
            );
        });
        
        log.info("Initialized flight data structures with {} airports", airports.size());
    }
    
    public FlightDTO createFlight(FlightDTO flightDTO) {
        log.info("Creating new flight: {}", flightDTO.getFlightNumber());
        
        if (flightRepository.findByFlightNumber(flightDTO.getFlightNumber()).isPresent()) {
            throw new BusinessException("Flight number already exists", "FLIGHT_ALREADY_EXISTS");
        }
        
        Flight flight = flightMapper.toEntity(flightDTO);
        flight.setAvailableSeats(flight.getTotalSeats());
        flight.setStatus(Flight.FlightStatus.SCHEDULED);
        flight.setCreatedAt(LocalDateTime.now());
        flight.setCreatedBy("SYSTEM");
        
        Flight savedFlight = flightRepository.save(flight);
        
        // Update data structures
        airportTrie.insert(flight.getDepartureAirport(), flight.getDepartureAirport());
        airportTrie.insert(flight.getArrivalAirport(), flight.getArrivalAirport());
        flightGraph.addRoute(flight.getDepartureAirport(), flight.getArrivalAirport(), 
                           flight.getBasePrice(), flight.getDurationMinutes());
        
        log.info("Flight created successfully with ID: {}", savedFlight.getId());
        return flightMapper.toDTO(savedFlight);
    }
    
    @Transactional(readOnly = true)
    @Cacheable(value = "flights", key = "#id")
    public FlightDTO getFlightById(Long id) {
        Flight flight = flightRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Flight", id.toString()));
        return flightMapper.toDTO(flight);
    }
    
    @Transactional(readOnly = true)
    @Cacheable(value = "flights", key = "#flightNumber")
    public FlightDTO getFlightByNumber(String flightNumber) {
        Flight flight = flightRepository.findByFlightNumber(flightNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Flight", flightNumber));
        return flightMapper.toDTO(flight);
    }
    
    @Transactional(readOnly = true)
    public List<FlightDTO> searchFlights(FlightSearchCriteria criteria) {
        log.info("Searching flights from {} to {} on {}", 
                criteria.getDepartureAirport(), criteria.getArrivalAirport(), criteria.getDepartureDate());
        
        // Simple search for same day
        LocalDateTime startOfDay = criteria.getDepartureDate().toLocalDate().atStartOfDay();
        LocalDateTime endOfDay = startOfDay.plusDays(1).minusSeconds(1);
        
        Sort sort = createSort(criteria.getSortBy(), criteria.getSortOrder());
        Pageable pageable = PageRequest.of(0, 100, sort);
        
        Page<Flight> flights = flightRepository.searchFlightsWithFilters(
                criteria.getDepartureAirport(),
                criteria.getArrivalAirport(),
                startOfDay,
                endOfDay,
                criteria.getPassengers() != null ? criteria.getPassengers() : 1,
                criteria.getMaxPrice(),
                criteria.getAirlineCode(),
                pageable
        );
        
        return flights.getContent().stream()
                .map(flightMapper::toDTO)
                .collect(Collectors.toList());
    }
    
    public FlightGraph.RouteResult findOptimalRoute(String departure, String arrival) {
        return flightGraph.findShortestRoute(departure, arrival);
    }
    
    public List<String> searchAirports(String prefix) {
        // This would be implemented with the Trie for autocomplete
        return flightRepository.findAllDepartureAirports().stream()
                .filter(airport -> airport.toLowerCase().startsWith(prefix.toLowerCase()))
                .collect(Collectors.toList());
    }
    
    @CacheEvict(value = "flights", key = "#id")
    public FlightDTO updateFlight(Long id, FlightDTO flightDTO) {
        Flight existingFlight = flightRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Flight", id.toString()));
        
        flightMapper.updateEntityFromDTO(flightDTO, existingFlight);
        existingFlight.setUpdatedAt(LocalDateTime.now());
        existingFlight.setUpdatedBy("SYSTEM");
        
        Flight updatedFlight = flightRepository.save(existingFlight);
        return flightMapper.toDTO(updatedFlight);
    }
    
    public FlightDTO updateFlightStatus(Long id, Flight.FlightStatus status) {
        Flight flight = flightRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Flight", id.toString()));
        
        flight.setStatus(status);
        flight.setUpdatedAt(LocalDateTime.now());
        
        Flight updatedFlight = flightRepository.save(flight);
        log.info("Flight {} status updated to {}", flight.getFlightNumber(), status);
        
        return flightMapper.toDTO(updatedFlight);
    }
    
    public synchronized boolean reserveSeats(Long flightId, Integer numberOfSeats) {
        Flight flight = flightRepository.findById(flightId)
                .orElseThrow(() -> new ResourceNotFoundException("Flight", flightId.toString()));
        
        if (flight.getAvailableSeats() < numberOfSeats) {
            return false;
        }
        
        flight.setAvailableSeats(flight.getAvailableSeats() - numberOfSeats);
        flightRepository.save(flight);
        
        log.info("Reserved {} seats for flight {}. Available seats: {}", 
                numberOfSeats, flight.getFlightNumber(), flight.getAvailableSeats());
        return true;
    }
    
    public void releaseSeats(Long flightId, Integer numberOfSeats) {
        Flight flight = flightRepository.findById(flightId)
                .orElseThrow(() -> new ResourceNotFoundException("Flight", flightId.toString()));
        
        flight.setAvailableSeats(flight.getAvailableSeats() + numberOfSeats);
        flightRepository.save(flight);
        
        log.info("Released {} seats for flight {}. Available seats: {}", 
                numberOfSeats, flight.getFlightNumber(), flight.getAvailableSeats());
    }
    
    private Sort createSort(String sortBy, String sortOrder) {
        Sort.Direction direction = "DESC".equalsIgnoreCase(sortOrder) ? 
                Sort.Direction.DESC : Sort.Direction.ASC;
        
        String sortField = switch (sortBy != null ? sortBy.toUpperCase() : "DEPARTURE_TIME") {
            case "PRICE" -> "basePrice";
            case "DURATION" -> "durationMinutes";
            case "DEPARTURE_TIME" -> "departureTime";
            default -> "departureTime";
        };
        
        return Sort.by(direction, sortField);
    }
}