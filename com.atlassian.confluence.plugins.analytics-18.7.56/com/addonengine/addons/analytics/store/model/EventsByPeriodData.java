/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  kotlin.Metadata
 *  kotlin.jvm.internal.Intrinsics
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package com.addonengine.addons.analytics.store.model;

import java.time.Instant;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(mv={1, 9, 0}, k=1, xi=48, d1={"\u0000,\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\t\n\u0002\b\t\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000e\n\u0000\b\u0086\b\u0018\u00002\u00020\u0001B\u0015\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u00a2\u0006\u0002\u0010\u0006J\t\u0010\u000b\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\f\u001a\u00020\u0005H\u00c6\u0003J\u001d\u0010\r\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u0005H\u00c6\u0001J\u0013\u0010\u000e\u001a\u00020\u000f2\b\u0010\u0010\u001a\u0004\u0018\u00010\u0001H\u00d6\u0003J\t\u0010\u0011\u001a\u00020\u0012H\u00d6\u0001J\t\u0010\u0013\u001a\u00020\u0014H\u00d6\u0001R\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0007\u0010\bR\u0011\u0010\u0004\u001a\u00020\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b\t\u0010\n\u00a8\u0006\u0015"}, d2={"Lcom/addonengine/addons/analytics/store/model/EventsByPeriodData;", "", "date", "Ljava/time/Instant;", "total", "", "(Ljava/time/Instant;J)V", "getDate", "()Ljava/time/Instant;", "getTotal", "()J", "component1", "component2", "copy", "equals", "", "other", "hashCode", "", "toString", "", "analytics"})
public final class EventsByPeriodData {
    @NotNull
    private final Instant date;
    private final long total;

    public EventsByPeriodData(@NotNull Instant date, long total) {
        Intrinsics.checkNotNullParameter((Object)date, (String)"date");
        this.date = date;
        this.total = total;
    }

    @NotNull
    public final Instant getDate() {
        return this.date;
    }

    public final long getTotal() {
        return this.total;
    }

    @NotNull
    public final Instant component1() {
        return this.date;
    }

    public final long component2() {
        return this.total;
    }

    @NotNull
    public final EventsByPeriodData copy(@NotNull Instant date, long total) {
        Intrinsics.checkNotNullParameter((Object)date, (String)"date");
        return new EventsByPeriodData(date, total);
    }

    public static /* synthetic */ EventsByPeriodData copy$default(EventsByPeriodData eventsByPeriodData, Instant instant, long l, int n, Object object) {
        if ((n & 1) != 0) {
            instant = eventsByPeriodData.date;
        }
        if ((n & 2) != 0) {
            l = eventsByPeriodData.total;
        }
        return eventsByPeriodData.copy(instant, l);
    }

    @NotNull
    public String toString() {
        return "EventsByPeriodData(date=" + this.date + ", total=" + this.total + ')';
    }

    public int hashCode() {
        int result = this.date.hashCode();
        result = result * 31 + Long.hashCode(this.total);
        return result;
    }

    public boolean equals(@Nullable Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof EventsByPeriodData)) {
            return false;
        }
        EventsByPeriodData eventsByPeriodData = (EventsByPeriodData)other;
        if (!Intrinsics.areEqual((Object)this.date, (Object)eventsByPeriodData.date)) {
            return false;
        }
        return this.total == eventsByPeriodData.total;
    }
}

