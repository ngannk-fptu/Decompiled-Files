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

import java.sql.Timestamp;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(mv={1, 9, 0}, k=1, xi=48, d1={"\u0000.\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\t\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u001e\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0002\b\u0086\b\u0018\u00002\u00020\u0001BK\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\b\u0010\u0006\u001a\u0004\u0018\u00010\u0007\u0012\b\u0010\b\u001a\u0004\u0018\u00010\u0007\u0012\b\u0010\t\u001a\u0004\u0018\u00010\u0003\u0012\u0006\u0010\n\u001a\u00020\u0003\u0012\u0006\u0010\u000b\u001a\u00020\u0003\u0012\u0006\u0010\f\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\rJ\t\u0010\u001b\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\u001c\u001a\u00020\u0005H\u00c6\u0003J\u000b\u0010\u001d\u001a\u0004\u0018\u00010\u0007H\u00c6\u0003J\u000b\u0010\u001e\u001a\u0004\u0018\u00010\u0007H\u00c6\u0003J\u0010\u0010\u001f\u001a\u0004\u0018\u00010\u0003H\u00c6\u0003\u00a2\u0006\u0002\u0010\u0016J\t\u0010 \u001a\u00020\u0003H\u00c6\u0003J\t\u0010!\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\"\u001a\u00020\u0003H\u00c6\u0003Jd\u0010#\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00052\n\b\u0002\u0010\u0006\u001a\u0004\u0018\u00010\u00072\n\b\u0002\u0010\b\u001a\u0004\u0018\u00010\u00072\n\b\u0002\u0010\t\u001a\u0004\u0018\u00010\u00032\b\b\u0002\u0010\n\u001a\u00020\u00032\b\b\u0002\u0010\u000b\u001a\u00020\u00032\b\b\u0002\u0010\f\u001a\u00020\u0003H\u00c6\u0001\u00a2\u0006\u0002\u0010$J\u0013\u0010%\u001a\u00020&2\b\u0010'\u001a\u0004\u0018\u00010\u0001H\u00d6\u0003J\t\u0010(\u001a\u00020)H\u00d6\u0001J\t\u0010*\u001a\u00020\u0005H\u00d6\u0001R\u0011\u0010\n\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000e\u0010\u000fR\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0010\u0010\u000fR\u0011\u0010\u0004\u001a\u00020\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0011\u0010\u0012R\u0013\u0010\u0006\u001a\u0004\u0018\u00010\u0007\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0013\u0010\u0014R\u0015\u0010\t\u001a\u0004\u0018\u00010\u0003\u00a2\u0006\n\n\u0002\u0010\u0017\u001a\u0004\b\u0015\u0010\u0016R\u0013\u0010\b\u001a\u0004\u0018\u00010\u0007\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0018\u0010\u0014R\u0011\u0010\f\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0019\u0010\u000fR\u0011\u0010\u000b\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001a\u0010\u000f\u00a8\u0006+"}, d2={"Lcom/addonengine/addons/analytics/store/model/FullContentStatistics;", "", "contentId", "", "contentName", "", "createdAt", "Ljava/sql/Timestamp;", "lastModifiedAt", "lastEventAt", "commentsCount", "viewCount", "usersViewed", "(JLjava/lang/String;Ljava/sql/Timestamp;Ljava/sql/Timestamp;Ljava/lang/Long;JJJ)V", "getCommentsCount", "()J", "getContentId", "getContentName", "()Ljava/lang/String;", "getCreatedAt", "()Ljava/sql/Timestamp;", "getLastEventAt", "()Ljava/lang/Long;", "Ljava/lang/Long;", "getLastModifiedAt", "getUsersViewed", "getViewCount", "component1", "component2", "component3", "component4", "component5", "component6", "component7", "component8", "copy", "(JLjava/lang/String;Ljava/sql/Timestamp;Ljava/sql/Timestamp;Ljava/lang/Long;JJJ)Lcom/addonengine/addons/analytics/store/model/FullContentStatistics;", "equals", "", "other", "hashCode", "", "toString", "analytics"})
public final class FullContentStatistics {
    private final long contentId;
    @NotNull
    private final String contentName;
    @Nullable
    private final Timestamp createdAt;
    @Nullable
    private final Timestamp lastModifiedAt;
    @Nullable
    private final Long lastEventAt;
    private final long commentsCount;
    private final long viewCount;
    private final long usersViewed;

    public FullContentStatistics(long contentId, @NotNull String contentName, @Nullable Timestamp createdAt, @Nullable Timestamp lastModifiedAt, @Nullable Long lastEventAt, long commentsCount, long viewCount, long usersViewed) {
        Intrinsics.checkNotNullParameter((Object)contentName, (String)"contentName");
        this.contentId = contentId;
        this.contentName = contentName;
        this.createdAt = createdAt;
        this.lastModifiedAt = lastModifiedAt;
        this.lastEventAt = lastEventAt;
        this.commentsCount = commentsCount;
        this.viewCount = viewCount;
        this.usersViewed = usersViewed;
    }

