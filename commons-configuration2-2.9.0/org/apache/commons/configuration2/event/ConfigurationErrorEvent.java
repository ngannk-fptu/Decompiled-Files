/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.configuration2.event;

import org.apache.commons.configuration2.event.Event;
import org.apache.commons.configuration2.event.EventType;

public class ConfigurationErrorEvent
extends Event {
    public static final EventType<ConfigurationErrorEvent> ANY = new EventType<Event>(Event.ANY, "ERROR");
    public static final EventType<ConfigurationErrorEvent> READ = new EventType<ConfigurationErrorEvent>(ANY, "READ_ERROR");
    public static final EventType<ConfigurationErrorEvent> WRITE = new EventType<ConfigurationErrorEvent>(ANY, "WRITE_ERROR");
    private static final long serialVersionUID = 20140712L;
    private final EventType<?> errorOperationType;
    private final String propertyName;
    private final Object propertyValue;
    private final Throwable cause;

    public ConfigurationErrorEvent(Object source, EventType<? extends ConfigurationErrorEvent> eventType, EventType<?> operationType, String propName, Object propValue, Throwable cause) {
        super(source, eventType);
        this.errorOperationType = operationType;
        this.propertyName = propName;
        this.propertyValue = propValue;
        this.cause = cause;
    }

    public EventType<?> getErrorOperationType() {
        return this.errorOperationType;
    }

    public String getPropertyName() {
        return this.propertyName;
    }

    public Object getPropertyValue() {
        return this.propertyValue;
    }

    public Throwable getCause() {
        return this.cause;
    }
}

