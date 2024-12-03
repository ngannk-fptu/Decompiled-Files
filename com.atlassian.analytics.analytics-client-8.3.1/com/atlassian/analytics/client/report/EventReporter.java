/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableSet
 */
package com.atlassian.analytics.client.report;

import com.atlassian.analytics.client.report.EventReportItem;
import com.atlassian.analytics.client.report.EventReporterStore;
import com.atlassian.analytics.client.report.TimeoutChecker;
import com.atlassian.analytics.event.AnalyticsEvent;
import com.atlassian.analytics.event.ProcessedEvent;
import com.atlassian.analytics.event.RawEvent;
import com.google.common.collect.ImmutableSet;
import java.util.Collection;
import java.util.Optional;
import java.util.Set;

public class EventReporter {
    private static final int CAPACITY = 1000;
    private static final Set<String> IGNORED_EVENTS = ImmutableSet.of((Object)"reindexissuesstarted", (Object)"reindexissuescompleted");
    private final TimeoutChecker timeoutChecker;
    private final EventReporterStore rawStore;
    private final EventReporterStore btfProcessedStore;
    private boolean capturing;

    public EventReporter(TimeoutChecker timeoutChecker) {
        this.timeoutChecker = timeoutChecker;
        this.rawStore = new EventReporterStore(1000, IGNORED_EVENTS);
        this.btfProcessedStore = new EventReporterStore(1000, IGNORED_EVENTS);
        this.capturing = false;
    }

    public void addEvent(RawEvent rawEvent, Optional<ProcessedEvent> processedEvent) {
        this.addRawEvent(rawEvent, this.rawStore, false);
        this.addProcessedEvent(rawEvent, processedEvent);
    }

    private void addProcessedEvent(RawEvent rawEvent, Optional<ProcessedEvent> processedEvent) {
        EventReporterStore processedStore = this.btfProcessedStore;
        if (processedEvent.isPresent()) {
            this.addProcessedEvent(processedEvent.get(), processedStore);
        } else {
            this.addRejectedRawEvent(rawEvent, processedStore);
        }
    }

    public void addProcessedEvent(ProcessedEvent event, EventReporterStore processedStore) {
        this.addRawEvent(event, processedStore, false);
    }

    public void addRejectedRawEvent(RawEvent event, EventReporterStore processedStore) {
        this.addRawEvent(event, processedStore, true);
    }

    private void addRawEvent(AnalyticsEvent event, EventReporterStore store, boolean removed) {
        if (this.capturing) {
            if (this.timeoutChecker.isTimeoutExceeded()) {
                this.setCapturing(false);
            } else {
                store.add(event, removed);
            }
        }
    }

    public Collection<EventReportItem> getRawEvents() {
        this.timeoutChecker.actionHasOccurred();
        return this.rawStore.getEvents();
    }

    public Collection<EventReportItem> getBtfProcessedEvents() {
        this.timeoutChecker.actionHasOccurred();
        return this.btfProcessedStore.getEvents();
    }

    public void clear() {
        this.timeoutChecker.actionHasOccurred();
        this.rawStore.clear();
        this.btfProcessedStore.clear();
    }

    public boolean isCapturing() {
        return this.capturing;
    }

    public void setCapturing(boolean capturing) {
        this.timeoutChecker.actionHasOccurred();
        this.capturing = capturing;
    }
}

