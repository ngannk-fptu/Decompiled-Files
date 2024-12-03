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

import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@JsonAutoDetect
@Metadata(mv={1, 9, 0}, k=1, xi=48, d1={"\u0000*\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0010\t\n\u0002\b\u0017\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0002\b\u0087\b\u0018\u00002\u00020\u0001B=\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0003\u0012\u0006\u0010\u0005\u001a\u00020\u0006\u0012\u0006\u0010\u0007\u001a\u00020\u0006\u0012\u0006\u0010\b\u001a\u00020\u0006\u0012\u0006\u0010\t\u001a\u00020\u0006\u0012\u0006\u0010\n\u001a\u00020\u0006\u00a2\u0006\u0002\u0010\u000bJ\t\u0010\u0015\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\u0016\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\u0017\u001a\u00020\u0006H\u00c6\u0003J\t\u0010\u0018\u001a\u00020\u0006H\u00c6\u0003J\t\u0010\u0019\u001a\u00020\u0006H\u00c6\u0003J\t\u0010\u001a\u001a\u00020\u0006H\u00c6\u0003J\t\u0010\u001b\u001a\u00020\u0006H\u00c6\u0003JO\u0010\u001c\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00032\b\b\u0002\u0010\u0005\u001a\u00020\u00062\b\b\u0002\u0010\u0007\u001a\u00020\u00062\b\b\u0002\u0010\b\u001a\u00020\u00062\b\b\u0002\u0010\t\u001a\u00020\u00062\b\b\u0002\u0010\n\u001a\u00020\u0006H\u00c6\u0001J\u0013\u0010\u001d\u001a\u00020\u001e2\b\u0010\u001f\u001a\u0004\u0018\u00010\u0001H\u00d6\u0003J\t\u0010 \u001a\u00020!H\u00d6\u0001J\t\u0010\"\u001a\u00020\u0003H\u00d6\u0001R\u0011\u0010\t\u001a\u00020\u0006\u00a2\u0006\b\n\u0000\u001a\u0004\b\f\u0010\rR\u0011\u0010\n\u001a\u00020\u0006\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000e\u0010\rR\u0011\u0010\u0007\u001a\u00020\u0006\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000f\u0010\rR\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0010\u0010\u0011R\u0011\u0010\b\u001a\u00020\u0006\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0012\u0010\rR\u0011\u0010\u0004\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0013\u0010\u0011R\u0011\u0010\u0005\u001a\u00020\u0006\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0014\u0010\r\u00a8\u0006#"}, d2={"Lcom/addonengine/addons/analytics/rest/dto/SpaceLevelUserActivityDto;", "", "type", "", "userId", "viewedCount", "", "createdCount", "updatedCount", "commentsCount", "contributorScore", "(Ljava/lang/String;Ljava/lang/String;JJJJJ)V", "getCommentsCount", "()J", "getContributorScore", "getCreatedCount", "getType", "()Ljava/lang/String;", "getUpdatedCount", "getUserId", "getViewedCount", "component1", "component2", "component3", "component4", "component5", "component6", "component7", "copy", "equals", "", "other", "hashCode", "", "toString", "analytics"})
public final class SpaceLevelUserActivityDto {
    @NotNull
    private final String type;
    @NotNull
    private final String userId;
    private final long viewedCount;
    private final long createdCount;
    private final long updatedCount;
    private final long commentsCount;
    private final long contributorScore;

    public SpaceLevelUserActivityDto(@NotNull String type, @NotNull String userId, long viewedCount, long createdCount, long updatedCount, long commentsCount, long contributorScore) {
        Intrinsics.checkNotNullParameter((Object)type, (String)"type");
        Intrinsics.checkNotNullParameter((Object)userId, (String)"userId");
        this.type = type;
        this.userId = userId;
        this.viewedCount = viewedCount;
        this.createdCount = createdCount;
        this.updatedCount = updatedCount;
        this.commentsCount = commentsCount;
        this.contributorScore = contributorScore;
    }

    @NotNull
    public final String getType() {
        return this.type;
    }

    @NotNull
    public final String getUserId() {
        return this.userId;
    }

    public final long getViewedCount() {
        return this.viewedCount;
    }

