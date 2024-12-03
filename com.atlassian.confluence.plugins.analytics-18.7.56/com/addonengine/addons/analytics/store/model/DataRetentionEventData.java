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

@Metadata(mv={1, 9, 0}, k=1, xi=48, d1={"\u0000&\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\t\n\u0002\b\t\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000e\n\u0000\b\u0086\b\u0018\u00002\u00020\u0001B\u0015\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0005J\t\u0010\t\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\n\u001a\u00020\u0003H\u00c6\u0003J\u001d\u0010\u000b\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u0003H\u00c6\u0001J\u0013\u0010\f\u001a\u00020\r2\b\u0010\u000e\u001a\u0004\u0018\u00010\u0001H\u00d6\u0003J\t\u0010\u000f\u001a\u00020\u0010H\u00d6\u0001J\t\u0010\u0011\u001a\u00020\u0012H\u00d6\u0001R\u0011\u0010\u0004\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0006\u0010\u0007R\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\b\u0010\u0007\u00a8\u0006\u0013"}, d2={"Lcom/addonengine/addons/analytics/store/model/DataRetentionEventData;", "", "id", "", "eventAt", "(JJ)V", "getEventAt", "()J", "getId", "component1", "component2", "copy", "equals", "", "other", "hashCode", "", "toString", "", "analytics"})
public final class DataRetentionEventData {
    private final long id;
    private final long eventAt;

    public DataRetentionEventData(long id, long eventAt) {
        this.id = id;
        this.eventAt = eventAt;
    }

    public final long getId() {
        return this.id;
    }

    public final long getEventAt() {
        return this.eventAt;
    }

    public final long component1() {
        return this.id;
    }

    public final long component2() {
        return this.eventAt;
    }

    @NotNull
    public final DataRetentionEventData copy(long id, long eventAt) {
        return new DataRetentionEventData(id, eventAt);
    }

    public static /* synthetic */ DataRetentionEventData copy$default(DataRetentionEventData dataRetentionEventData, long l, long l2, int n, Object object) {
        if ((n & 1) != 0) {
            l = dataRetentionEventData.id;
        }
        if ((n & 2) != 0) {
            l2 = dataRetentionEventData.eventAt;
        }
        return dataRetentionEventData.copy(l, l2);
    }

    @NotNull
    public String toString() {
        return "DataRetentionEventData(id=" + this.id + ", eventAt=" + this.eventAt + ')';
    }

    public int hashCode() {
        int result = Long.hashCode(this.id);
        result = result * 31 + Long.hashCode(this.eventAt);
        return result;
    }

    public boolean equals(@Nullable Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof DataRetentionEventData)) {
            return false;
        }
        DataRetentionEventData dataRetentionEventData = (DataRetentionEventData)other;
        if (this.id != dataRetentionEventData.id) {
            return false;
        }
        return this.eventAt == dataRetentionEventData.eventAt;
    }
}

