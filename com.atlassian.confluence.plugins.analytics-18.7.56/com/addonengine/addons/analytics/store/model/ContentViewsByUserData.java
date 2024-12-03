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

@Metadata(mv={1, 9, 0}, k=1, xi=48, d1={"\u00000\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\t\n\u0002\b\u000e\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0002\b\u0086\b\u0018\u00002\u00020\u0001B'\u0012\b\u0010\u0002\u001a\u0004\u0018\u00010\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\u0006\u0010\u0006\u001a\u00020\u0005\u0012\u0006\u0010\u0007\u001a\u00020\b\u00a2\u0006\u0002\u0010\tJ\u000b\u0010\u0011\u001a\u0004\u0018\u00010\u0003H\u00c6\u0003J\t\u0010\u0012\u001a\u00020\u0005H\u00c6\u0003J\t\u0010\u0013\u001a\u00020\u0005H\u00c6\u0003J\t\u0010\u0014\u001a\u00020\bH\u00c6\u0003J3\u0010\u0015\u001a\u00020\u00002\n\b\u0002\u0010\u0002\u001a\u0004\u0018\u00010\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00052\b\b\u0002\u0010\u0006\u001a\u00020\u00052\b\b\u0002\u0010\u0007\u001a\u00020\bH\u00c6\u0001J\u0013\u0010\u0016\u001a\u00020\u00172\b\u0010\u0018\u001a\u0004\u0018\u00010\u0001H\u00d6\u0003J\t\u0010\u0019\u001a\u00020\u001aH\u00d6\u0001J\t\u0010\u001b\u001a\u00020\u0003H\u00d6\u0001R\u0011\u0010\u0004\u001a\u00020\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b\n\u0010\u000bR\u0011\u0010\u0006\u001a\u00020\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b\f\u0010\u000bR\u0013\u0010\u0002\u001a\u0004\u0018\u00010\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\r\u0010\u000eR\u0011\u0010\u0007\u001a\u00020\b\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000f\u0010\u0010\u00a8\u0006\u001c"}, d2={"Lcom/addonengine/addons/analytics/store/model/ContentViewsByUserData;", "", "userKey", "", "lastVersionViewedModificationDate", "Ljava/time/Instant;", "lastViewedAt", "views", "", "(Ljava/lang/String;Ljava/time/Instant;Ljava/time/Instant;J)V", "getLastVersionViewedModificationDate", "()Ljava/time/Instant;", "getLastViewedAt", "getUserKey", "()Ljava/lang/String;", "getViews", "()J", "component1", "component2", "component3", "component4", "copy", "equals", "", "other", "hashCode", "", "toString", "analytics"})
public final class ContentViewsByUserData {
    @Nullable
    private final String userKey;
    @NotNull
    private final Instant lastVersionViewedModificationDate;
    @NotNull
    private final Instant lastViewedAt;
    private final long views;

    public ContentViewsByUserData(@Nullable String userKey, @NotNull Instant lastVersionViewedModificationDate, @NotNull Instant lastViewedAt, long views) {
        Intrinsics.checkNotNullParameter((Object)lastVersionViewedModificationDate, (String)"lastVersionViewedModificationDate");
        Intrinsics.checkNotNullParameter((Object)lastViewedAt, (String)"lastViewedAt");
        this.userKey = userKey;
        this.lastVersionViewedModificationDate = lastVersionViewedModificationDate;
        this.lastViewedAt = lastViewedAt;
        this.views = views;
    }

    @Nullable
    public final String getUserKey() {
        return this.userKey;
    }

    @NotNull
    public final Instant getLastVersionViewedModificationDate() {
        return this.lastVersionViewedModificationDate;
    }

    @NotNull
    public final Instant getLastViewedAt() {
        return this.lastViewedAt;
    }

    public final long getViews() {
        return this.views;
    }

    @Nullable
    public final String component1() {
        return this.userKey;
    }

    @NotNull
    public final Instant component2() {
        return this.lastVersionViewedModificationDate;
    }

    @NotNull
    public final Instant component3() {
        return this.lastViewedAt;
    }

    public final long component4() {
        return this.views;
    }

    @NotNull
    public final ContentViewsByUserData copy(@Nullable String userKey, @NotNull Instant lastVersionViewedModificationDate, @NotNull Instant lastViewedAt, long views) {
        Intrinsics.checkNotNullParameter((Object)lastVersionViewedModificationDate, (String)"lastVersionViewedModificationDate");
        Intrinsics.checkNotNullParameter((Object)lastViewedAt, (String)"lastViewedAt");
        return new ContentViewsByUserData(userKey, lastVersionViewedModificationDate, lastViewedAt, views);
    }

    public static /* synthetic */ ContentViewsByUserData copy$default(ContentViewsByUserData contentViewsByUserData, String string, Instant instant, Instant instant2, long l, int n, Object object) {
        if ((n & 1) != 0) {
            string = contentViewsByUserData.userKey;
        }
        if ((n & 2) != 0) {
            instant = contentViewsByUserData.lastVersionViewedModificationDate;
        }
        if ((n & 4) != 0) {
            instant2 = contentViewsByUserData.lastViewedAt;
        }
        if ((n & 8) != 0) {
            l = contentViewsByUserData.views;
        }
        return contentViewsByUserData.copy(string, instant, instant2, l);
    }

    @NotNull
    public String toString() {
        return "ContentViewsByUserData(userKey=" + this.userKey + ", lastVersionViewedModificationDate=" + this.lastVersionViewedModificationDate + ", lastViewedAt=" + this.lastViewedAt + ", views=" + this.views + ')';
    }

    public int hashCode() {
        int result = this.userKey == null ? 0 : this.userKey.hashCode();
        result = result * 31 + this.lastVersionViewedModificationDate.hashCode();
        result = result * 31 + this.lastViewedAt.hashCode();
        result = result * 31 + Long.hashCode(this.views);
        return result;
    }

    public boolean equals(@Nullable Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof ContentViewsByUserData)) {
            return false;
        }
        ContentViewsByUserData contentViewsByUserData = (ContentViewsByUserData)other;
        if (!Intrinsics.areEqual((Object)this.userKey, (Object)contentViewsByUserData.userKey)) {
            return false;
        }
        if (!Intrinsics.areEqual((Object)this.lastVersionViewedModificationDate, (Object)contentViewsByUserData.lastVersionViewedModificationDate)) {
            return false;
        }
        if (!Intrinsics.areEqual((Object)this.lastViewedAt, (Object)contentViewsByUserData.lastViewedAt)) {
            return false;
        }
        return this.views == contentViewsByUserData.views;
    }
}

