package com.airticket.booking.service;

import com.airticket.booking.dto.BookingDTO;
import com.airticket.booking.dto.BookingRequest;
import com.airticket.booking.entity.Booking;
import com.airticket.booking.repository.BookingRepository;
import com.airticket.booking.saga.*;
import com.airticket.common.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.statemachine.StateMachine;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class BookingService {
    
    private final BookingRepository bookingRepository;
    private final BookingMapper bookingMapper;
    private final FlightServiceClient flightServiceClient;
    private final BookingSagaStateMachine sagaStateMachine;
    
    public BookingDTO createBooking(BookingRequest request) {
        log.info("Creating booking for user {} and flight {}", request.getUserId(), request.getFlightId());
        
        // Get flight information and calculate total amount
        FlightServiceClient.FlightInfo flight = flightServiceClient.getFlightById(request.getFlightId());
        Double totalAmount = flight.getBasePrice() * request.getNumberOfSeats();
        
        // Create booking entity
        Booking booking = new Booking();
        booking.setBookingReference(generateBookingReference());
        booking.setUserId(request.getUserId());
        booking.setFlightId(request.getFlightId());
        booking.setPassengerName(request.getPassengerName());
        booking.setPassengerEmail(request.getPassengerEmail());
        booking.setPassengerPhone(request.getPassengerPhone());
        booking.setNumberOfSeats(request.getNumberOfSeats());
        booking.setTotalAmount(totalAmount);
        booking.setStatus(Booking.BookingStatus.PENDING);
        booking.setBookingDate(LocalDateTime.now());
        booking.setSpecialRequirements(request.getSpecialRequirements());
        booking.setSagaId(UUID.randomUUID().toString());
        booking.setSagaStatus(Booking.SagaStatus.STARTED);
        booking.setCreatedAt(LocalDateTime.now());
        booking.setCreatedBy("SYSTEM");
        
        Booking savedBooking = bookingRepository.save(booking);
        
        // Start booking saga
        startBookingSaga(savedBooking, request);
        
        log.info("Booking created with ID: {} and reference: {}", savedBooking.getId(), savedBooking.getBookingReference());
        return bookingMapper.toDTO(savedBooking);
    }
    
    private void startBookingSaga(Booking booking, BookingRequest request) {
        try {
            BookingSagaData sagaData = new BookingSagaData();
            sagaData.setBookingId(booking.getId());
            sagaData.setSagaId(booking.getSagaId());
            sagaData.setUserId(booking.getUserId());
            sagaData.setFlightId(booking.getFlightId());
            sagaData.setPassengerName(booking.getPassengerName());
            sagaData.setPassengerEmail(booking.getPassengerEmail());
            sagaData.setPassengerPhone(booking.getPassengerPhone());
            sagaData.setNumberOfSeats(booking.getNumberOfSeats());
            sagaData.setTotalAmount(booking.getTotalAmount());
            sagaData.setPaymentMethod(request.getPaymentMethod());
            sagaData.setCardNumber(request.getCardNumber());
            sagaData.setCardHolderName(request.getCardHolderName());
            sagaData.setSpecialRequirements(booking.getSpecialRequirements());
            sagaData.setCurrentStatus(booking.getStatus());
            
            StateMachine<BookingSagaState, BookingSagaEvent> stateMachine = 
                    sagaStateMachine.createStateMachine(booking.getSagaId());
            
            stateMachine.getExtendedState().getVariables().put("sagaData", sagaData);
            stateMachine.start();
            stateMachine.sendEvent(BookingSagaEvent.START_BOOKING);
            
        } catch (Exception e) {
            log.error("Failed to start booking saga for booking {}: {}", booking.getId(), e.getMessage());
            booking.setStatus(Booking.BookingStatus.FAILED);
            booking.setSagaStatus(Booking.SagaStatus.FAILED);
            bookingRepository.save(booking);
        }
    }
    
    @Transactional(readOnly = true)
    public BookingDTO getBookingById(Long id) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Booking", id.toString()));
        return bookingMapper.toDTO(booking);
    }
    
    @Transactional(readOnly = true)
    public BookingDTO getBookingByReference(String reference) {
        Booking booking = bookingRepository.findByBookingReference(reference)
                .orElseThrow(() -> new ResourceNotFoundException("Booking", reference));
        return bookingMapper.toDTO(booking);
    }
    
    @Transactional(readOnly = true)
    public List<BookingDTO> getBookingsByUserId(Long userId) {
        return bookingRepository.findByUserId(userId).stream()
                .map(bookingMapper::toDTO)
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public List<BookingDTO> getBookingsByFlightId(Long flightId) {
        return bookingRepository.findByFlightId(flightId).stream()
                .map(bookingMapper::toDTO)
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public List<BookingDTO> getBookingsByStatus(Booking.BookingStatus status) {
        return bookingRepository.findByStatus(status).stream()
                .map(bookingMapper::toDTO)
                .collect(Collectors.toList());
    }
    
    public BookingDTO cancelBooking(Long id) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Booking", id.toString()));
        
        if (booking.getStatus() == Booking.BookingStatus.CONFIRMED || 
            booking.getStatus() == Booking.BookingStatus.PENDING) {
            
            booking.setStatus(Booking.BookingStatus.CANCELLED);
            booking.setUpdatedAt(LocalDateTime.now());
            booking.setUpdatedBy("SYSTEM");
            
            // Release seats
            try {
                flightServiceClient.releaseSeats(booking.getFlightId(), booking.getNumberOfSeats());
            } catch (Exception e) {
                log.error("Failed to release seats for cancelled booking {}: {}", id, e.getMessage());
            }
            
            Booking updatedBooking = bookingRepository.save(booking);
            log.info("Booking {} cancelled successfully", id);
            
            return bookingMapper.toDTO(updatedBooking);
        }
        
        throw new IllegalStateException("Booking cannot be cancelled in current status: " + booking.getStatus());
    }
    
    @Transactional(readOnly = true)
    public List<BookingDTO> getBookingsByPassengerEmail(String email) {
        return bookingRepository.findByPassengerEmail(email).stream()
                .map(bookingMapper::toDTO)
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public List<BookingDTO> getBookingsInDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return bookingRepository.findBookingsByDateRange(startDate, endDate).stream()
                .map(bookingMapper::toDTO)
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public Long getCompletedBookingsCount(LocalDateTime since) {
        return bookingRepository.countCompletedBookingsSince(since);
    }
    
    @Transactional(readOnly = true)
    public Double getTotalRevenue(LocalDateTime startDate, LocalDateTime endDate) {
        Double revenue = bookingRepository.getTotalRevenueInDateRange(startDate, endDate);
        return revenue != null ? revenue : 0.0;
    }
    
    private String generateBookingReference() {
        return "AIR" + System.currentTimeMillis() + UUID.randomUUID().toString().substring(0, 6).toUpperCase();
    }
}