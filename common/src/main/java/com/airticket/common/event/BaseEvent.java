package com.airticket.common.event;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public abstract class BaseEvent {
    private String eventId;
    private LocalDateTime timestamp;
    private String eventType;
    private String source;

    protected BaseEvent() {
        this.eventId = UUID.randomUUID().toString();
        this.timestamp = LocalDateTime.now();
        this.eventType = this.getClass().getSimpleName();
        this.source = "air-ticket-system";
    }
}