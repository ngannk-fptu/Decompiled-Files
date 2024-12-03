/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  kotlin.Metadata
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package com.addonengine.addons.analytics.store.server;

import kotlin.Metadata;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(mv={1, 9, 0}, k=1, xi=48, d1={"\u0000&\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\t\n\u0002\b\t\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000e\n\u0000\b\u0086\b\u0018\u00002\u00020\u0001B\u0015\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0005J\t\u0010\t\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\n\u001a\u00020\u0003H\u00c6\u0003J\u001d\u0010\u000b\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u0003H\u00c6\u0001J\u0013\u0010\f\u001a\u00020\r2\b\u0010\u000e\u001a\u0004\u0018\u00010\u0001H\u00d6\u0003J\t\u0010\u000f\u001a\u00020\u0010H\u00d6\u0001J\t\u0010\u0011\u001a\u00020\u0012H\u00d6\u0001R\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0006\u0010\u0007R\u0011\u0010\u0004\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\b\u0010\u0007\u00a8\u0006\u0013"}, d2={"Lcom/addonengine/addons/analytics/store/server/TimedEvent;", "", "id", "", "timestamp", "(JJ)V", "getId", "()J", "getTimestamp", "component1", "component2", "copy", "equals", "", "other", "hashCode", "", "toString", "", "analytics"})
public final class TimedEvent {
    private final long id;
    private final long timestamp;

    public TimedEvent(long id, long timestamp) {
        this.id = id;
        this.timestamp = timestamp;
    }

    public final long getId() {
        return this.id;
    }

    public final long getTimestamp() {
        return this.timestamp;
    }

    public final long component1() {
        return this.id;
    }

    public final long component2() {
        return this.timestamp;
    }

    @NotNull
    public final TimedEvent copy(long id, long timestamp) {
        return new TimedEvent(id, timestamp);
    }

    public static /* synthetic */ TimedEvent copy$default(TimedEvent timedEvent, long l, long l2, int n, Object object) {
        if ((n & 1) != 0) {
            l = timedEvent.id;
        }
        if ((n & 2) != 0) {
            l2 = timedEvent.timestamp;
        }
        return timedEvent.copy(l, l2);
    }

    @NotNull
    public String toString() {
        return "TimedEvent(id=" + this.id + ", timestamp=" + this.timestamp + ')';
    }

    public int hashCode() {
        int result = Long.hashCode(this.id);
        result = result * 31 + Long.hashCode(this.timestamp);
        return result;
    }

    public boolean equals(@Nullable Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof TimedEvent)) {
            return false;
        }
        TimedEvent timedEvent = (TimedEvent)other;
        if (this.id != timedEvent.id) {
            return false;
        }
        return this.timestamp == timedEvent.timestamp;
    }
}

