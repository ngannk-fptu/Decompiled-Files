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

import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(mv={1, 9, 0}, k=1, xi=48, d1={"\u0000(\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\t\n\u0002\b\u0015\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0002\b\u0086\b\u0018\u00002\u00020\u0001B5\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\u0006\u0010\u0006\u001a\u00020\u0005\u0012\u0006\u0010\u0007\u001a\u00020\u0005\u0012\u0006\u0010\b\u001a\u00020\u0005\u0012\u0006\u0010\t\u001a\u00020\u0005\u00a2\u0006\u0002\u0010\nJ\t\u0010\u0013\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\u0014\u001a\u00020\u0005H\u00c6\u0003J\t\u0010\u0015\u001a\u00020\u0005H\u00c6\u0003J\t\u0010\u0016\u001a\u00020\u0005H\u00c6\u0003J\t\u0010\u0017\u001a\u00020\u0005H\u00c6\u0003J\t\u0010\u0018\u001a\u00020\u0005H\u00c6\u0003JE\u0010\u0019\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00052\b\b\u0002\u0010\u0006\u001a\u00020\u00052\b\b\u0002\u0010\u0007\u001a\u00020\u00052\b\b\u0002\u0010\b\u001a\u00020\u00052\b\b\u0002\u0010\t\u001a\u00020\u0005H\u00c6\u0001J\u0013\u0010\u001a\u001a\u00020\u001b2\b\u0010\u001c\u001a\u0004\u0018\u00010\u0001H\u00d6\u0003J\t\u0010\u001d\u001a\u00020\u001eH\u00d6\u0001J\t\u0010\u001f\u001a\u00020\u0003H\u00d6\u0001R\u0011\u0010\b\u001a\u00020\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000b\u0010\fR\u0011\u0010\t\u001a\u00020\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b\r\u0010\fR\u0011\u0010\u0006\u001a\u00020\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000e\u0010\fR\u0011\u0010\u0007\u001a\u00020\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000f\u0010\fR\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0010\u0010\u0011R\u0011\u0010\u0004\u001a\u00020\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0012\u0010\f\u00a8\u0006 "}, d2={"Lcom/addonengine/addons/analytics/store/model/FullGlobalUserStatistics;", "", "userKey", "", "viewedCount", "", "createdCount", "updatedCount", "commentsCount", "contributorScore", "(Ljava/lang/String;JJJJJ)V", "getCommentsCount", "()J", "getContributorScore", "getCreatedCount", "getUpdatedCount", "getUserKey", "()Ljava/lang/String;", "getViewedCount", "component1", "component2", "component3", "component4", "component5", "component6", "copy", "equals", "", "other", "hashCode", "", "toString", "analytics"})
public final class FullGlobalUserStatistics {
    @NotNull
    private final String userKey;
    private final long viewedCount;
    private final long createdCount;
    private final long updatedCount;
    private final long commentsCount;
    private final long contributorScore;

    public FullGlobalUserStatistics(@NotNull String userKey, long viewedCount, long createdCount, long updatedCount, long commentsCount, long contributorScore) {
        Intrinsics.checkNotNullParameter((Object)userKey, (String)"userKey");
        this.userKey = userKey;
        this.viewedCount = viewedCount;
        this.createdCount = createdCount;
        this.updatedCount = updatedCount;
        this.commentsCount = commentsCount;
        this.contributorScore = contributorScore;
    }

    @NotNull
    public final String getUserKey() {
        return this.userKey;
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
        return this.userKey;
    }

    public final long component2() {
        return this.viewedCount;
    }

    public final long component3() {
        return this.createdCount;
    }

    public final long component4() {
        return this.updatedCount;
    }

    public final long component5() {
        return this.commentsCount;
    }

    public final long component6() {
        return this.contributorScore;
    }

    @NotNull
    public final FullGlobalUserStatistics copy(@NotNull String userKey, long viewedCount, long createdCount, long updatedCount, long commentsCount, long contributorScore) {
        Intrinsics.checkNotNullParameter((Object)userKey, (String)"userKey");
        return new FullGlobalUserStatistics(userKey, viewedCount, createdCount, updatedCount, commentsCount, contributorScore);
    }

    public static /* synthetic */ FullGlobalUserStatistics copy$default(FullGlobalUserStatistics fullGlobalUserStatistics, String string, long l, long l2, long l3, long l4, long l5, int n, Object object) {
        if ((n & 1) != 0) {
            string = fullGlobalUserStatistics.userKey;
        }
        if ((n & 2) != 0) {
            l = fullGlobalUserStatistics.viewedCount;
        }
        if ((n & 4) != 0) {
            l2 = fullGlobalUserStatistics.createdCount;
        }
        if ((n & 8) != 0) {
            l3 = fullGlobalUserStatistics.updatedCount;
        }
        if ((n & 0x10) != 0) {
            l4 = fullGlobalUserStatistics.commentsCount;
        }
        if ((n & 0x20) != 0) {
            l5 = fullGlobalUserStatistics.contributorScore;
        }
        return fullGlobalUserStatistics.copy(string, l, l2, l3, l4, l5);
    }

    @NotNull
    public String toString() {
        return "FullGlobalUserStatistics(userKey=" + this.userKey + ", viewedCount=" + this.viewedCount + ", createdCount=" + this.createdCount + ", updatedCount=" + this.updatedCount + ", commentsCount=" + this.commentsCount + ", contributorScore=" + this.contributorScore + ')';
    }

    public int hashCode() {
        int result = this.userKey.hashCode();
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
        if (!(other instanceof FullGlobalUserStatistics)) {
            return false;
        }
        FullGlobalUserStatistics fullGlobalUserStatistics = (FullGlobalUserStatistics)other;
        if (!Intrinsics.areEqual((Object)this.userKey, (Object)fullGlobalUserStatistics.userKey)) {
            return false;
        }
        if (this.viewedCount != fullGlobalUserStatistics.viewedCount) {
            return false;
        }
        if (this.createdCount != fullGlobalUserStatistics.createdCount) {
            return false;
        }
        if (this.updatedCount != fullGlobalUserStatistics.updatedCount) {
            return false;
        }
        if (this.commentsCount != fullGlobalUserStatistics.commentsCount) {
            return false;
        }
        return this.contributorScore == fullGlobalUserStatistics.contributorScore;
    }
}

