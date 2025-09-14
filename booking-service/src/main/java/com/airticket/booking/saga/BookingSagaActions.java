package com.airticket.booking.saga;

import com.airticket.booking.entity.Booking;
import com.airticket.booking.repository.BookingRepository;
import com.airticket.booking.service.FlightServiceClient;
import com.airticket.booking.service.PaymentServiceClient;
import com.airticket.common.event.BookingCreatedEvent;
import com.airticket.common.event.NotificationEvent;
import com.airticket.common.event.PaymentProcessedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.statemachine.StateContext;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class BookingSagaActions {
    
    private final BookingRepository bookingRepository;
    private final FlightServiceClient flightServiceClient;
    private final PaymentServiceClient paymentServiceClient;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    
    public void reserveSeats(StateContext<BookingSagaState, BookingSagaEvent> context) {
        BookingSagaData sagaData = getSagaData(context);
        log.info("Reserving {} seats for flight {} in saga {}", 
                sagaData.getNumberOfSeats(), sagaData.getFlightId(), sagaData.getSagaId());
        
        try {
            boolean success = flightServiceClient.reserveSeats(sagaData.getFlightId(), sagaData.getNumberOfSeats());
            
            if (success) {
                updateBookingStatus(sagaData.getBookingId(), Booking.BookingStatus.PENDING, Booking.SagaStatus.SEATS_RESERVED);
                context.getStateMachine().sendEvent(BookingSagaEvent.SEATS_RESERVED);
            } else {
                updateBookingStatus(sagaData.getBookingId(), Booking.BookingStatus.FAILED, Booking.SagaStatus.FAILED);
                context.getStateMachine().sendEvent(BookingSagaEvent.SEATS_RESERVATION_FAILED);
            }
        } catch (Exception e) {
            log.error("Failed to reserve seats for saga {}: {}", sagaData.getSagaId(), e.getMessage());
            context.getStateMachine().sendEvent(BookingSagaEvent.SEATS_RESERVATION_FAILED);
        }
    }
    
    public void processPayment(StateContext<BookingSagaState, BookingSagaEvent> context) {
        BookingSagaData sagaData = getSagaData(context);
        log.info("Processing payment for booking {} in saga {}", sagaData.getBookingId(), sagaData.getSagaId());
        
        try {
            // Create payment request
            PaymentServiceClient.PaymentRequest paymentRequest = new PaymentServiceClient.PaymentRequest();
            paymentRequest.setBookingId(sagaData.getBookingId());
            paymentRequest.setAmount(sagaData.getTotalAmount());
            paymentRequest.setPaymentMethod(sagaData.getPaymentMethod());
            paymentRequest.setCardNumber(sagaData.getCardNumber());
            paymentRequest.setCardHolderName(sagaData.getCardHolderName());
            
            PaymentServiceClient.PaymentResponse response = paymentServiceClient.processPayment(paymentRequest);
            
            if (response.isSuccess()) {
                sagaData.setPaymentId(response.getPaymentId());
                sagaData.setTransactionId(response.getTransactionId());
                updateBookingStatus(sagaData.getBookingId(), Booking.BookingStatus.CONFIRMED, Booking.SagaStatus.PAYMENT_PROCESSED);
                context.getStateMachine().sendEvent(BookingSagaEvent.PAYMENT_PROCESSED);
            } else {
                sagaData.setFailureReason(response.getMessage());
                context.getStateMachine().sendEvent(BookingSagaEvent.PAYMENT_FAILED);
            }
        } catch (Exception e) {
            log.error("Failed to process payment for saga {}: {}", sagaData.getSagaId(), e.getMessage());
            context.getStateMachine().sendEvent(BookingSagaEvent.PAYMENT_FAILED);
        }
    }
    
    public void sendNotification(StateContext<BookingSagaState, BookingSagaEvent> context) {
        BookingSagaData sagaData = getSagaData(context);
        log.info("Sending notification for booking {} in saga {}", sagaData.getBookingId(), sagaData.getSagaId());
        
        try {
            // Publish booking created event
            BookingCreatedEvent bookingEvent = new BookingCreatedEvent(
                    sagaData.getBookingId(),
                    sagaData.getUserId(),
                    sagaData.getFlightId(),
                    sagaData.getPassengerName(),
                    sagaData.getPassengerEmail(),
                    sagaData.getTotalAmount(),
                    "CONFIRMED"
            );
            
            kafkaTemplate.send("booking-events", bookingEvent);
            
            // Send notification event
            NotificationEvent notificationEvent = new NotificationEvent(
                    sagaData.getPassengerEmail(),
                    "EMAIL",
                    "Booking Confirmation",
                    "Your booking has been confirmed!",
                    "booking-confirmation",
                    bookingEvent
            );
            
            kafkaTemplate.send("notification-events", notificationEvent);
            
            updateBookingStatus(sagaData.getBookingId(), Booking.BookingStatus.CONFIRMED, Booking.SagaStatus.NOTIFICATION_SENT);
            context.getStateMachine().sendEvent(BookingSagaEvent.NOTIFICATION_SENT);
            
        } catch (Exception e) {
            log.error("Failed to send notification for saga {}: {}", sagaData.getSagaId(), e.getMessage());
            context.getStateMachine().sendEvent(BookingSagaEvent.NOTIFICATION_FAILED);
        }
    }
    
    public void completeBooking(StateContext<BookingSagaState, BookingSagaEvent> context) {
        BookingSagaData sagaData = getSagaData(context);
        log.info("Completing booking {} in saga {}", sagaData.getBookingId(), sagaData.getSagaId());
        
        updateBookingStatus(sagaData.getBookingId(), Booking.BookingStatus.COMPLETED, Booking.SagaStatus.COMPLETED);
    }
    
    public void compensateSeats(StateContext<BookingSagaState, BookingSagaEvent> context) {
        BookingSagaData sagaData = getSagaData(context);
        log.info("Compensating seats for booking {} in saga {}", sagaData.getBookingId(), sagaData.getSagaId());
        
        try {
            flightServiceClient.releaseSeats(sagaData.getFlightId(), sagaData.getNumberOfSeats());
            context.getStateMachine().sendEvent(BookingSagaEvent.SAGA_FAILED);
        } catch (Exception e) {
            log.error("Failed to compensate seats for saga {}: {}", sagaData.getSagaId(), e.getMessage());
            context.getStateMachine().sendEvent(BookingSagaEvent.SAGA_FAILED);
        }
    }
    
    public void compensatePayment(StateContext<BookingSagaState, BookingSagaEvent> context) {
        BookingSagaData sagaData = getSagaData(context);
        log.info("Compensating payment for booking {} in saga {}", sagaData.getBookingId(), sagaData.getSagaId());
        
        try {
            if (sagaData.getPaymentId() != null) {
                paymentServiceClient.refundPayment(sagaData.getPaymentId());
            }
            context.getStateMachine().sendEvent(BookingSagaEvent.COMPENSATE_SEATS);
        } catch (Exception e) {
            log.error("Failed to compensate payment for saga {}: {}", sagaData.getSagaId(), e.getMessage());
            context.getStateMachine().sendEvent(BookingSagaEvent.COMPENSATE_SEATS);
        }
    }
    
    public void handleFailure(StateContext<BookingSagaState, BookingSagaEvent> context) {
        BookingSagaData sagaData = getSagaData(context);
        log.error("Booking saga {} failed for booking {}", sagaData.getSagaId(), sagaData.getBookingId());
        
        updateBookingStatus(sagaData.getBookingId(), Booking.BookingStatus.FAILED, Booking.SagaStatus.FAILED);
    }
    
    private BookingSagaData getSagaData(StateContext<BookingSagaState, BookingSagaEvent> context) {
        return context.getExtendedState().get("sagaData", BookingSagaData.class);
    }
    
    private void updateBookingStatus(Long bookingId, Booking.BookingStatus status, Booking.SagaStatus sagaStatus) {
        bookingRepository.findById(bookingId).ifPresent(booking -> {
            booking.setStatus(status);
            booking.setSagaStatus(sagaStatus);
            bookingRepository.save(booking);
        });
    }
}