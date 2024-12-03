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

import java.time.Instant;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(mv={1, 9, 0}, k=1, xi=48, d1={"\u0000,\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\t\n\u0000\n\u0002\u0018\u0002\n\u0002\b\f\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000e\n\u0000\b\u0086\b\u0018\u00002\u00020\u0001B\u001d\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\u0006\u0010\u0006\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0007J\t\u0010\r\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\u000e\u001a\u00020\u0005H\u00c6\u0003J\t\u0010\u000f\u001a\u00020\u0003H\u00c6\u0003J'\u0010\u0010\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00052\b\b\u0002\u0010\u0006\u001a\u00020\u0003H\u00c6\u0001J\u0013\u0010\u0011\u001a\u00020\u00122\b\u0010\u0013\u001a\u0004\u0018\u00010\u0001H\u00d6\u0003J\t\u0010\u0014\u001a\u00020\u0015H\u00d6\u0001J\t\u0010\u0016\u001a\u00020\u0017H\u00d6\u0001R\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\b\u0010\tR\u0011\u0010\u0004\u001a\u00020\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b\n\u0010\u000bR\u0011\u0010\u0006\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\f\u0010\t\u00a8\u0006\u0018"}, d2={"Lcom/addonengine/addons/analytics/store/model/EventsByChildContentData;", "", "contentId", "", "lastViewedAt", "Ljava/time/Instant;", "views", "(JLjava/time/Instant;J)V", "getContentId", "()J", "getLastViewedAt", "()Ljava/time/Instant;", "getViews", "component1", "component2", "component3", "copy", "equals", "", "other", "hashCode", "", "toString", "", "analytics"})
public final class EventsByChildContentData {
    private final long contentId;
    @NotNull
    private final Instant lastViewedAt;
    private final long views;

    public EventsByChildContentData(long contentId, @NotNull Instant lastViewedAt, long views) {
        Intrinsics.checkNotNullParameter((Object)lastViewedAt, (String)"lastViewedAt");
        this.contentId = contentId;
        this.lastViewedAt = lastViewedAt;
        this.views = views;
    }

    public final long getContentId() {
        return this.contentId;
    }

    @NotNull
    public final Instant getLastViewedAt() {
        return this.lastViewedAt;
    }

    public final long getViews() {
        return this.views;
    }

    public final long component1() {
        return this.contentId;
    }

    @NotNull
    public final Instant component2() {
        return this.lastViewedAt;
    }

    public final long component3() {
        return this.views;
    }

    @NotNull
    public final EventsByChildContentData copy(long contentId, @NotNull Instant lastViewedAt, long views) {
        Intrinsics.checkNotNullParameter((Object)lastViewedAt, (String)"lastViewedAt");
        return new EventsByChildContentData(contentId, lastViewedAt, views);
    }

    public static /* synthetic */ EventsByChildContentData copy$default(EventsByChildContentData eventsByChildContentData, long l, Instant instant, long l2, int n, Object object) {
        if ((n & 1) != 0) {
            l = eventsByChildContentData.contentId;
        }
        if ((n & 2) != 0) {
            instant = eventsByChildContentData.lastViewedAt;
        }
        if ((n & 4) != 0) {
            l2 = eventsByChildContentData.views;
        }
        return eventsByChildContentData.copy(l, instant, l2);
    }

    @NotNull
    public String toString() {
        return "EventsByChildContentData(contentId=" + this.contentId + ", lastViewedAt=" + this.lastViewedAt + ", views=" + this.views + ')';
    }

    public int hashCode() {
        int result = Long.hashCode(this.contentId);
        result = result * 31 + this.lastViewedAt.hashCode();
        result = result * 31 + Long.hashCode(this.views);
        return result;
    }

    public boolean equals(@Nullable Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof EventsByChildContentData)) {
            return false;
        }
        EventsByChildContentData eventsByChildContentData = (EventsByChildContentData)other;
        if (this.contentId != eventsByChildContentData.contentId) {
            return false;
        }
        if (!Intrinsics.areEqual((Object)this.lastViewedAt, (Object)eventsByChildContentData.lastViewedAt)) {
            return false;
        }
        return this.views == eventsByChildContentData.views;
    }
}

