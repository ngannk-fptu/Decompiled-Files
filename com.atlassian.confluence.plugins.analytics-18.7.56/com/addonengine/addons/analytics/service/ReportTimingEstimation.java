/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  kotlin.Metadata
 *  org.codehaus.jackson.annotate.JsonAutoDetect
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package com.addonengine.addons.analytics.service;

import kotlin.Metadata;
import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@JsonAutoDetect
@Metadata(mv={1, 9, 0}, k=1, xi=48, d1={"\u0000&\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\t\n\u0002\b\u0003\n\u0002\u0010\u000b\n\u0002\b\u000b\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000e\n\u0000\b\u0087\b\u0018\u00002\u00020\u0001B\u0017\b\u0016\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0005B\u0015\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0006\u001a\u00020\u0007\u00a2\u0006\u0002\u0010\bJ\t\u0010\r\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\u000e\u001a\u00020\u0007H\u00c6\u0003J\u001d\u0010\u000f\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0006\u001a\u00020\u0007H\u00c6\u0001J\u0013\u0010\u0010\u001a\u00020\u00072\b\u0010\u0011\u001a\u0004\u0018\u00010\u0001H\u00d6\u0003J\t\u0010\u0012\u001a\u00020\u0013H\u00d6\u0001J\t\u0010\u0014\u001a\u00020\u0015H\u00d6\u0001R\u0011\u0010\u0006\u001a\u00020\u0007\u00a2\u0006\b\n\u0000\u001a\u0004\b\t\u0010\nR\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000b\u0010\f\u00a8\u0006\u0016"}, d2={"Lcom/addonengine/addons/analytics/service/ReportTimingEstimation;", "", "estimatedEvents", "", "slownessThreshold", "(JJ)V", "canBeSlow", "", "(JZ)V", "getCanBeSlow", "()Z", "getEstimatedEvents", "()J", "component1", "component2", "copy", "equals", "other", "hashCode", "", "toString", "", "analytics"})
public final class ReportTimingEstimation {
    private final long estimatedEvents;
    private final boolean canBeSlow;

    public ReportTimingEstimation(long estimatedEvents, boolean canBeSlow) {
        this.estimatedEvents = estimatedEvents;
        this.canBeSlow = canBeSlow;
    }

    public final long getEstimatedEvents() {
        return this.estimatedEvents;
    }

    public final boolean getCanBeSlow() {
        return this.canBeSlow;
    }

    public ReportTimingEstimation(long estimatedEvents, long slownessThreshold) {
        this(estimatedEvents, estimatedEvents >= slownessThreshold);
    }

    public final long component1() {
        return this.estimatedEvents;
    }

    public final boolean component2() {
        return this.canBeSlow;
    }

    @NotNull
    public final ReportTimingEstimation copy(long estimatedEvents, boolean canBeSlow) {
        return new ReportTimingEstimation(estimatedEvents, canBeSlow);
    }

    public static /* synthetic */ ReportTimingEstimation copy$default(ReportTimingEstimation reportTimingEstimation, long l, boolean bl, int n, Object object) {
        if ((n & 1) != 0) {
            l = reportTimingEstimation.estimatedEvents;
        }
        if ((n & 2) != 0) {
            bl = reportTimingEstimation.canBeSlow;
        }
        return reportTimingEstimation.copy(l, bl);
    }

    @NotNull
    public String toString() {
        return "ReportTimingEstimation(estimatedEvents=" + this.estimatedEvents + ", canBeSlow=" + this.canBeSlow + ')';
    }

    public int hashCode() {
        int result = Long.hashCode(this.estimatedEvents);
        int n = this.canBeSlow ? 1 : 0;
        if (n != 0) {
            n = 1;
        }
        result = result * 31 + n;
        return result;
    }

    public boolean equals(@Nullable Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof ReportTimingEstimation)) {
            return false;
        }
        ReportTimingEstimation reportTimingEstimation = (ReportTimingEstimation)other;
        if (this.estimatedEvents != reportTimingEstimation.estimatedEvents) {
            return false;
        }
        return this.canBeSlow == reportTimingEstimation.canBeSlow;
    }
}

