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

import com.addonengine.addons.analytics.rest.dto.UserActivityDto;
import java.util.List;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@JsonAutoDetect
@Metadata(mv={1, 9, 0}, k=1, xi=48, d1={"\u0000*\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\u0006\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000e\n\u0000\b\u0087\b\u0018\u00002\u00020\u0001B\u0013\u0012\f\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00040\u0003\u00a2\u0006\u0002\u0010\u0005J\u000f\u0010\b\u001a\b\u0012\u0004\u0012\u00020\u00040\u0003H\u00c6\u0003J\u0019\u0010\t\u001a\u00020\u00002\u000e\b\u0002\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00040\u0003H\u00c6\u0001J\u0013\u0010\n\u001a\u00020\u000b2\b\u0010\f\u001a\u0004\u0018\u00010\u0001H\u00d6\u0003J\t\u0010\r\u001a\u00020\u000eH\u00d6\u0001J\t\u0010\u000f\u001a\u00020\u0010H\u00d6\u0001R\u0017\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00040\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0006\u0010\u0007\u00a8\u0006\u0011"}, d2={"Lcom/addonengine/addons/analytics/rest/dto/ActivityByUserDto;", "", "activityByUser", "", "Lcom/addonengine/addons/analytics/rest/dto/UserActivityDto;", "(Ljava/util/List;)V", "getActivityByUser", "()Ljava/util/List;", "component1", "copy", "equals", "", "other", "hashCode", "", "toString", "", "analytics"})
public final class ActivityByUserDto {
    @NotNull
    private final List<UserActivityDto> activityByUser;

    public ActivityByUserDto(@NotNull List<UserActivityDto> activityByUser2) {
        Intrinsics.checkNotNullParameter(activityByUser2, (String)"activityByUser");
        this.activityByUser = activityByUser2;
    }

    @NotNull
    public final List<UserActivityDto> getActivityByUser() {
        return this.activityByUser;
    }

    @NotNull
    public final List<UserActivityDto> component1() {
        return this.activityByUser;
    }

    @NotNull
    public final ActivityByUserDto copy(@NotNull List<UserActivityDto> activityByUser2) {
        Intrinsics.checkNotNullParameter(activityByUser2, (String)"activityByUser");
        return new ActivityByUserDto(activityByUser2);
    }

    public static /* synthetic */ ActivityByUserDto copy$default(ActivityByUserDto activityByUserDto, List list, int n, Object object) {
        if ((n & 1) != 0) {
            list = activityByUserDto.activityByUser;
        }
        return activityByUserDto.copy(list);
    }

    @NotNull
    public String toString() {
        return "ActivityByUserDto(activityByUser=" + this.activityByUser + ')';
    }

    public int hashCode() {
        return ((Object)this.activityByUser).hashCode();
    }

    public boolean equals(@Nullable Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof ActivityByUserDto)) {
            return false;
        }
        ActivityByUserDto activityByUserDto = (ActivityByUserDto)other;
        return Intrinsics.areEqual(this.activityByUser, activityByUserDto.activityByUser);
    }
}

