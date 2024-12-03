/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  kotlin.Metadata
 *  kotlin.jvm.internal.Intrinsics
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package com.addonengine.addons.analytics.store.server;

import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(mv={1, 9, 0}, k=1, xi=48, d1={"\u0000(\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\t\n\u0002\b\u000f\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0002\b\u0086\b\u0018\u00002\u00020\u0001B%\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\u0006\u0010\u0006\u001a\u00020\u0005\u0012\u0006\u0010\u0007\u001a\u00020\u0005\u00a2\u0006\u0002\u0010\bJ\t\u0010\u000f\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\u0010\u001a\u00020\u0005H\u00c6\u0003J\t\u0010\u0011\u001a\u00020\u0005H\u00c6\u0003J\t\u0010\u0012\u001a\u00020\u0005H\u00c6\u0003J1\u0010\u0013\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00052\b\b\u0002\u0010\u0006\u001a\u00020\u00052\b\b\u0002\u0010\u0007\u001a\u00020\u0005H\u00c6\u0001J\u0013\u0010\u0014\u001a\u00020\u00152\b\u0010\u0016\u001a\u0004\u0018\u00010\u0001H\u00d6\u0003J\t\u0010\u0017\u001a\u00020\u0018H\u00d6\u0001J\t\u0010\u0019\u001a\u00020\u0003H\u00d6\u0001R\u0011\u0010\u0004\u001a\u00020\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b\t\u0010\nR\u0011\u0010\u0006\u001a\u00020\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000b\u0010\nR\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\f\u0010\rR\u0011\u0010\u0007\u001a\u00020\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000e\u0010\n\u00a8\u0006\u001a"}, d2={"Lcom/addonengine/addons/analytics/store/server/UserViewsData;", "", "userKey", "", "lastVersionViewedModificationDate", "", "lastViewedAt", "views", "(Ljava/lang/String;JJJ)V", "getLastVersionViewedModificationDate", "()J", "getLastViewedAt", "getUserKey", "()Ljava/lang/String;", "getViews", "component1", "component2", "component3", "component4", "copy", "equals", "", "other", "hashCode", "", "toString", "analytics"})
public final class UserViewsData {
    @NotNull
    private final String userKey;
    private final long lastVersionViewedModificationDate;
    private final long lastViewedAt;
    private final long views;

    public UserViewsData(@NotNull String userKey, long lastVersionViewedModificationDate, long lastViewedAt, long views) {
        Intrinsics.checkNotNullParameter((Object)userKey, (String)"userKey");
        this.userKey = userKey;
        this.lastVersionViewedModificationDate = lastVersionViewedModificationDate;
        this.lastViewedAt = lastViewedAt;
        this.views = views;
    }

    @NotNull
    public final String getUserKey() {
        return this.userKey;
    }

    public final long getLastVersionViewedModificationDate() {
        return this.lastVersionViewedModificationDate;
    }

    public final long getLastViewedAt() {
        return this.lastViewedAt;
    }

    public final long getViews() {
        return this.views;
    }

    @NotNull
    public final String component1() {
        return this.userKey;
    }

    public final long component2() {
        return this.lastVersionViewedModificationDate;
    }

    public final long component3() {
        return this.lastViewedAt;
    }

    public final long component4() {
        return this.views;
    }

    @NotNull
    public final UserViewsData copy(@NotNull String userKey, long lastVersionViewedModificationDate, long lastViewedAt, long views) {
        Intrinsics.checkNotNullParameter((Object)userKey, (String)"userKey");
        return new UserViewsData(userKey, lastVersionViewedModificationDate, lastViewedAt, views);
    }

    public static /* synthetic */ UserViewsData copy$default(UserViewsData userViewsData, String string, long l, long l2, long l3, int n, Object object) {
        if ((n & 1) != 0) {
            string = userViewsData.userKey;
        }
        if ((n & 2) != 0) {
            l = userViewsData.lastVersionViewedModificationDate;
        }
        if ((n & 4) != 0) {
            l2 = userViewsData.lastViewedAt;
        }
        if ((n & 8) != 0) {
            l3 = userViewsData.views;
        }
        return userViewsData.copy(string, l, l2, l3);
    }

    @NotNull
    public String toString() {
        return "UserViewsData(userKey=" + this.userKey + ", lastVersionViewedModificationDate=" + this.lastVersionViewedModificationDate + ", lastViewedAt=" + this.lastViewedAt + ", views=" + this.views + ')';
    }

    public int hashCode() {
        int result = this.userKey.hashCode();
        result = result * 31 + Long.hashCode(this.lastVersionViewedModificationDate);
        result = result * 31 + Long.hashCode(this.lastViewedAt);
        result = result * 31 + Long.hashCode(this.views);
        return result;
    }

    public boolean equals(@Nullable Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof UserViewsData)) {
            return false;
        }
        UserViewsData userViewsData = (UserViewsData)other;
        if (!Intrinsics.areEqual((Object)this.userKey, (Object)userViewsData.userKey)) {
            return false;
        }
        if (this.lastVersionViewedModificationDate != userViewsData.lastVersionViewedModificationDate) {
            return false;
        }
        if (this.lastViewedAt != userViewsData.lastViewedAt) {
            return false;
        }
        return this.views == userViewsData.views;
    }
}

