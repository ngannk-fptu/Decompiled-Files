/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.diagnostics.detail.ThreadDump
 *  com.google.common.base.MoreObjects
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableList$Builder
 *  javax.annotation.Nonnull
 *  org.codehaus.jackson.annotate.JsonCreator
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.diagnostics.internal.platform.monitor.event;

import com.atlassian.diagnostics.detail.ThreadDump;
import com.google.common.base.MoreObjects;
import com.google.common.collect.ImmutableList;
import java.util.List;
import java.util.Objects;
import javax.annotation.Nonnull;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

public class EventDroppedDetails {
    private final String eventType;
    private final int queueLength;
    private final List<ThreadDump> threadDumps;

    private EventDroppedDetails(Builder builder) {
        this.eventType = builder.eventType;
        this.queueLength = builder.queueLength;
        this.threadDumps = builder.threadDumps.build();
    }

    @JsonCreator
    static EventDroppedDetails create(@JsonProperty(value="eventType") String className, @JsonProperty(value="queueLength") int queueLength, @JsonProperty(value="threads") List<ThreadDump> threadDumps) {
        Builder builder = new Builder(className, queueLength);
        if (threadDumps != null) {
            builder.threadDumps(threadDumps);
        }
        return builder.build();
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        EventDroppedDetails details = (EventDroppedDetails)o;
        return this.queueLength == details.queueLength && Objects.equals(this.eventType, details.eventType) && Objects.equals(this.threadDumps, details.threadDumps);
    }

    @Nonnull
    public String getEventType() {
        return this.eventType;
    }

    public int getQueueLength() {
        return this.queueLength;
    }

    @Nonnull
    public List<ThreadDump> getThreadDumps() {
        return this.threadDumps;
    }

    public int hashCode() {
        return Objects.hash(this.eventType, this.queueLength, this.threadDumps);
    }

    public String toString() {
        return MoreObjects.toStringHelper((Object)this).add("eventType", (Object)this.eventType).add("queueLength", this.queueLength).add("threadDumps", this.threadDumps).toString();
    }

    public static class Builder {
        private final String eventType;
        private final int queueLength;
        private final ImmutableList.Builder<ThreadDump> threadDumps;

        public Builder(String eventClass, int queueLength) {
            this.eventType = Objects.requireNonNull(eventClass, "eventType");
            this.queueLength = queueLength;
            this.threadDumps = ImmutableList.builder();
        }

        @Nonnull
        public EventDroppedDetails build() {
            return new EventDroppedDetails(this);
        }

        @Nonnull
        public Builder threadDumps(@Nonnull Iterable<ThreadDump> values) {
            this.threadDumps.addAll(Objects.requireNonNull(values, "threadDumps"));
            return this;
        }
    }
}

