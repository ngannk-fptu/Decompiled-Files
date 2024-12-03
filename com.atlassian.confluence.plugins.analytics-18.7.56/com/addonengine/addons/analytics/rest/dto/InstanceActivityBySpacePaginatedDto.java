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

import com.addonengine.addons.analytics.rest.dto.InstanceSpaceActivityDto;
import java.util.List;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@JsonAutoDetect
@Metadata(mv={1, 9, 0}, k=1, xi=48, d1={"\u0000,\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\t\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0002\b\u0087\b\u0018\u00002\u00020\u0001B\u001d\u0012\f\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00040\u0003\u0012\b\u0010\u0005\u001a\u0004\u0018\u00010\u0006\u00a2\u0006\u0002\u0010\u0007J\u000f\u0010\f\u001a\b\u0012\u0004\u0012\u00020\u00040\u0003H\u00c6\u0003J\u000b\u0010\r\u001a\u0004\u0018\u00010\u0006H\u00c6\u0003J%\u0010\u000e\u001a\u00020\u00002\u000e\b\u0002\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00040\u00032\n\b\u0002\u0010\u0005\u001a\u0004\u0018\u00010\u0006H\u00c6\u0001J\u0013\u0010\u000f\u001a\u00020\u00102\b\u0010\u0011\u001a\u0004\u0018\u00010\u0001H\u00d6\u0003J\t\u0010\u0012\u001a\u00020\u0013H\u00d6\u0001J\t\u0010\u0014\u001a\u00020\u0006H\u00d6\u0001R\u0017\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00040\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\b\u0010\tR\u0013\u0010\u0005\u001a\u0004\u0018\u00010\u0006\u00a2\u0006\b\n\u0000\u001a\u0004\b\n\u0010\u000b\u00a8\u0006\u0015"}, d2={"Lcom/addonengine/addons/analytics/rest/dto/InstanceActivityBySpacePaginatedDto;", "", "activityBySpace", "", "Lcom/addonengine/addons/analytics/rest/dto/InstanceSpaceActivityDto;", "nextPageToken", "", "(Ljava/util/List;Ljava/lang/String;)V", "getActivityBySpace", "()Ljava/util/List;", "getNextPageToken", "()Ljava/lang/String;", "component1", "component2", "copy", "equals", "", "other", "hashCode", "", "toString", "analytics"})
public final class InstanceActivityBySpacePaginatedDto {
    @NotNull
    private final List<InstanceSpaceActivityDto> activityBySpace;
    @Nullable
    private final String nextPageToken;

    public InstanceActivityBySpacePaginatedDto(@NotNull List<InstanceSpaceActivityDto> activityBySpace2, @Nullable String nextPageToken) {
        Intrinsics.checkNotNullParameter(activityBySpace2, (String)"activityBySpace");
        this.activityBySpace = activityBySpace2;
        this.nextPageToken = nextPageToken;
    }

    @NotNull
    public final List<InstanceSpaceActivityDto> getActivityBySpace() {
        return this.activityBySpace;
    }

    @Nullable
    public final String getNextPageToken() {
        return this.nextPageToken;
    }

    @NotNull
    public final List<InstanceSpaceActivityDto> component1() {
        return this.activityBySpace;
    }

    @Nullable
    public final String component2() {
        return this.nextPageToken;
    }

    @NotNull
    public final InstanceActivityBySpacePaginatedDto copy(@NotNull List<InstanceSpaceActivityDto> activityBySpace2, @Nullable String nextPageToken) {
        Intrinsics.checkNotNullParameter(activityBySpace2, (String)"activityBySpace");
        return new InstanceActivityBySpacePaginatedDto(activityBySpace2, nextPageToken);
    }

    public static /* synthetic */ InstanceActivityBySpacePaginatedDto copy$default(InstanceActivityBySpacePaginatedDto instanceActivityBySpacePaginatedDto, List list, String string, int n, Object object) {
        if ((n & 1) != 0) {
            list = instanceActivityBySpacePaginatedDto.activityBySpace;
        }
        if ((n & 2) != 0) {
            string = instanceActivityBySpacePaginatedDto.nextPageToken;
        }
        return instanceActivityBySpacePaginatedDto.copy(list, string);
    }

    @NotNull
    public String toString() {
        return "InstanceActivityBySpacePaginatedDto(activityBySpace=" + this.activityBySpace + ", nextPageToken=" + this.nextPageToken + ')';
    }

    public int hashCode() {
        int result = ((Object)this.activityBySpace).hashCode();
        result = result * 31 + (this.nextPageToken == null ? 0 : this.nextPageToken.hashCode());
        return result;
    }

    public boolean equals(@Nullable Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof InstanceActivityBySpacePaginatedDto)) {
            return false;
        }
        InstanceActivityBySpacePaginatedDto instanceActivityBySpacePaginatedDto = (InstanceActivityBySpacePaginatedDto)other;
        if (!Intrinsics.areEqual(this.activityBySpace, instanceActivityBySpacePaginatedDto.activityBySpace)) {
            return false;
        }
        return Intrinsics.areEqual((Object)this.nextPageToken, (Object)instanceActivityBySpacePaginatedDto.nextPageToken);
    }
}

