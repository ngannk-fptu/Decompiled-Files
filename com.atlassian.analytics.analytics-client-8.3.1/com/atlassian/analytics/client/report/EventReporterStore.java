/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.analytics.client.report;

import com.atlassian.analytics.client.report.EventReportItem;
import com.atlassian.analytics.event.AnalyticsEvent;
import java.util.Collection;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;

public class EventReporterStore {
    private final int capacity;
    private final Set<String> ignoredEvents;
    private final Queue<EventReportItem> events;
    private final AtomicInteger count;

    public EventReporterStore(int capacity, Set<String> ignoredEvents) {
        this.capacity = capacity;
        this.ignoredEvents = ignoredEvents;
        this.events = new ConcurrentLinkedQueue<EventReportItem>();
        this.count = new AtomicInteger(0);
    }

    public void add(AnalyticsEvent message) {
        this.add(message, false);
    }

    public void add(AnalyticsEvent event, boolean removed) {
        if (!this.ignoredEvents.contains(event.getName())) {
            this.ensureCapacityLimit();
            this.events.add(new EventReportItem(event.getName(), event.getReceivedTime(), event.getUser(), event.getRequestCorrelationId(), event.getProperties(), removed));
        }
    }

    public Collection<EventReportItem> getEvents() {
        return this.events;
    }

    public void clear() {
        this.events.clear();
        this.count.set(0);
    }

    private void ensureCapacityLimit() {
        this.count.incrementAndGet();
        if (this.count.intValue() > this.capacity) {
            this.count.decrementAndGet();
            this.events.poll();
        }
    }
}

