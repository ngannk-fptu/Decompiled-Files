/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  kotlin.Metadata
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package com.addonengine.addons.analytics.store.model;

import kotlin.Metadata;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(mv={1, 9, 0}, k=1, xi=48, d1={"\u0000&\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\t\n\u0002\b\u0006\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000e\n\u0000\b\u0086\b\u0018\u00002\u00020\u0001B\r\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004J\t\u0010\u0007\u001a\u00020\u0003H\u00c6\u0003J\u0013\u0010\b\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u0003H\u00c6\u0001J\u0013\u0010\t\u001a\u00020\n2\b\u0010\u000b\u001a\u0004\u0018\u00010\u0001H\u00d6\u0003J\t\u0010\f\u001a\u00020\rH\u00d6\u0001J\t\u0010\u000e\u001a\u00020\u000fH\u00d6\u0001R\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0005\u0010\u0006\u00a8\u0006\u0010"}, d2={"Lcom/addonengine/addons/analytics/store/model/EventLimitSettingsData;", "", "maxRowCount", "", "(J)V", "getMaxRowCount", "()J", "component1", "copy", "equals", "", "other", "hashCode", "", "toString", "", "analytics"})
public final class EventLimitSettingsData {
    private final long maxRowCount;

    public EventLimitSettingsData(long maxRowCount) {
        this.maxRowCount = maxRowCount;
    }

    public final long getMaxRowCount() {
        return this.maxRowCount;
    }

    public final long component1() {
        return this.maxRowCount;
    }

    @NotNull
    public final EventLimitSettingsData copy(long maxRowCount) {
        return new EventLimitSettingsData(maxRowCount);
    }

    public static /* synthetic */ EventLimitSettingsData copy$default(EventLimitSettingsData eventLimitSettingsData, long l, int n, Object object) {
        if ((n & 1) != 0) {
            l = eventLimitSettingsData.maxRowCount;
        }
        return eventLimitSettingsData.copy(l);
    }

    @NotNull
    public String toString() {
        return "EventLimitSettingsData(maxRowCount=" + this.maxRowCount + ')';
    }

    public int hashCode() {
        return Long.hashCode(this.maxRowCount);
    }

    public boolean equals(@Nullable Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof EventLimitSettingsData)) {
            return false;
        }
        EventLimitSettingsData eventLimitSettingsData = (EventLimitSettingsData)other;
        return this.maxRowCount == eventLimitSettingsData.maxRowCount;
    }
}

