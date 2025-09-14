package com.airticket.common.event;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class NotificationEvent extends BaseEvent {
    private String recipient;
    private String notificationType; // EMAIL, SMS
    private String subject;
    private String message;
    private String templateName;
    private Object templateData;

    public NotificationEvent() {
        super();
    }

    public NotificationEvent(String recipient, String notificationType, 
                           String subject, String message, String templateName, Object templateData) {
        super();
        this.recipient = recipient;
        this.notificationType = notificationType;
        this.subject = subject;
        this.message = message;
        this.templateName = templateName;
        this.templateData = templateData;
    }
}