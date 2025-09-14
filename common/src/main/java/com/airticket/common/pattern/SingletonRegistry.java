package com.airticket.common.pattern;

import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

/**
 * Singleton Pattern implementation with thread safety
 */
public class SingletonRegistry {
    private static final ConcurrentHashMap<String, Object> instances = new ConcurrentHashMap<>();

    @SuppressWarnings("unchecked")
    public static <T> T getInstance(String key, Function<String, T> factory) {
        return (T) instances.computeIfAbsent(key, factory);
    }

    public static void removeInstance(String key) {
        instances.remove(key);
    }

    public static void clear() {
        instances.clear();
    }
}