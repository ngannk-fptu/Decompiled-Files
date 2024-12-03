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

@Metadata(mv={1, 9, 0}, k=1, xi=48, d1={"\u0000&\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\t\n\u0002\b\f\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000e\n\u0000\b\u0086\b\u0018\u00002\u00020\u0001B\u001d\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0003\u0012\u0006\u0010\u0005\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0006J\t\u0010\u000b\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\f\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\r\u001a\u00020\u0003H\u00c6\u0003J'\u0010\u000e\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00032\b\b\u0002\u0010\u0005\u001a\u00020\u0003H\u00c6\u0001J\u0013\u0010\u000f\u001a\u00020\u00102\b\u0010\u0011\u001a\u0004\u0018\u00010\u0001H\u00d6\u0003J\t\u0010\u0012\u001a\u00020\u0013H\u00d6\u0001J\t\u0010\u0014\u001a\u00020\u0015H\u00d6\u0001R\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0007\u0010\bR\u0011\u0010\u0004\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\t\u0010\bR\u0011\u0010\u0005\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\n\u0010\b\u00a8\u0006\u0016"}, d2={"Lcom/addonengine/addons/analytics/store/server/ChildContentActivityData;", "", "contentId", "", "lastViewedAt", "views", "(JJJ)V", "getContentId", "()J", "getLastViewedAt", "getViews", "component1", "component2", "component3", "copy", "equals", "", "other", "hashCode", "", "toString", "", "analytics"})
public final class ChildContentActivityData {
    private final long contentId;
    private final long lastViewedAt;
    private final long views;

    public ChildContentActivityData(long contentId, long lastViewedAt, long views) {
        this.contentId = contentId;
        this.lastViewedAt = lastViewedAt;
        this.views = views;
    }

    public final long getContentId() {
        return this.contentId;
    }

    public final long getLastViewedAt() {
        return this.lastViewedAt;
    }

    public final long getViews() {
        return this.views;
    }

    public final long component1() {
        return this.contentId;
    }

    public final long component2() {
        return this.lastViewedAt;
    }

    public final long component3() {
        return this.views;
    }

    @NotNull
    public final ChildContentActivityData copy(long contentId, long lastViewedAt, long views) {
        return new ChildContentActivityData(contentId, lastViewedAt, views);
    }

    public static /* synthetic */ ChildContentActivityData copy$default(ChildContentActivityData childContentActivityData, long l, long l2, long l3, int n, Object object) {
        if ((n & 1) != 0) {
            l = childContentActivityData.contentId;
        }
        if ((n & 2) != 0) {
            l2 = childContentActivityData.lastViewedAt;
        }
        if ((n & 4) != 0) {
            l3 = childContentActivityData.views;
        }
        return childContentActivityData.copy(l, l2, l3);
    }

    @NotNull
    public String toString() {
        return "ChildContentActivityData(contentId=" + this.contentId + ", lastViewedAt=" + this.lastViewedAt + ", views=" + this.views + ')';
    }

    public int hashCode() {
        int result = Long.hashCode(this.contentId);
        result = result * 31 + Long.hashCode(this.lastViewedAt);
        result = result * 31 + Long.hashCode(this.views);
        return result;
    }

    public boolean equals(@Nullable Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof ChildContentActivityData)) {
            return false;
        }
        ChildContentActivityData childContentActivityData = (ChildContentActivityData)other;
        if (this.contentId != childContentActivityData.contentId) {
            return false;
        }
        if (this.lastViewedAt != childContentActivityData.lastViewedAt) {
            return false;
        }
        return this.views == childContentActivityData.views;
    }
}

