package com.airticket.booking.saga;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.config.StateMachineBuilder;
import org.springframework.statemachine.config.StateMachineBuilder.Builder;
import org.springframework.stereotype.Component;

import java.util.EnumSet;

import static com.airticket.booking.saga.BookingSagaEvent.*;
import static com.airticket.booking.saga.BookingSagaState.*;

@Component
@RequiredArgsConstructor
@Slf4j
public class BookingSagaStateMachine {
    
    private final BookingSagaActions sagaActions;
    
    public StateMachine<BookingSagaState, BookingSagaEvent> createStateMachine(String sagaId) throws Exception {
        Builder<BookingSagaState, BookingSagaEvent> builder = StateMachineBuilder.builder();
        
        builder.configureStates()
                .withStates()
                .initial(INITIAL)
                .states(EnumSet.allOf(BookingSagaState.class))
                .end(COMPLETED)
                .end(FAILED);
        
        builder.configureTransitions()
                // Forward flow
                .withExternal()
                    .source(INITIAL).target(RESERVING_SEATS)
                    .event(START_BOOKING)
                    .action(sagaActions::reserveSeats)
                .and()
                .withExternal()
                    .source(RESERVING_SEATS).target(SEATS_RESERVED)
                    .event(SEATS_RESERVED)
                    .action(sagaActions::processPayment)
                .and()
                .withExternal()
                    .source(SEATS_RESERVED).target(PROCESSING_PAYMENT)
                    .event(PAYMENT_PROCESSED)
                .and()
                .withExternal()
                    .source(PROCESSING_PAYMENT).target(PAYMENT_PROCESSED)
                    .event(PAYMENT_PROCESSED)
                    .action(sagaActions::sendNotification)
                .and()
                .withExternal()
                    .source(PAYMENT_PROCESSED).target(SENDING_NOTIFICATION)
                    .event(NOTIFICATION_SENT)
                .and()
                .withExternal()
                    .source(SENDING_NOTIFICATION).target(COMPLETED)
                    .event(COMPLETE_BOOKING)
                    .action(sagaActions::completeBooking)
                
                // Compensation flow
                .and()
                .withExternal()
                    .source(RESERVING_SEATS).target(FAILED)
                    .event(SEATS_RESERVATION_FAILED)
                    .action(sagaActions::handleFailure)
                .and()
                .withExternal()
                    .source(PROCESSING_PAYMENT).target(COMPENSATING_SEATS)
                    .event(PAYMENT_FAILED)
                    .action(sagaActions::compensateSeats)
                .and()
                .withExternal()
                    .source(SENDING_NOTIFICATION).target(COMPENSATING_PAYMENT)
                    .event(NOTIFICATION_FAILED)
                    .action(sagaActions::compensatePayment)
                .and()
                .withExternal()
                    .source(COMPENSATING_PAYMENT).target(COMPENSATING_SEATS)
                    .event(COMPENSATE_SEATS)
                    .action(sagaActions::compensateSeats)
                .and()
                .withExternal()
                    .source(COMPENSATING_SEATS).target(FAILED)
                    .event(SAGA_FAILED)
                    .action(sagaActions::handleFailure);
        
        StateMachine<BookingSagaState, BookingSagaEvent> stateMachine = builder.build();
        stateMachine.getExtendedState().getVariables().put("sagaId", sagaId);
        
        return stateMachine;
    }
}