    public final long getContentId() {
        return this.contentId;
    }

    @NotNull
    public final String getContentName() {
        return this.contentName;
    }

    @Nullable
    public final Timestamp getCreatedAt() {
        return this.createdAt;
    }

    @Nullable
    public final Timestamp getLastModifiedAt() {
        return this.lastModifiedAt;
    }

    @Nullable
    public final Long getLastEventAt() {
        return this.lastEventAt;
    }

    public final long getCommentsCount() {
        return this.commentsCount;
    }

    public final long getViewCount() {
        return this.viewCount;
    }

    public final long getUsersViewed() {
        return this.usersViewed;
    }

    public final long component1() {
        return this.contentId;
    }

    @NotNull
    public final String component2() {
        return this.contentName;
    }

    @Nullable
    public final Timestamp component3() {
        return this.createdAt;
    }

    @Nullable
    public final Timestamp component4() {
        return this.lastModifiedAt;
    }

    @Nullable
    public final Long component5() {
        return this.lastEventAt;
    }

    public final long component6() {
        return this.commentsCount;
    }

    public final long component7() {
        return this.viewCount;
    }

    public final long component8() {
        return this.usersViewed;
    }

    @NotNull
    public final FullContentStatistics copy(long contentId, @NotNull String contentName, @Nullable Timestamp createdAt, @Nullable Timestamp lastModifiedAt, @Nullable Long lastEventAt, long commentsCount, long viewCount, long usersViewed) {
        Intrinsics.checkNotNullParameter((Object)contentName, (String)"contentName");
        return new FullContentStatistics(contentId, contentName, createdAt, lastModifiedAt, lastEventAt, commentsCount, viewCount, usersViewed);
    }

    public static /* synthetic */ FullContentStatistics copy$default(FullContentStatistics fullContentStatistics, long l, String string, Timestamp timestamp, Timestamp timestamp2, Long l2, long l3, long l4, long l5, int n, Object object) {
        if ((n & 1) != 0) {
            l = fullContentStatistics.contentId;
        }
        if ((n & 2) != 0) {
            string = fullContentStatistics.contentName;
        }
        if ((n & 4) != 0) {
            timestamp = fullContentStatistics.createdAt;
        }
        if ((n & 8) != 0) {
            timestamp2 = fullContentStatistics.lastModifiedAt;
        }
        if ((n & 0x10) != 0) {
            l2 = fullContentStatistics.lastEventAt;
        }
        if ((n & 0x20) != 0) {
            l3 = fullContentStatistics.commentsCount;
        }
        if ((n & 0x40) != 0) {
            l4 = fullContentStatistics.viewCount;
        }
        if ((n & 0x80) != 0) {
            l5 = fullContentStatistics.usersViewed;
        }
        return fullContentStatistics.copy(l, string, timestamp, timestamp2, l2, l3, l4, l5);
    }

    @NotNull
    public String toString() {
        return "FullContentStatistics(contentId=" + this.contentId + ", contentName=" + this.contentName + ", createdAt=" + this.createdAt + ", lastModifiedAt=" + this.lastModifiedAt + ", lastEventAt=" + this.lastEventAt + ", commentsCount=" + this.commentsCount + ", viewCount=" + this.viewCount + ", usersViewed=" + this.usersViewed + ')';
    }

    public int hashCode() {
        int result = Long.hashCode(this.contentId);
        result = result * 31 + this.contentName.hashCode();
        result = result * 31 + (this.createdAt == null ? 0 : this.createdAt.hashCode());
        result = result * 31 + (this.lastModifiedAt == null ? 0 : this.lastModifiedAt.hashCode());
        result = result * 31 + (this.lastEventAt == null ? 0 : ((Object)this.lastEventAt).hashCode());
        result = result * 31 + Long.hashCode(this.commentsCount);
        result = result * 31 + Long.hashCode(this.viewCount);
        result = result * 31 + Long.hashCode(this.usersViewed);
        return result;
    }

    public boolean equals(@Nullable Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof FullContentStatistics)) {
            return false;
        }
        FullContentStatistics fullContentStatistics = (FullContentStatistics)other;
        if (this.contentId != fullContentStatistics.contentId) {
            return false;
        }
        if (!Intrinsics.areEqual((Object)this.contentName, (Object)fullContentStatistics.contentName)) {
            return false;
        }
        if (!Intrinsics.areEqual((Object)this.createdAt, (Object)fullContentStatistics.createdAt)) {
            return false;
        }
        if (!Intrinsics.areEqual((Object)this.lastModifiedAt, (Object)fullContentStatistics.lastModifiedAt)) {
            return false;
        }
        if (!Intrinsics.areEqual((Object)this.lastEventAt, (Object)fullContentStatistics.lastEventAt)) {
            return false;
        }
        if (this.commentsCount != fullContentStatistics.commentsCount) {
            return false;
        }
        if (this.viewCount != fullContentStatistics.viewCount) {
            return false;
        }
        return this.usersViewed == fullContentStatistics.usersViewed;
    }
}

