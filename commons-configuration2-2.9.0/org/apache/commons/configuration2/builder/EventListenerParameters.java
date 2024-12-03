/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.configuration2.builder;

import java.util.Collections;
import java.util.Map;
import org.apache.commons.configuration2.builder.BuilderParameters;
import org.apache.commons.configuration2.builder.EventListenerProvider;
import org.apache.commons.configuration2.event.Event;
import org.apache.commons.configuration2.event.EventListener;
import org.apache.commons.configuration2.event.EventListenerList;
import org.apache.commons.configuration2.event.EventListenerRegistrationData;
import org.apache.commons.configuration2.event.EventType;

public class EventListenerParameters
implements BuilderParameters,
EventListenerProvider {
    private final EventListenerList eventListeners = new EventListenerList();

    public <T extends Event> EventListenerParameters addEventListener(EventType<T> eventType, EventListener<? super T> listener) {
        this.eventListeners.addEventListener(eventType, listener);
        return this;
    }

    public <T extends Event> EventListenerParameters addEventListener(EventListenerRegistrationData<T> registrationData) {
        this.eventListeners.addEventListener(registrationData);
        return this;
    }

    @Override
    public Map<String, Object> getParameters() {
        return Collections.emptyMap();
    }

    @Override
    public EventListenerList getListeners() {
        return this.eventListeners;
    }
}

