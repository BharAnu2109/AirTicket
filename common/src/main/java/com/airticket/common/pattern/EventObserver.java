package com.airticket.common.pattern;

import java.util.ArrayList;
import java.util.List;

/**
 * Observer Pattern for event notifications
 */
public interface EventObserver<T> {
    void handleEvent(T event);
}

class EventPublisher<T> {
    private final List<EventObserver<T>> observers = new ArrayList<>();

    public void addObserver(EventObserver<T> observer) {
        observers.add(observer);
    }

    public void removeObserver(EventObserver<T> observer) {
        observers.remove(observer);
    }

    public void notifyObservers(T event) {
        for (EventObserver<T> observer : observers) {
            try {
                observer.handleEvent(event);
            } catch (Exception e) {
                // Log error but continue with other observers
                System.err.println("Error notifying observer: " + e.getMessage());
            }
        }
    }
}