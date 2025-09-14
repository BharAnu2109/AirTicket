package com.airticket.booking.controller;

import com.airticket.booking.dto.BookingDTO;
import com.airticket.booking.dto.BookingRequest;
import com.airticket.booking.entity.Booking;
import com.airticket.booking.service.BookingService;
import com.airticket.common.dto.ApiResponse;
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
@RequestMapping("/api/v1/bookings")
@RequiredArgsConstructor
@Slf4j
public class BookingController {
    
    private final BookingService bookingService;
    
    @PostMapping
    public ResponseEntity<ApiResponse<BookingDTO>> createBooking(@Valid @RequestBody BookingRequest request) {
        log.info("Received booking request for user {} and flight {}", request.getUserId(), request.getFlightId());
        BookingDTO booking = bookingService.createBooking(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Booking created successfully", booking));
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<BookingDTO>> getBookingById(@PathVariable Long id) {
        BookingDTO booking = bookingService.getBookingById(id);
        return ResponseEntity.ok(ApiResponse.success(booking));
    }
    
    @GetMapping("/reference/{reference}")
    public ResponseEntity<ApiResponse<BookingDTO>> getBookingByReference(@PathVariable String reference) {
        BookingDTO booking = bookingService.getBookingByReference(reference);
        return ResponseEntity.ok(ApiResponse.success(booking));
    }
    
    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<List<BookingDTO>>> getBookingsByUserId(@PathVariable Long userId) {
        List<BookingDTO> bookings = bookingService.getBookingsByUserId(userId);
        return ResponseEntity.ok(ApiResponse.success("Bookings retrieved successfully", bookings));
    }
    
    @GetMapping("/flight/{flightId}")
    public ResponseEntity<ApiResponse<List<BookingDTO>>> getBookingsByFlightId(@PathVariable Long flightId) {
        List<BookingDTO> bookings = bookingService.getBookingsByFlightId(flightId);
        return ResponseEntity.ok(ApiResponse.success("Flight bookings retrieved successfully", bookings));
    }
    
    @GetMapping("/status/{status}")
    public ResponseEntity<ApiResponse<List<BookingDTO>>> getBookingsByStatus(@PathVariable Booking.BookingStatus status) {
        List<BookingDTO> bookings = bookingService.getBookingsByStatus(status);
        return ResponseEntity.ok(ApiResponse.success("Bookings by status retrieved successfully", bookings));
    }
    
    @GetMapping("/passenger/email/{email}")
    public ResponseEntity<ApiResponse<List<BookingDTO>>> getBookingsByPassengerEmail(@PathVariable String email) {
        List<BookingDTO> bookings = bookingService.getBookingsByPassengerEmail(email);
        return ResponseEntity.ok(ApiResponse.success("Passenger bookings retrieved successfully", bookings));
    }
    
    @GetMapping("/date-range")
    public ResponseEntity<ApiResponse<List<BookingDTO>>> getBookingsInDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        List<BookingDTO> bookings = bookingService.getBookingsInDateRange(startDate, endDate);
        return ResponseEntity.ok(ApiResponse.success("Bookings in date range retrieved successfully", bookings));
    }
    
    @PutMapping("/{id}/cancel")
    public ResponseEntity<ApiResponse<BookingDTO>> cancelBooking(@PathVariable Long id) {
        BookingDTO cancelledBooking = bookingService.cancelBooking(id);
        return ResponseEntity.ok(ApiResponse.success("Booking cancelled successfully", cancelledBooking));
    }
    
    @GetMapping("/stats/completed")
    public ResponseEntity<ApiResponse<Long>> getCompletedBookingsCount(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime since) {
        Long count = bookingService.getCompletedBookingsCount(since);
        return ResponseEntity.ok(ApiResponse.success("Completed bookings count retrieved", count));
    }
    
    @GetMapping("/stats/revenue")
    public ResponseEntity<ApiResponse<Double>> getTotalRevenue(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        Double revenue = bookingService.getTotalRevenue(startDate, endDate);
        return ResponseEntity.ok(ApiResponse.success("Total revenue retrieved", revenue));
    }
}