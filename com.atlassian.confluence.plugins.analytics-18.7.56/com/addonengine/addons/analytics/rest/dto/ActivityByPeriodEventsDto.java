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

import com.addonengine.addons.analytics.rest.dto.PeriodActivityDto;
import java.util.List;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@JsonAutoDetect
@Metadata(mv={1, 9, 0}, k=1, xi=48, d1={"\u0000*\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\f\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000e\n\u0000\b\u0087\b\u0018\u00002\u00020\u0001B/\u0012\f\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00040\u0003\u0012\f\u0010\u0005\u001a\b\u0012\u0004\u0012\u00020\u00040\u0003\u0012\f\u0010\u0006\u001a\b\u0012\u0004\u0012\u00020\u00040\u0003\u00a2\u0006\u0002\u0010\u0007J\u000f\u0010\f\u001a\b\u0012\u0004\u0012\u00020\u00040\u0003H\u00c6\u0003J\u000f\u0010\r\u001a\b\u0012\u0004\u0012\u00020\u00040\u0003H\u00c6\u0003J\u000f\u0010\u000e\u001a\b\u0012\u0004\u0012\u00020\u00040\u0003H\u00c6\u0003J9\u0010\u000f\u001a\u00020\u00002\u000e\b\u0002\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00040\u00032\u000e\b\u0002\u0010\u0005\u001a\b\u0012\u0004\u0012\u00020\u00040\u00032\u000e\b\u0002\u0010\u0006\u001a\b\u0012\u0004\u0012\u00020\u00040\u0003H\u00c6\u0001J\u0013\u0010\u0010\u001a\u00020\u00112\b\u0010\u0012\u001a\u0004\u0018\u00010\u0001H\u00d6\u0003J\t\u0010\u0013\u001a\u00020\u0014H\u00d6\u0001J\t\u0010\u0015\u001a\u00020\u0016H\u00d6\u0001R\u0017\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00040\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\b\u0010\tR\u0017\u0010\u0005\u001a\b\u0012\u0004\u0012\u00020\u00040\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\n\u0010\tR\u0017\u0010\u0006\u001a\b\u0012\u0004\u0012\u00020\u00040\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000b\u0010\t\u00a8\u0006\u0017"}, d2={"Lcom/addonengine/addons/analytics/rest/dto/ActivityByPeriodEventsDto;", "", "creates", "", "Lcom/addonengine/addons/analytics/rest/dto/PeriodActivityDto;", "updates", "views", "(Ljava/util/List;Ljava/util/List;Ljava/util/List;)V", "getCreates", "()Ljava/util/List;", "getUpdates", "getViews", "component1", "component2", "component3", "copy", "equals", "", "other", "hashCode", "", "toString", "", "analytics"})
public final class ActivityByPeriodEventsDto {
    @NotNull
    private final List<PeriodActivityDto> creates;
    @NotNull
    private final List<PeriodActivityDto> updates;
    @NotNull
    private final List<PeriodActivityDto> views;

    public ActivityByPeriodEventsDto(@NotNull List<PeriodActivityDto> creates, @NotNull List<PeriodActivityDto> updates, @NotNull List<PeriodActivityDto> views) {
        Intrinsics.checkNotNullParameter(creates, (String)"creates");
        Intrinsics.checkNotNullParameter(updates, (String)"updates");
        Intrinsics.checkNotNullParameter(views, (String)"views");
        this.creates = creates;
        this.updates = updates;
        this.views = views;
    }

    @NotNull
    public final List<PeriodActivityDto> getCreates() {
        return this.creates;
    }

    @NotNull
    public final List<PeriodActivityDto> getUpdates() {
        return this.updates;
    }

    @NotNull
    public final List<PeriodActivityDto> getViews() {
        return this.views;
    }

    @NotNull
    public final List<PeriodActivityDto> component1() {
        return this.creates;
    }

    @NotNull
    public final List<PeriodActivityDto> component2() {
        return this.updates;
    }

    @NotNull
    public final List<PeriodActivityDto> component3() {
        return this.views;
    }

    @NotNull
    public final ActivityByPeriodEventsDto copy(@NotNull List<PeriodActivityDto> creates, @NotNull List<PeriodActivityDto> updates, @NotNull List<PeriodActivityDto> views) {
        Intrinsics.checkNotNullParameter(creates, (String)"creates");
        Intrinsics.checkNotNullParameter(updates, (String)"updates");
        Intrinsics.checkNotNullParameter(views, (String)"views");
        return new ActivityByPeriodEventsDto(creates, updates, views);
    }

    public static /* synthetic */ ActivityByPeriodEventsDto copy$default(ActivityByPeriodEventsDto activityByPeriodEventsDto, List list, List list2, List list3, int n, Object object) {
        if ((n & 1) != 0) {
            list = activityByPeriodEventsDto.creates;
        }
        if ((n & 2) != 0) {
            list2 = activityByPeriodEventsDto.updates;
        }
        if ((n & 4) != 0) {
            list3 = activityByPeriodEventsDto.views;
        }
        return activityByPeriodEventsDto.copy(list, list2, list3);
    }

    @NotNull
    public String toString() {
        return "ActivityByPeriodEventsDto(creates=" + this.creates + ", updates=" + this.updates + ", views=" + this.views + ')';
    }

    public int hashCode() {
        int result = ((Object)this.creates).hashCode();
        result = result * 31 + ((Object)this.updates).hashCode();
        result = result * 31 + ((Object)this.views).hashCode();
        return result;
    }

    public boolean equals(@Nullable Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof ActivityByPeriodEventsDto)) {
            return false;
        }
        ActivityByPeriodEventsDto activityByPeriodEventsDto = (ActivityByPeriodEventsDto)other;
        if (!Intrinsics.areEqual(this.creates, activityByPeriodEventsDto.creates)) {
            return false;
        }
        if (!Intrinsics.areEqual(this.updates, activityByPeriodEventsDto.updates)) {
            return false;
        }
        return Intrinsics.areEqual(this.views, activityByPeriodEventsDto.views);
    }
}

