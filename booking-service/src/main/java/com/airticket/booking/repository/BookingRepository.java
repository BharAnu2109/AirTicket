package com.airticket.booking.repository;

import com.airticket.booking.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    
    Optional<Booking> findByBookingReference(String bookingReference);
    
    List<Booking> findByUserId(Long userId);
    
    List<Booking> findByFlightId(Long flightId);
    
    List<Booking> findByStatus(Booking.BookingStatus status);
    
    List<Booking> findBySagaStatus(Booking.SagaStatus sagaStatus);
    
    Optional<Booking> findBySagaId(String sagaId);
    
    @Query("SELECT b FROM Booking b WHERE b.userId = :userId AND b.status = :status")
    List<Booking> findByUserIdAndStatus(@Param("userId") Long userId, 
                                       @Param("status") Booking.BookingStatus status);
    
    @Query("SELECT b FROM Booking b WHERE b.bookingDate BETWEEN :startDate AND :endDate")
    List<Booking> findBookingsByDateRange(@Param("startDate") LocalDateTime startDate, 
                                         @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT COUNT(b) FROM Booking b WHERE b.status = 'COMPLETED' AND b.bookingDate >= :date")
    Long countCompletedBookingsSince(@Param("date") LocalDateTime date);
    
    @Query("SELECT SUM(b.totalAmount) FROM Booking b WHERE b.status = 'COMPLETED' AND b.bookingDate BETWEEN :startDate AND :endDate")
    Double getTotalRevenueInDateRange(@Param("startDate") LocalDateTime startDate, 
                                     @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT b FROM Booking b WHERE b.passengerEmail = :email")
    List<Booking> findByPassengerEmail(@Param("email") String email);
}