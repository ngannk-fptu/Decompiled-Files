/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.configuration2.event;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import org.apache.commons.configuration2.event.ConfigurationErrorEvent;
import org.apache.commons.configuration2.event.ConfigurationEvent;
import org.apache.commons.configuration2.event.Event;
import org.apache.commons.configuration2.event.EventListener;
import org.apache.commons.configuration2.event.EventListenerList;
import org.apache.commons.configuration2.event.EventListenerRegistrationData;
import org.apache.commons.configuration2.event.EventSource;
import org.apache.commons.configuration2.event.EventType;

public class BaseEventSource
implements EventSource {
    private EventListenerList eventListeners;
    private final Object lockDetailEventsCount = new Object();
    private int detailEvents;

    public BaseEventSource() {
        this.initListeners();
    }

    public <T extends Event> Collection<EventListener<? super T>> getEventListeners(EventType<T> eventType) {
        LinkedList result = new LinkedList();
        this.eventListeners.getEventListeners(eventType).forEach(result::add);
        return Collections.unmodifiableCollection(result);
    }

    public List<EventListenerRegistrationData<?>> getEventListenerRegistrations() {
        return this.eventListeners.getRegistrations();
    }

    public boolean isDetailEvents() {
        return this.checkDetailEvents(0);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void setDetailEvents(boolean enable) {
        Object object = this.lockDetailEventsCount;
        synchronized (object) {
            this.detailEvents = enable ? ++this.detailEvents : --this.detailEvents;
        }
    }

    @Override
    public <T extends Event> void addEventListener(EventType<T> eventType, EventListener<? super T> listener) {
        this.eventListeners.addEventListener(eventType, listener);
    }

    @Override
    public <T extends Event> boolean removeEventListener(EventType<T> eventType, EventListener<? super T> listener) {
        return this.eventListeners.removeEventListener(eventType, listener);
    }

    public void clearEventListeners() {
        this.eventListeners.clear();
    }

    public void clearErrorListeners() {
        this.eventListeners.getRegistrationsForSuperType(ConfigurationErrorEvent.ANY).forEach(this.eventListeners::removeEventListener);
    }

    public void copyEventListeners(BaseEventSource source) {
        if (source == null) {
            throw new IllegalArgumentException("Target event source must not be null!");
        }
        source.eventListeners.addAll(this.eventListeners);
    }

    protected <T extends ConfigurationEvent> void fireEvent(EventType<T> type, String propName, Object propValue, boolean before) {
        EventListenerList.EventListenerIterator<T> it;
        if (this.checkDetailEvents(-1) && (it = this.eventListeners.getEventListenerIterator(type)).hasNext()) {
            ConfigurationEvent event = this.createEvent(type, propName, propValue, before);
            while (it.hasNext()) {
                it.invokeNext(event);
            }
        }
    }

    protected <T extends ConfigurationEvent> ConfigurationEvent createEvent(EventType<T> type, String propName, Object propValue, boolean before) {
        return new ConfigurationEvent(this, type, propName, propValue, before);
    }

    public <T extends ConfigurationErrorEvent> void fireError(EventType<T> eventType, EventType<?> operationType, String propertyName, Object propertyValue, Throwable cause) {
        EventListenerList.EventListenerIterator<T> iterator = this.eventListeners.getEventListenerIterator(eventType);
        if (iterator.hasNext()) {
            ConfigurationErrorEvent event = this.createErrorEvent(eventType, operationType, propertyName, propertyValue, cause);
            while (iterator.hasNext()) {
                iterator.invokeNext(event);
            }
        }
    }

    protected ConfigurationErrorEvent createErrorEvent(EventType<? extends ConfigurationErrorEvent> type, EventType<?> opType, String propName, Object propValue, Throwable ex) {
        return new ConfigurationErrorEvent(this, type, opType, propName, propValue, ex);
    }

    protected Object clone() throws CloneNotSupportedException {
        BaseEventSource copy = (BaseEventSource)super.clone();
        copy.initListeners();
        return copy;
    }

    private void initListeners() {
        this.eventListeners = new EventListenerList();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private boolean checkDetailEvents(int limit) {
        Object object = this.lockDetailEventsCount;
        synchronized (object) {
            return this.detailEvents > limit;
        }
    }
}

