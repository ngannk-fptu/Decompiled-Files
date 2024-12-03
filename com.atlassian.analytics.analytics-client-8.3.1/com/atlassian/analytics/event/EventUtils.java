/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.ImmutableMap$Builder
 */
package com.atlassian.analytics.event;

import com.google.common.collect.ImmutableMap;
import java.util.Map;

public class EventUtils {
    private static final String BROWSER_EVENT_NAME = "browser";
    private static final String MOBILE_EVENT_NAME = "mobile";

    private static boolean isClientEvent(String name) {
        return BROWSER_EVENT_NAME.equals(name) || MOBILE_EVENT_NAME.equals(name);
    }

    @Deprecated
    public static String getBrowserEventName(String name, Map<String, Object> properties) {
        return EventUtils.getEventName(name, properties);
    }

    public static String getEventName(String name, Map<String, Object> properties) {
        Object nameFromProperties = properties != null ? properties.get("name") : null;
        return EventUtils.isClientEvent(name) && nameFromProperties != null ? nameFromProperties.toString() : name;
    }

    @Deprecated
    public static Map<String, Object> getBrowserEventProperties(String name, Map<String, Object> properties) {
        return EventUtils.getEventProperties(name, properties);
    }

    public static Map<String, Object> getEventProperties(String name, Map<String, Object> properties) {
        if (EventUtils.isClientEvent(name)) {
            return EventUtils.getClientEventProperties(properties);
        }
        return properties;
    }

    private static Map<String, Object> getClientEventProperties(Map<String, Object> properties) {
        ImmutableMap.Builder builder = ImmutableMap.builder();
        for (Map.Entry<String, Object> entry : properties.entrySet()) {
            String propertyKey = entry.getKey();
            if (!propertyKey.startsWith("properties.")) continue;
            builder.put((Object)propertyKey.substring("properties.".length()), entry.getValue());
        }
        return builder.build();
    }
}

