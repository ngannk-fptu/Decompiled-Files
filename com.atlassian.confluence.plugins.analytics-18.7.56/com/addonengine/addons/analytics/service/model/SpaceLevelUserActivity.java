/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  kotlin.Metadata
 *  kotlin.jvm.internal.Intrinsics
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package com.addonengine.addons.analytics.service.model;

import com.addonengine.addons.analytics.service.confluence.model.UserType;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(mv={1, 9, 0}, k=1, xi=48, d1={"\u0000.\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\t\n\u0002\b\u0018\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0002\b\u0086\b\u0018\u00002\u00020\u0001B?\u0012\b\u0010\u0002\u001a\u0004\u0018\u00010\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\u0006\u0010\u0006\u001a\u00020\u0007\u0012\u0006\u0010\b\u001a\u00020\u0007\u0012\u0006\u0010\t\u001a\u00020\u0007\u0012\u0006\u0010\n\u001a\u00020\u0007\u0012\u0006\u0010\u000b\u001a\u00020\u0007\u00a2\u0006\u0002\u0010\fJ\u000b\u0010\u0017\u001a\u0004\u0018\u00010\u0003H\u00c6\u0003J\t\u0010\u0018\u001a\u00020\u0005H\u00c6\u0003J\t\u0010\u0019\u001a\u00020\u0007H\u00c6\u0003J\t\u0010\u001a\u001a\u00020\u0007H\u00c6\u0003J\t\u0010\u001b\u001a\u00020\u0007H\u00c6\u0003J\t\u0010\u001c\u001a\u00020\u0007H\u00c6\u0003J\t\u0010\u001d\u001a\u00020\u0007H\u00c6\u0003JQ\u0010\u001e\u001a\u00020\u00002\n\b\u0002\u0010\u0002\u001a\u0004\u0018\u00010\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00052\b\b\u0002\u0010\u0006\u001a\u00020\u00072\b\b\u0002\u0010\b\u001a\u00020\u00072\b\b\u0002\u0010\t\u001a\u00020\u00072\b\b\u0002\u0010\n\u001a\u00020\u00072\b\b\u0002\u0010\u000b\u001a\u00020\u0007H\u00c6\u0001J\u0013\u0010\u001f\u001a\u00020 2\b\u0010!\u001a\u0004\u0018\u00010\u0001H\u00d6\u0003J\t\u0010\"\u001a\u00020#H\u00d6\u0001J\t\u0010$\u001a\u00020\u0003H\u00d6\u0001R\u0011\u0010\n\u001a\u00020\u0007\u00a2\u0006\b\n\u0000\u001a\u0004\b\r\u0010\u000eR\u0011\u0010\u000b\u001a\u00020\u0007\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000f\u0010\u000eR\u0011\u0010\b\u001a\u00020\u0007\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0010\u0010\u000eR\u0011\u0010\t\u001a\u00020\u0007\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0011\u0010\u000eR\u0013\u0010\u0002\u001a\u0004\u0018\u00010\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0012\u0010\u0013R\u0011\u0010\u0004\u001a\u00020\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0014\u0010\u0015R\u0011\u0010\u0006\u001a\u00020\u0007\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0016\u0010\u000e\u00a8\u0006%"}, d2={"Lcom/addonengine/addons/analytics/service/model/SpaceLevelUserActivity;", "", "userKey", "", "userType", "Lcom/addonengine/addons/analytics/service/confluence/model/UserType;", "viewedCount", "", "createdCount", "updatedCount", "commentsCount", "contributorScore", "(Ljava/lang/String;Lcom/addonengine/addons/analytics/service/confluence/model/UserType;JJJJJ)V", "getCommentsCount", "()J", "getContributorScore", "getCreatedCount", "getUpdatedCount", "getUserKey", "()Ljava/lang/String;", "getUserType", "()Lcom/addonengine/addons/analytics/service/confluence/model/UserType;", "getViewedCount", "component1", "component2", "component3", "component4", "component5", "component6", "component7", "copy", "equals", "", "other", "hashCode", "", "toString", "analytics"})
public final class SpaceLevelUserActivity {
    @Nullable
    private final String userKey;
    @NotNull
    private final UserType userType;
    private final long viewedCount;
    private final long createdCount;
    private final long updatedCount;
    private final long commentsCount;
    private final long contributorScore;

