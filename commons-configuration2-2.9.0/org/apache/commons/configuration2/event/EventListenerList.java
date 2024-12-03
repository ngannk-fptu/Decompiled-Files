/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.configuration2.event;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import org.apache.commons.configuration2.event.Event;
import org.apache.commons.configuration2.event.EventListener;
import org.apache.commons.configuration2.event.EventListenerRegistrationData;
import org.apache.commons.configuration2.event.EventType;

public class EventListenerList {
    private final List<EventListenerRegistrationData<?>> listeners = new CopyOnWriteArrayList();

    public <T extends Event> void addEventListener(EventType<T> type, EventListener<? super T> listener) {
        this.listeners.add(new EventListenerRegistrationData<T>(type, listener));
    }

    public <T extends Event> void addEventListener(EventListenerRegistrationData<T> regData) {
        if (regData == null) {
            throw new IllegalArgumentException("EventListenerRegistrationData must not be null!");
        }
        this.listeners.add(regData);
    }

    public <T extends Event> boolean removeEventListener(EventType<T> eventType, EventListener<? super T> listener) {
        return listener != null && eventType != null && this.removeEventListener(new EventListenerRegistrationData<T>(eventType, listener));
    }

    public <T extends Event> boolean removeEventListener(EventListenerRegistrationData<T> regData) {
        return this.listeners.remove(regData);
    }

    public void fire(Event event) {
        if (event == null) {
            throw new IllegalArgumentException("Event to be fired must not be null!");
        }
        EventListenerIterator<? extends Event> iterator = this.getEventListenerIterator(event.getEventType());
        while (iterator.hasNext()) {
            ((EventListenerIterator)iterator).invokeNextListenerUnchecked(event);
        }
    }

    public <T extends Event> Iterable<EventListener<? super T>> getEventListeners(EventType<T> eventType) {
        return () -> this.getEventListenerIterator(eventType);
    }

    public <T extends Event> EventListenerIterator<T> getEventListenerIterator(EventType<T> eventType) {
        return new EventListenerIterator(this.listeners.iterator(), eventType);
    }

    public List<EventListenerRegistrationData<?>> getRegistrations() {
        return Collections.unmodifiableList(this.listeners);
    }

    public <T extends Event> List<EventListenerRegistrationData<? extends T>> getRegistrationsForSuperType(EventType<T> eventType) {
        HashMap superTypes = new HashMap();
        LinkedList results = new LinkedList();
        this.listeners.forEach(reg -> {
            Set base = (Set)superTypes.get(reg.getEventType());
            if (base == null) {
                base = EventType.fetchSuperEventTypes(reg.getEventType());
                superTypes.put(reg.getEventType(), base);
            }
            if (base.contains(eventType)) {
                EventListenerRegistrationData result = reg;
                results.add(result);
            }
        });
        return results;
    }

    public void clear() {
        this.listeners.clear();
    }

    public void addAll(EventListenerList c) {
        if (c == null) {
            throw new IllegalArgumentException("List to be copied must not be null!");
        }
        c.getRegistrations().forEach(this::addEventListener);
    }

    private static void callListener(EventListener<?> listener, Event event) {
        EventListener<?> rowListener = listener;
        rowListener.onEvent(event);
    }

    public static final class EventListenerIterator<T extends Event>
    implements Iterator<EventListener<? super T>> {
        private final Iterator<EventListenerRegistrationData<?>> underlyingIterator;
        private final EventType<T> baseEventType;
        private final Set<EventType<?>> acceptedTypes;
        private EventListener<? super T> nextElement;

        private EventListenerIterator(Iterator<EventListenerRegistrationData<?>> it, EventType<T> base) {
            this.underlyingIterator = it;
            this.baseEventType = base;
            this.acceptedTypes = EventType.fetchSuperEventTypes(base);
            this.initNextElement();
        }

        @Override
        public boolean hasNext() {
            return this.nextElement != null;
        }

        @Override
        public EventListener<? super T> next() {
            if (this.nextElement == null) {
                throw new NoSuchElementException("No more event listeners!");
            }
            EventListener<? super T> result = this.nextElement;
            this.initNextElement();
            return result;
        }

        public void invokeNext(Event event) {
            this.validateEvent(event);
            this.invokeNextListenerUnchecked(event);
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException("Removing elements is not supported!");
        }

        private void initNextElement() {
            this.nextElement = null;
            while (this.underlyingIterator.hasNext() && this.nextElement == null) {
                EventListenerRegistrationData<?> regData = this.underlyingIterator.next();
                if (!this.acceptedTypes.contains(regData.getEventType())) continue;
                this.nextElement = this.castListener(regData);
            }
        }

        private void validateEvent(Event event) {
            if (event == null || !EventType.fetchSuperEventTypes(event.getEventType()).contains(this.baseEventType)) {
                throw new IllegalArgumentException("Event incompatible with listener iteration: " + event);
            }
        }

        private void invokeNextListenerUnchecked(Event event) {
            EventListenerList.callListener((EventListener)this.next(), event);
        }

        private EventListener<? super T> castListener(EventListenerRegistrationData<?> regData) {
            EventListener<?> listener = regData.getListener();
            return listener;
        }
    }
}

