/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  kotlin.Metadata
 *  kotlin.jvm.internal.Intrinsics
 *  org.codehaus.jackson.annotate.JsonAutoDetect
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package com.addonengine.addons.analytics.rest.dto;

import com.addonengine.addons.analytics.rest.util.ApiDateTime;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@JsonAutoDetect
@Metadata(mv={1, 9, 0}, k=1, xi=48, d1={"\u0000&\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\b\f\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000e\n\u0000\b\u0087\b\u0018\u00002\u00020\u0001B\u001d\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0003\u0012\u0006\u0010\u0005\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0006J\t\u0010\u000b\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\f\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\r\u001a\u00020\u0003H\u00c6\u0003J'\u0010\u000e\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00032\b\b\u0002\u0010\u0005\u001a\u00020\u0003H\u00c6\u0001J\u0013\u0010\u000f\u001a\u00020\u00102\b\u0010\u0011\u001a\u0004\u0018\u00010\u0001H\u00d6\u0003J\t\u0010\u0012\u001a\u00020\u0013H\u00d6\u0001J\t\u0010\u0014\u001a\u00020\u0015H\u00d6\u0001R\u0011\u0010\u0005\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0007\u0010\bR\u0011\u0010\u0004\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\t\u0010\bR\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\n\u0010\b\u00a8\u0006\u0016"}, d2={"Lcom/addonengine/addons/analytics/rest/dto/SampleDataDetailsDto;", "", "minDate", "Lcom/addonengine/addons/analytics/rest/util/ApiDateTime;", "maxDate", "lastUpdatedAt", "(Lcom/addonengine/addons/analytics/rest/util/ApiDateTime;Lcom/addonengine/addons/analytics/rest/util/ApiDateTime;Lcom/addonengine/addons/analytics/rest/util/ApiDateTime;)V", "getLastUpdatedAt", "()Lcom/addonengine/addons/analytics/rest/util/ApiDateTime;", "getMaxDate", "getMinDate", "component1", "component2", "component3", "copy", "equals", "", "other", "hashCode", "", "toString", "", "analytics"})
public final class SampleDataDetailsDto {
    @NotNull
    private final ApiDateTime minDate;
    @NotNull
    private final ApiDateTime maxDate;
    @NotNull
    private final ApiDateTime lastUpdatedAt;

    public SampleDataDetailsDto(@NotNull ApiDateTime minDate, @NotNull ApiDateTime maxDate, @NotNull ApiDateTime lastUpdatedAt) {
        Intrinsics.checkNotNullParameter((Object)minDate, (String)"minDate");
        Intrinsics.checkNotNullParameter((Object)maxDate, (String)"maxDate");
        Intrinsics.checkNotNullParameter((Object)lastUpdatedAt, (String)"lastUpdatedAt");
        this.minDate = minDate;
        this.maxDate = maxDate;
        this.lastUpdatedAt = lastUpdatedAt;
    }

    @NotNull
    public final ApiDateTime getMinDate() {
        return this.minDate;
    }

    @NotNull
    public final ApiDateTime getMaxDate() {
        return this.maxDate;
    }

    @NotNull
    public final ApiDateTime getLastUpdatedAt() {
        return this.lastUpdatedAt;
    }

    @NotNull
    public final ApiDateTime component1() {
        return this.minDate;
    }

    @NotNull
    public final ApiDateTime component2() {
        return this.maxDate;
    }

    @NotNull
    public final ApiDateTime component3() {
        return this.lastUpdatedAt;
    }

    @NotNull
    public final SampleDataDetailsDto copy(@NotNull ApiDateTime minDate, @NotNull ApiDateTime maxDate, @NotNull ApiDateTime lastUpdatedAt) {
        Intrinsics.checkNotNullParameter((Object)minDate, (String)"minDate");
        Intrinsics.checkNotNullParameter((Object)maxDate, (String)"maxDate");
        Intrinsics.checkNotNullParameter((Object)lastUpdatedAt, (String)"lastUpdatedAt");
        return new SampleDataDetailsDto(minDate, maxDate, lastUpdatedAt);
    }

    public static /* synthetic */ SampleDataDetailsDto copy$default(SampleDataDetailsDto sampleDataDetailsDto, ApiDateTime apiDateTime, ApiDateTime apiDateTime2, ApiDateTime apiDateTime3, int n, Object object) {
        if ((n & 1) != 0) {
            apiDateTime = sampleDataDetailsDto.minDate;
        }
        if ((n & 2) != 0) {
            apiDateTime2 = sampleDataDetailsDto.maxDate;
        }
        if ((n & 4) != 0) {
            apiDateTime3 = sampleDataDetailsDto.lastUpdatedAt;
        }
        return sampleDataDetailsDto.copy(apiDateTime, apiDateTime2, apiDateTime3);
    }

    @NotNull
    public String toString() {
        return "SampleDataDetailsDto(minDate=" + this.minDate + ", maxDate=" + this.maxDate + ", lastUpdatedAt=" + this.lastUpdatedAt + ')';
    }

    public int hashCode() {
        int result = this.minDate.hashCode();
        result = result * 31 + this.maxDate.hashCode();
        result = result * 31 + this.lastUpdatedAt.hashCode();
        return result;
    }

    public boolean equals(@Nullable Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof SampleDataDetailsDto)) {
            return false;
        }
        SampleDataDetailsDto sampleDataDetailsDto = (SampleDataDetailsDto)other;
        if (!Intrinsics.areEqual((Object)this.minDate, (Object)sampleDataDetailsDto.minDate)) {
            return false;
        }
        if (!Intrinsics.areEqual((Object)this.maxDate, (Object)sampleDataDetailsDto.maxDate)) {
            return false;
        }
        return Intrinsics.areEqual((Object)this.lastUpdatedAt, (Object)sampleDataDetailsDto.lastUpdatedAt);
    }
}

