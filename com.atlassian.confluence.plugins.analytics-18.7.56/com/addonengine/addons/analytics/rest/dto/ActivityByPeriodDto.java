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

import com.addonengine.addons.analytics.rest.dto.ActivityByPeriodEventsDto;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@JsonAutoDetect
@Metadata(mv={1, 9, 0}, k=1, xi=48, d1={"\u0000&\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0006\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000e\n\u0000\b\u0087\b\u0018\u00002\u00020\u0001B\r\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004J\t\u0010\u0007\u001a\u00020\u0003H\u00c6\u0003J\u0013\u0010\b\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u0003H\u00c6\u0001J\u0013\u0010\t\u001a\u00020\n2\b\u0010\u000b\u001a\u0004\u0018\u00010\u0001H\u00d6\u0003J\t\u0010\f\u001a\u00020\rH\u00d6\u0001J\t\u0010\u000e\u001a\u00020\u000fH\u00d6\u0001R\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0005\u0010\u0006\u00a8\u0006\u0010"}, d2={"Lcom/addonengine/addons/analytics/rest/dto/ActivityByPeriodDto;", "", "activityByDate", "Lcom/addonengine/addons/analytics/rest/dto/ActivityByPeriodEventsDto;", "(Lcom/addonengine/addons/analytics/rest/dto/ActivityByPeriodEventsDto;)V", "getActivityByDate", "()Lcom/addonengine/addons/analytics/rest/dto/ActivityByPeriodEventsDto;", "component1", "copy", "equals", "", "other", "hashCode", "", "toString", "", "analytics"})
public final class ActivityByPeriodDto {
    @NotNull
    private final ActivityByPeriodEventsDto activityByDate;

    public ActivityByPeriodDto(@NotNull ActivityByPeriodEventsDto activityByDate) {
        Intrinsics.checkNotNullParameter((Object)activityByDate, (String)"activityByDate");
        this.activityByDate = activityByDate;
    }

    @NotNull
    public final ActivityByPeriodEventsDto getActivityByDate() {
        return this.activityByDate;
    }

    @NotNull
    public final ActivityByPeriodEventsDto component1() {
        return this.activityByDate;
    }

    @NotNull
    public final ActivityByPeriodDto copy(@NotNull ActivityByPeriodEventsDto activityByDate) {
        Intrinsics.checkNotNullParameter((Object)activityByDate, (String)"activityByDate");
        return new ActivityByPeriodDto(activityByDate);
    }

    public static /* synthetic */ ActivityByPeriodDto copy$default(ActivityByPeriodDto activityByPeriodDto, ActivityByPeriodEventsDto activityByPeriodEventsDto, int n, Object object) {
        if ((n & 1) != 0) {
            activityByPeriodEventsDto = activityByPeriodDto.activityByDate;
        }
        return activityByPeriodDto.copy(activityByPeriodEventsDto);
    }

    @NotNull
    public String toString() {
        return "ActivityByPeriodDto(activityByDate=" + this.activityByDate + ')';
    }

    public int hashCode() {
        return this.activityByDate.hashCode();
    }

    public boolean equals(@Nullable Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof ActivityByPeriodDto)) {
            return false;
        }
        ActivityByPeriodDto activityByPeriodDto = (ActivityByPeriodDto)other;
        return Intrinsics.areEqual((Object)this.activityByDate, (Object)activityByPeriodDto.activityByDate);
    }
}

