package com.airticket.notification.service;

import com.airticket.common.event.NotificationEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {
    
    private final JavaMailSender mailSender;
    
    @KafkaListener(topics = "notification-events", groupId = "notification-service-group")
    public void handleNotificationEvent(NotificationEvent event) {
        log.info("Received notification event for recipient: {}", event.getRecipient());
        
        try {
            if ("EMAIL".equalsIgnoreCase(event.getNotificationType())) {
                sendEmail(event);
            } else if ("SMS".equalsIgnoreCase(event.getNotificationType())) {
                sendSMS(event);
            }
        } catch (Exception e) {
            log.error("Failed to send notification: {}", e.getMessage());
        }
    }
    
    private void sendEmail(NotificationEvent event) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(event.getRecipient());
            message.setSubject(event.getSubject());
            message.setText(event.getMessage());
            message.setFrom("noreply@airticket.com");
            
            mailSender.send(message);
            log.info("Email sent successfully to {}", event.getRecipient());
            
        } catch (Exception e) {
            log.error("Failed to send email to {}: {}", event.getRecipient(), e.getMessage());
        }
    }
    
    private void sendSMS(NotificationEvent event) {
        // SMS implementation would go here
        // For demo purposes, just log
        log.info("SMS would be sent to {}: {}", event.getRecipient(), event.getMessage());
    }
}