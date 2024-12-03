/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  kotlin.Metadata
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package com.addonengine.addons.analytics.store.server;

import kotlin.Metadata;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(mv={1, 9, 0}, k=1, xi=48, d1={"\u0000&\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\t\n\u0002\b\u000f\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000e\n\u0000\b\u0086\b\u0018\u00002\u00020\u0001B%\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0003\u0012\u0006\u0010\u0005\u001a\u00020\u0003\u0012\u0006\u0010\u0006\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0007J\t\u0010\r\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\u000e\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\u000f\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\u0010\u001a\u00020\u0003H\u00c6\u0003J1\u0010\u0011\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00032\b\b\u0002\u0010\u0005\u001a\u00020\u00032\b\b\u0002\u0010\u0006\u001a\u00020\u0003H\u00c6\u0001J\u0013\u0010\u0012\u001a\u00020\u00132\b\u0010\u0014\u001a\u0004\u0018\u00010\u0001H\u00d6\u0003J\t\u0010\u0015\u001a\u00020\u0016H\u00d6\u0001J\t\u0010\u0017\u001a\u00020\u0018H\u00d6\u0001R\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\b\u0010\tR\u0011\u0010\u0004\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\n\u0010\tR\u0011\u0010\u0006\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000b\u0010\tR\u0011\u0010\u0005\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\f\u0010\t\u00a8\u0006\u0019"}, d2={"Lcom/addonengine/addons/analytics/store/server/ContentActivityWithUniqueUsersData;", "", "contentId", "", "lastEventAt", "uniqueUsers", "total", "(JJJJ)V", "getContentId", "()J", "getLastEventAt", "getTotal", "getUniqueUsers", "component1", "component2", "component3", "component4", "copy", "equals", "", "other", "hashCode", "", "toString", "", "analytics"})
public final class ContentActivityWithUniqueUsersData {
    private final long contentId;
    private final long lastEventAt;
    private final long uniqueUsers;
    private final long total;

    public ContentActivityWithUniqueUsersData(long contentId, long lastEventAt, long uniqueUsers, long total) {
        this.contentId = contentId;
        this.lastEventAt = lastEventAt;
        this.uniqueUsers = uniqueUsers;
        this.total = total;
    }

    public final long getContentId() {
        return this.contentId;
    }

    public final long getLastEventAt() {
        return this.lastEventAt;
    }

    public final long getUniqueUsers() {
        return this.uniqueUsers;
    }

    public final long getTotal() {
        return this.total;
    }

    public final long component1() {
        return this.contentId;
    }

    public final long component2() {
        return this.lastEventAt;
    }

    public final long component3() {
        return this.uniqueUsers;
    }

    public final long component4() {
        return this.total;
    }

    @NotNull
    public final ContentActivityWithUniqueUsersData copy(long contentId, long lastEventAt, long uniqueUsers, long total) {
        return new ContentActivityWithUniqueUsersData(contentId, lastEventAt, uniqueUsers, total);
    }

    public static /* synthetic */ ContentActivityWithUniqueUsersData copy$default(ContentActivityWithUniqueUsersData contentActivityWithUniqueUsersData, long l, long l2, long l3, long l4, int n, Object object) {
        if ((n & 1) != 0) {
            l = contentActivityWithUniqueUsersData.contentId;
        }
        if ((n & 2) != 0) {
            l2 = contentActivityWithUniqueUsersData.lastEventAt;
        }
        if ((n & 4) != 0) {
            l3 = contentActivityWithUniqueUsersData.uniqueUsers;
        }
        if ((n & 8) != 0) {
            l4 = contentActivityWithUniqueUsersData.total;
        }
        return contentActivityWithUniqueUsersData.copy(l, l2, l3, l4);
    }

    @NotNull
    public String toString() {
        return "ContentActivityWithUniqueUsersData(contentId=" + this.contentId + ", lastEventAt=" + this.lastEventAt + ", uniqueUsers=" + this.uniqueUsers + ", total=" + this.total + ')';
    }

    public int hashCode() {
        int result = Long.hashCode(this.contentId);
        result = result * 31 + Long.hashCode(this.lastEventAt);
        result = result * 31 + Long.hashCode(this.uniqueUsers);
        result = result * 31 + Long.hashCode(this.total);
        return result;
    }

    public boolean equals(@Nullable Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof ContentActivityWithUniqueUsersData)) {
            return false;
        }
        ContentActivityWithUniqueUsersData contentActivityWithUniqueUsersData = (ContentActivityWithUniqueUsersData)other;
        if (this.contentId != contentActivityWithUniqueUsersData.contentId) {
            return false;
        }
        if (this.lastEventAt != contentActivityWithUniqueUsersData.lastEventAt) {
            return false;
        }
        if (this.uniqueUsers != contentActivityWithUniqueUsersData.uniqueUsers) {
            return false;
        }
        return this.total == contentActivityWithUniqueUsersData.total;
    }
}