    public final long getCreatedCount() {
        return this.createdCount;
    }

    public final long getUpdatedCount() {
        return this.updatedCount;
    }

    public final long getCommentsCount() {
        return this.commentsCount;
    }

    public final long getContributorScore() {
        return this.contributorScore;
    }

    @NotNull
    public final String component1() {
        return this.type;
    }

    @NotNull
    public final String component2() {
        return this.userId;
    }

    public final long component3() {
        return this.viewedCount;
    }

    public final long component4() {
        return this.createdCount;
    }

    public final long component5() {
        return this.updatedCount;
    }

    public final long component6() {
        return this.commentsCount;
    }

    public final long component7() {
        return this.contributorScore;
    }

    @NotNull
    public final SpaceLevelUserActivityDto copy(@NotNull String type, @NotNull String userId, long viewedCount, long createdCount, long updatedCount, long commentsCount, long contributorScore) {
        Intrinsics.checkNotNullParameter((Object)type, (String)"type");
        Intrinsics.checkNotNullParameter((Object)userId, (String)"userId");
        return new SpaceLevelUserActivityDto(type, userId, viewedCount, createdCount, updatedCount, commentsCount, contributorScore);
    }

    public static /* synthetic */ SpaceLevelUserActivityDto copy$default(SpaceLevelUserActivityDto spaceLevelUserActivityDto, String string, String string2, long l, long l2, long l3, long l4, long l5, int n, Object object) {
        if ((n & 1) != 0) {
            string = spaceLevelUserActivityDto.type;
        }
        if ((n & 2) != 0) {
            string2 = spaceLevelUserActivityDto.userId;
        }
        if ((n & 4) != 0) {
            l = spaceLevelUserActivityDto.viewedCount;
        }
        if ((n & 8) != 0) {
            l2 = spaceLevelUserActivityDto.createdCount;
        }
        if ((n & 0x10) != 0) {
            l3 = spaceLevelUserActivityDto.updatedCount;
        }
        if ((n & 0x20) != 0) {
            l4 = spaceLevelUserActivityDto.commentsCount;
        }
        if ((n & 0x40) != 0) {
            l5 = spaceLevelUserActivityDto.contributorScore;
        }
        return spaceLevelUserActivityDto.copy(string, string2, l, l2, l3, l4, l5);
    }

    @NotNull
    public String toString() {
        return "SpaceLevelUserActivityDto(type=" + this.type + ", userId=" + this.userId + ", viewedCount=" + this.viewedCount + ", createdCount=" + this.createdCount + ", updatedCount=" + this.updatedCount + ", commentsCount=" + this.commentsCount + ", contributorScore=" + this.contributorScore + ')';
    }

    public int hashCode() {
        int result = this.type.hashCode();
        result = result * 31 + this.userId.hashCode();
        result = result * 31 + Long.hashCode(this.viewedCount);
        result = result * 31 + Long.hashCode(this.createdCount);
        result = result * 31 + Long.hashCode(this.updatedCount);
        result = result * 31 + Long.hashCode(this.commentsCount);
        result = result * 31 + Long.hashCode(this.contributorScore);
        return result;
    }

    public boolean equals(@Nullable Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof SpaceLevelUserActivityDto)) {
            return false;
        }
        SpaceLevelUserActivityDto spaceLevelUserActivityDto = (SpaceLevelUserActivityDto)other;
        if (!Intrinsics.areEqual((Object)this.type, (Object)spaceLevelUserActivityDto.type)) {
            return false;
        }
        if (!Intrinsics.areEqual((Object)this.userId, (Object)spaceLevelUserActivityDto.userId)) {
            return false;
        }
        if (this.viewedCount != spaceLevelUserActivityDto.viewedCount) {
            return false;
        }
        if (this.createdCount != spaceLevelUserActivityDto.createdCount) {
            return false;
        }
        if (this.updatedCount != spaceLevelUserActivityDto.updatedCount) {
            return false;
        }
        if (this.commentsCount != spaceLevelUserActivityDto.commentsCount) {
            return false;
        }
        return this.contributorScore == spaceLevelUserActivityDto.contributorScore;
    }
}

