/*
 * Decompiled with CFR 0.152.
 */
package ch.qos.logback.core.spi;

public class ConfigurationEvent {
    final EventType eventType;
    final Object data;

    private ConfigurationEvent(EventType eventType, Object data) {
        this.eventType = eventType;
        this.data = data;
    }

    public static ConfigurationEvent newConfigurationChangeDetectorRunningEvent(Object data) {
        return new ConfigurationEvent(EventType.CHANGE_DETECTOR_RUNNING, data);
    }

    public static ConfigurationEvent newConfigurationChangeDetectorRegisteredEvent(Object data) {
        return new ConfigurationEvent(EventType.CHANGE_DETECTOR_REGISTERED, data);
    }

    public static ConfigurationEvent newConfigurationChangeDetectedEvent(Object data) {
        return new ConfigurationEvent(EventType.CHANGE_DETECTED, data);
    }

    public static ConfigurationEvent newConfigurationStartedEvent(Object data) {
        return new ConfigurationEvent(EventType.CONFIGURATION_STARTED, data);
    }

    public static ConfigurationEvent newConfigurationEndedEvent(Object data) {
        return new ConfigurationEvent(EventType.CONFIGURATION_ENDED, data);
    }

    public EventType getEventType() {
        return this.eventType;
    }

    public Object getData() {
        return this.data;
    }

    public String toString() {
        return "ConfigurationEvent{eventType=" + (Object)((Object)this.eventType) + ", data=" + this.data + '}';
    }

    public static enum EventType {
        CHANGE_DETECTOR_REGISTERED,
        CHANGE_DETECTOR_RUNNING,
        CHANGE_DETECTED,
        CONFIGURATION_STARTED,
        CONFIGURATION_ENDED;

    }
}