    public SpaceLevelUserActivity(@Nullable String userKey, @NotNull UserType userType, long viewedCount, long createdCount, long updatedCount, long commentsCount, long contributorScore) {
        Intrinsics.checkNotNullParameter((Object)((Object)userType), (String)"userType");
        this.userKey = userKey;
        this.userType = userType;
        this.viewedCount = viewedCount;
        this.createdCount = createdCount;
        this.updatedCount = updatedCount;
        this.commentsCount = commentsCount;
        this.contributorScore = contributorScore;
    }

    @Nullable
    public final String getUserKey() {
        return this.userKey;
    }

    @NotNull
    public final UserType getUserType() {
        return this.userType;
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

    @Nullable
    public final String component1() {
        return this.userKey;
    }

    @NotNull
    public final UserType component2() {
        return this.userType;
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
    public final SpaceLevelUserActivity copy(@Nullable String userKey, @NotNull UserType userType, long viewedCount, long createdCount, long updatedCount, long commentsCount, long contributorScore) {
        Intrinsics.checkNotNullParameter((Object)((Object)userType), (String)"userType");
        return new SpaceLevelUserActivity(userKey, userType, viewedCount, createdCount, updatedCount, commentsCount, contributorScore);
    }

    public static /* synthetic */ SpaceLevelUserActivity copy$default(SpaceLevelUserActivity spaceLevelUserActivity, String string, UserType userType, long l, long l2, long l3, long l4, long l5, int n, Object object) {
        if ((n & 1) != 0) {
            string = spaceLevelUserActivity.userKey;
        }
        if ((n & 2) != 0) {
            userType = spaceLevelUserActivity.userType;
        }
        if ((n & 4) != 0) {
            l = spaceLevelUserActivity.viewedCount;
        }
        if ((n & 8) != 0) {
            l2 = spaceLevelUserActivity.createdCount;
        }
        if ((n & 0x10) != 0) {
            l3 = spaceLevelUserActivity.updatedCount;
        }
        if ((n & 0x20) != 0) {
            l4 = spaceLevelUserActivity.commentsCount;
        }
        if ((n & 0x40) != 0) {
            l5 = spaceLevelUserActivity.contributorScore;
        }
        return spaceLevelUserActivity.copy(string, userType, l, l2, l3, l4, l5);
    }

    @NotNull
    public String toString() {
        return "SpaceLevelUserActivity(userKey=" + this.userKey + ", userType=" + (Object)((Object)this.userType) + ", viewedCount=" + this.viewedCount + ", createdCount=" + this.createdCount + ", updatedCount=" + this.updatedCount + ", commentsCount=" + this.commentsCount + ", contributorScore=" + this.contributorScore + ')';
    }

    public int hashCode() {
        int result = this.userKey == null ? 0 : this.userKey.hashCode();
        result = result * 31 + this.userType.hashCode();
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
        if (!(other instanceof SpaceLevelUserActivity)) {
            return false;
        }
        SpaceLevelUserActivity spaceLevelUserActivity = (SpaceLevelUserActivity)other;
        if (!Intrinsics.areEqual((Object)this.userKey, (Object)spaceLevelUserActivity.userKey)) {
            return false;
        }
        if (this.userType != spaceLevelUserActivity.userType) {
            return false;
        }
        if (this.viewedCount != spaceLevelUserActivity.viewedCount) {
            return false;
        }
        if (this.createdCount != spaceLevelUserActivity.createdCount) {
            return false;
        }
        if (this.updatedCount != spaceLevelUserActivity.updatedCount) {
            return false;
        }
        if (this.commentsCount != spaceLevelUserActivity.commentsCount) {
            return false;
        }
        return this.contributorScore == spaceLevelUserActivity.contributorScore;
    }
}

