/*
 * Decompiled with CFR 0.152.
 */
package com.microsoft.aad.msal4j;

import com.microsoft.aad.msal4j.ApiEvent;
import com.microsoft.aad.msal4j.DefaultEvent;
import com.microsoft.aad.msal4j.Event;
import com.microsoft.aad.msal4j.EventKey;
import com.microsoft.aad.msal4j.ITelemetry;
import com.microsoft.aad.msal4j.ITelemetryManager;
import com.microsoft.aad.msal4j.StringHelper;
import com.microsoft.aad.msal4j.TelemetryHelper;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.function.Predicate;

class TelemetryManager
implements ITelemetryManager,
ITelemetry {
    private final ConcurrentHashMap<String, List<Event>> completedEvents = new ConcurrentHashMap();
    private final ConcurrentHashMap<EventKey, Event> eventsInProgress = new ConcurrentHashMap();
    private final ConcurrentHashMap<String, ConcurrentHashMap<String, Integer>> eventCount = new ConcurrentHashMap();
    private boolean onlySendFailureTelemetry;
    private Consumer<List<HashMap<String, String>>> telemetryConsumer;

    public TelemetryManager(Consumer<List<HashMap<String, String>>> telemetryConsumer, boolean onlySendFailureTelemetry) {
        this.telemetryConsumer = telemetryConsumer;
        this.onlySendFailureTelemetry = onlySendFailureTelemetry;
    }

    @Override
    public TelemetryHelper createTelemetryHelper(String requestId, String clientId, Event eventToStart, Boolean shouldFlush) {
        return new TelemetryHelper(this, requestId, clientId, eventToStart, shouldFlush);
    }

    @Override
    public String generateRequestId() {
        return UUID.randomUUID().toString();
    }

    @Override
    public void startEvent(String requestId, Event eventToStart) {
        if (this.hasConsumer() && !StringHelper.isBlank(requestId)) {
            this.eventsInProgress.put(new EventKey(requestId, eventToStart), eventToStart);
        }
    }

    @Override
    public void stopEvent(String requestId, Event eventToStop) {
        if (!this.hasConsumer() || StringHelper.isBlank(requestId)) {
            return;
        }
        EventKey eventKey = new EventKey(requestId, eventToStop);
        Event eventStarted = this.eventsInProgress.getOrDefault(eventKey, null);
        if (eventStarted == null) {
            return;
        }
        eventToStop.stop();
        this.incrementEventCount(requestId, eventToStop);
        if (!this.completedEvents.containsKey(requestId)) {
            ArrayList<Event> eventList = new ArrayList<Event>(Arrays.asList(eventToStop));
            this.completedEvents.put(requestId, eventList);
        } else {
            List<Event> eventList = this.completedEvents.get(requestId);
            eventList.add(eventToStop);
        }
        this.eventsInProgress.remove(eventKey);
    }

    @Override
    public void flush(String requestId, String clientId) {
        if (!this.hasConsumer()) {
            return;
        }
        if (!this.completedEvents.containsKey(requestId)) {
            return;
        }
        this.completedEvents.get(requestId).addAll(this.collateOrphanedEvents(requestId));
        List<Event> eventsToFlush = this.completedEvents.remove(requestId);
        Map eventCountToFlush = this.eventCount.remove(requestId);
        eventCountToFlush = eventCountToFlush != null ? eventCountToFlush : new ConcurrentHashMap();
        Predicate<Event> isSuccessfulPredicate = event -> event instanceof ApiEvent && ((ApiEvent)event).getWasSuccessful();
        if (this.onlySendFailureTelemetry && eventsToFlush.stream().anyMatch(isSuccessfulPredicate)) {
            eventsToFlush.clear();
        }
        if (eventsToFlush.size() <= 0) {
            return;
        }
        eventsToFlush.add(0, new DefaultEvent(clientId, eventCountToFlush));
        this.telemetryConsumer.accept(Collections.unmodifiableList(eventsToFlush));
    }

    private Collection<Event> collateOrphanedEvents(String requestId) {
        ArrayList<Event> orphanedEvents = new ArrayList<Event>();
        for (EventKey key : this.eventsInProgress.keySet()) {
            if (!key.getRequestId().equalsIgnoreCase(requestId)) continue;
            orphanedEvents.add(this.eventsInProgress.remove(key));
        }
        return orphanedEvents;
    }

    private void incrementEventCount(String requestId, Event eventToIncrement) {
        final String eventName = (String)eventToIncrement.get("event_name");
        ConcurrentHashMap<String, Integer> eventNameCount = this.eventCount.getOrDefault(requestId, new ConcurrentHashMap<String, Integer>(){
            {
                this.put(eventName, 0);
            }
        });
        eventNameCount.put(eventName, eventNameCount.getOrDefault(eventName, 0) + 1);
        this.eventCount.put(requestId, eventNameCount);
    }

    private boolean hasConsumer() {
        return this.telemetryConsumer != null;
    }
}

