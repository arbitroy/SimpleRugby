package com.simplyrugby.util;

import java.util.HashMap;
import java.util.Map;

/**
 * Simple service locator for managing service dependencies.
 */
public class ServiceLocator {
    private static final Map<Class<?>, Object> services = new HashMap<>();

    /**
     * Register a service implementation.
     *
     * @param serviceClass The service interface class
     * @param implementation The service implementation
     * @param <T> The service type
     */
    public static <T> void register(Class<T> serviceClass, T implementation) {
        services.put(serviceClass, implementation);
    }

    /**
     * Get a service implementation.
     *
     * @param serviceClass The service interface class
     * @param <T> The service type
     * @return The service implementation
     */
    @SuppressWarnings("unchecked")
    public static <T> T getService(Class<T> serviceClass) {
        T service = (T) services.get(serviceClass);
        if (service == null) {
            throw new IllegalStateException("Service not registered: " + serviceClass.getName());
        }
        return service;
    }

    /**
     * Clear all registered services.
     */
    public static void clear() {
        services.clear();
    }
}