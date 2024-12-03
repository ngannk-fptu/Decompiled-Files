/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.embedded.api.Directory
 *  com.atlassian.crowd.event.EventStore
 *  com.atlassian.crowd.event.EventTokenExpiredException
 *  com.atlassian.crowd.event.Events
 *  com.atlassian.crowd.model.application.Application
 *  com.atlassian.crowd.model.application.Applications
 *  com.atlassian.crowd.model.event.Operation
 *  com.atlassian.crowd.model.event.OperationEvent
 */
package com.atlassian.crowd.event;

import com.atlassian.crowd.embedded.api.Directory;
import com.atlassian.crowd.event.EventStore;
import com.atlassian.crowd.event.EventTokenExpiredException;
import com.atlassian.crowd.event.Events;
import com.atlassian.crowd.model.application.Application;
import com.atlassian.crowd.model.application.Applications;
import com.atlassian.crowd.model.event.Operation;
import com.atlassian.crowd.model.event.OperationEvent;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentNavigableMap;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.stream.Collectors;

public final class EventStoreGeneric
implements EventStore {
    private static final String SEPARATOR = ":";
    private static final Random random = new Random();
    private final long maxStoredEvents;
    private final ConcurrentNavigableMap<Long, OperationEvent> events = new ConcurrentSkipListMap<Long, OperationEvent>();
    private final String instanceId = String.valueOf(random.nextLong());
    private long eventNumber = 0L;

    public EventStoreGeneric(long maxStoredEvents) {
        this.maxStoredEvents = maxStoredEvents + 1L;
        this.storeOperationEvent(new ResetEvent());
    }

    public String getCurrentEventToken(List<Long> directoryIds) {
        return this.toEventToken((Long)this.events.lastKey());
    }

    public Events getNewEvents(String eventToken, List<Long> directoryIds) throws EventTokenExpiredException {
        Long currentEventNumber = this.toEventNumber(eventToken);
        Iterator eventsSince = this.events.tailMap((Object)currentEventNumber).entrySet().iterator();
        if (!eventsSince.hasNext() || !((Long)eventsSince.next().getKey()).equals(currentEventNumber)) {
            throw new EventTokenExpiredException();
        }
        ArrayList<OperationEvent> events = new ArrayList<OperationEvent>();
        Long newEventNumber = currentEventNumber;
        while (eventsSince.hasNext()) {
            Map.Entry eventEntry = eventsSince.next();
            OperationEvent event = (OperationEvent)eventEntry.getValue();
            if (event instanceof ResetEvent) {
                throw new EventTokenExpiredException(((ResetEvent)event).getResetReason());
            }
            newEventNumber = (Long)eventEntry.getKey();
            if (event.getDirectoryId() != null && !directoryIds.contains(event.getDirectoryId())) continue;
            events.add(event);
        }
        return new Events(events, this.toEventToken(newEventNumber));
    }

    public Events getNewEvents(String eventToken, Application application) throws EventTokenExpiredException {
        return this.getNewEvents(eventToken, Applications.getActiveDirectories((Application)application).stream().map(Directory::getId).collect(Collectors.toList()));
    }

    public synchronized void storeOperationEvent(OperationEvent event) {
        long currentEventNumber;
        if ((currentEventNumber = ++this.eventNumber) > this.maxStoredEvents) {
            this.events.remove(this.events.firstKey());
        }
        this.events.put(currentEventNumber, event);
    }

    public void handleApplicationEvent(Object event) {
        this.storeOperationEvent(ResetEvent.fromUnsupportedEvent(event.getClass()));
    }

    private Long toEventNumber(String eventToken) throws EventTokenExpiredException {
        String[] parts = eventToken.split(SEPARATOR);
        if (parts.length != 2 || !parts[0].equals(this.instanceId)) {
            throw new EventTokenExpiredException();
        }
        return Long.valueOf(parts[1]);
    }

    private String toEventToken(long eventNumber) {
        return this.instanceId + SEPARATOR + eventNumber;
    }

    private static class ResetEvent
    implements OperationEvent {
        private final String resetReason;

        public ResetEvent() {
            this.resetReason = null;
        }

        public ResetEvent(String resetReason) {
            this.resetReason = resetReason;
        }

        public static ResetEvent fromUnsupportedEvent(Class unsupportedEvent) {
            return ResetEvent.withReason(unsupportedEvent.getName() + " is not supported by incremental sync.");
        }

        public static ResetEvent withReason(String reason) {
            return new ResetEvent(reason);
        }

        public String getResetReason() {
            return this.resetReason;
        }

        public Operation getOperation() {
            return null;
        }

        public Long getDirectoryId() {
            return null;
        }
    }
}

