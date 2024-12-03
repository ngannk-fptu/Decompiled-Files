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

import com.addonengine.addons.analytics.rest.dto.SpaceLevelUserActivityDto;
import java.util.List;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@JsonAutoDetect
@Metadata(mv={1, 9, 0}, k=1, xi=48, d1={"\u0000,\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\t\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0002\b\u0087\b\u0018\u00002\u00020\u0001B\u001d\u0012\f\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00040\u0003\u0012\b\u0010\u0005\u001a\u0004\u0018\u00010\u0006\u00a2\u0006\u0002\u0010\u0007J\u000f\u0010\f\u001a\b\u0012\u0004\u0012\u00020\u00040\u0003H\u00c6\u0003J\u000b\u0010\r\u001a\u0004\u0018\u00010\u0006H\u00c6\u0003J%\u0010\u000e\u001a\u00020\u00002\u000e\b\u0002\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00040\u00032\n\b\u0002\u0010\u0005\u001a\u0004\u0018\u00010\u0006H\u00c6\u0001J\u0013\u0010\u000f\u001a\u00020\u00102\b\u0010\u0011\u001a\u0004\u0018\u00010\u0001H\u00d6\u0003J\t\u0010\u0012\u001a\u00020\u0013H\u00d6\u0001J\t\u0010\u0014\u001a\u00020\u0006H\u00d6\u0001R\u0017\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00040\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\b\u0010\tR\u0013\u0010\u0005\u001a\u0004\u0018\u00010\u0006\u00a2\u0006\b\n\u0000\u001a\u0004\b\n\u0010\u000b\u00a8\u0006\u0015"}, d2={"Lcom/addonengine/addons/analytics/rest/dto/SpaceLevelActivityByUserDto;", "", "activityByUser", "", "Lcom/addonengine/addons/analytics/rest/dto/SpaceLevelUserActivityDto;", "nextPageToken", "", "(Ljava/util/List;Ljava/lang/String;)V", "getActivityByUser", "()Ljava/util/List;", "getNextPageToken", "()Ljava/lang/String;", "component1", "component2", "copy", "equals", "", "other", "hashCode", "", "toString", "analytics"})
public final class SpaceLevelActivityByUserDto {
    @NotNull
    private final List<SpaceLevelUserActivityDto> activityByUser;
    @Nullable
    private final String nextPageToken;

    public SpaceLevelActivityByUserDto(@NotNull List<SpaceLevelUserActivityDto> activityByUser2, @Nullable String nextPageToken) {
        Intrinsics.checkNotNullParameter(activityByUser2, (String)"activityByUser");
        this.activityByUser = activityByUser2;
        this.nextPageToken = nextPageToken;
    }

    @NotNull
    public final List<SpaceLevelUserActivityDto> getActivityByUser() {
        return this.activityByUser;
    }

    @Nullable
    public final String getNextPageToken() {
        return this.nextPageToken;
    }

    @NotNull
    public final List<SpaceLevelUserActivityDto> component1() {
        return this.activityByUser;
    }

    @Nullable
    public final String component2() {
        return this.nextPageToken;
    }

    @NotNull
    public final SpaceLevelActivityByUserDto copy(@NotNull List<SpaceLevelUserActivityDto> activityByUser2, @Nullable String nextPageToken) {
        Intrinsics.checkNotNullParameter(activityByUser2, (String)"activityByUser");
        return new SpaceLevelActivityByUserDto(activityByUser2, nextPageToken);
    }

    public static /* synthetic */ SpaceLevelActivityByUserDto copy$default(SpaceLevelActivityByUserDto spaceLevelActivityByUserDto, List list, String string, int n, Object object) {
        if ((n & 1) != 0) {
            list = spaceLevelActivityByUserDto.activityByUser;
        }
        if ((n & 2) != 0) {
            string = spaceLevelActivityByUserDto.nextPageToken;
        }
        return spaceLevelActivityByUserDto.copy(list, string);
    }

    @NotNull
    public String toString() {
        return "SpaceLevelActivityByUserDto(activityByUser=" + this.activityByUser + ", nextPageToken=" + this.nextPageToken + ')';
    }

    public int hashCode() {
        int result = ((Object)this.activityByUser).hashCode();
        result = result * 31 + (this.nextPageToken == null ? 0 : this.nextPageToken.hashCode());
        return result;
    }

    public boolean equals(@Nullable Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof SpaceLevelActivityByUserDto)) {
            return false;
        }
        SpaceLevelActivityByUserDto spaceLevelActivityByUserDto = (SpaceLevelActivityByUserDto)other;
        if (!Intrinsics.areEqual(this.activityByUser, spaceLevelActivityByUserDto.activityByUser)) {
            return false;
        }
        return Intrinsics.areEqual((Object)this.nextPageToken, (Object)spaceLevelActivityByUserDto.nextPageToken);
    }
}

