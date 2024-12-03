/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  kotlin.Metadata
 *  org.codehaus.jackson.annotate.JsonAutoDetect
 *  org.codehaus.jackson.annotate.JsonProperty
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package com.addonengine.addons.analytics.store.server.settings.model;

import kotlin.Metadata;
import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.annotate.JsonProperty;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@JsonAutoDetect
@Metadata(mv={1, 9, 0}, k=1, xi=48, d1={"\u0000&\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\t\n\u0002\b\f\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000e\n\u0000\b\u0087\b\u0018\u00002\u00020\u0001B#\u0012\b\b\u0001\u0010\u0002\u001a\u00020\u0003\u0012\b\b\u0001\u0010\u0004\u001a\u00020\u0003\u0012\b\b\u0001\u0010\u0005\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0006J\t\u0010\u000b\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\f\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\r\u001a\u00020\u0003H\u00c6\u0003J'\u0010\u000e\u001a\u00020\u00002\b\b\u0003\u0010\u0002\u001a\u00020\u00032\b\b\u0003\u0010\u0004\u001a\u00020\u00032\b\b\u0003\u0010\u0005\u001a\u00020\u0003H\u00c6\u0001J\u0013\u0010\u000f\u001a\u00020\u00102\b\u0010\u0011\u001a\u0004\u0018\u00010\u0001H\u00d6\u0003J\t\u0010\u0012\u001a\u00020\u0013H\u00d6\u0001J\t\u0010\u0014\u001a\u00020\u0015H\u00d6\u0001R\u0011\u0010\u0005\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0007\u0010\bR\u0011\u0010\u0004\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\t\u0010\bR\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\n\u0010\b\u00a8\u0006\u0016"}, d2={"Lcom/addonengine/addons/analytics/store/server/settings/model/SampleDataMetadataSetting;", "", "minDate", "", "maxDate", "lastUpdatedAt", "(JJJ)V", "getLastUpdatedAt", "()J", "getMaxDate", "getMinDate", "component1", "component2", "component3", "copy", "equals", "", "other", "hashCode", "", "toString", "", "analytics"})
public final class SampleDataMetadataSetting {
    private final long minDate;
    private final long maxDate;
    private final long lastUpdatedAt;

    public SampleDataMetadataSetting(@JsonProperty(value="minDate") long minDate, @JsonProperty(value="maxDate") long maxDate, @JsonProperty(value="lastUpdatedAt") long lastUpdatedAt) {
        this.minDate = minDate;
        this.maxDate = maxDate;
        this.lastUpdatedAt = lastUpdatedAt;
    }

    public final long getMinDate() {
        return this.minDate;
    }

    public final long getMaxDate() {
        return this.maxDate;
    }

    public final long getLastUpdatedAt() {
        return this.lastUpdatedAt;
    }

    public final long component1() {
        return this.minDate;
    }

    public final long component2() {
        return this.maxDate;
    }

    public final long component3() {
        return this.lastUpdatedAt;
    }

    @NotNull
    public final SampleDataMetadataSetting copy(@JsonProperty(value="minDate") long minDate, @JsonProperty(value="maxDate") long maxDate, @JsonProperty(value="lastUpdatedAt") long lastUpdatedAt) {
        return new SampleDataMetadataSetting(minDate, maxDate, lastUpdatedAt);
    }

    public static /* synthetic */ SampleDataMetadataSetting copy$default(SampleDataMetadataSetting sampleDataMetadataSetting, long l, long l2, long l3, int n, Object object) {
        if ((n & 1) != 0) {
            l = sampleDataMetadataSetting.minDate;
        }
        if ((n & 2) != 0) {
            l2 = sampleDataMetadataSetting.maxDate;
        }
        if ((n & 4) != 0) {
            l3 = sampleDataMetadataSetting.lastUpdatedAt;
        }
        return sampleDataMetadataSetting.copy(l, l2, l3);
    }

    @NotNull
    public String toString() {
        return "SampleDataMetadataSetting(minDate=" + this.minDate + ", maxDate=" + this.maxDate + ", lastUpdatedAt=" + this.lastUpdatedAt + ')';
    }

    public int hashCode() {
        int result = Long.hashCode(this.minDate);
        result = result * 31 + Long.hashCode(this.maxDate);
        result = result * 31 + Long.hashCode(this.lastUpdatedAt);
        return result;
    }

    public boolean equals(@Nullable Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof SampleDataMetadataSetting)) {
            return false;
        }
        SampleDataMetadataSetting sampleDataMetadataSetting = (SampleDataMetadataSetting)other;
        if (this.minDate != sampleDataMetadataSetting.minDate) {
            return false;
        }
        if (this.maxDate != sampleDataMetadataSetting.maxDate) {
            return false;
        }
        return this.lastUpdatedAt == sampleDataMetadataSetting.lastUpdatedAt;
    }
}

