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

@Metadata(mv={1, 9, 0}, k=1, xi=48, d1={"\u0000(\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\t\n\u0002\b\u000f\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0002\b\u0086\b\u0018\u00002\u00020\u0001B%\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\u0006\u0010\u0006\u001a\u00020\u0005\u0012\u0006\u0010\u0007\u001a\u00020\u0005\u00a2\u0006\u0002\u0010\bJ\t\u0010\u000f\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\u0010\u001a\u00020\u0005H\u00c6\u0003J\t\u0010\u0011\u001a\u00020\u0005H\u00c6\u0003J\t\u0010\u0012\u001a\u00020\u0005H\u00c6\u0003J1\u0010\u0013\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00052\b\b\u0002\u0010\u0006\u001a\u00020\u00052\b\b\u0002\u0010\u0007\u001a\u00020\u0005H\u00c6\u0001J\u0013\u0010\u0014\u001a\u00020\u00152\b\u0010\u0016\u001a\u0004\u0018\u00010\u0001H\u00d6\u0003J\t\u0010\u0017\u001a\u00020\u0018H\u00d6\u0001J\t\u0010\u0019\u001a\u00020\u0003H\u00d6\u0001R\u0011\u0010\u0004\u001a\u00020\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b\t\u0010\nR\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000b\u0010\fR\u0011\u0010\u0007\u001a\u00020\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b\r\u0010\nR\u0011\u0010\u0006\u001a\u00020\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000e\u0010\n\u00a8\u0006\u001a"}, d2={"Lcom/addonengine/addons/analytics/store/server/SpaceActivityWithUniqueUsersData;", "", "spaceKey", "", "lastEventAt", "", "uniqueUsers", "total", "(Ljava/lang/String;JJJ)V", "getLastEventAt", "()J", "getSpaceKey", "()Ljava/lang/String;", "getTotal", "getUniqueUsers", "component1", "component2", "component3", "component4", "copy", "equals", "", "other", "hashCode", "", "toString", "analytics"})
public final class SpaceActivityWithUniqueUsersData {
    @NotNull
    private final String spaceKey;
    private final long lastEventAt;
    private final long uniqueUsers;
    private final long total;

    public SpaceActivityWithUniqueUsersData(@NotNull String spaceKey, long lastEventAt, long uniqueUsers, long total) {
        Intrinsics.checkNotNullParameter((Object)spaceKey, (String)"spaceKey");
        this.spaceKey = spaceKey;
        this.lastEventAt = lastEventAt;
        this.uniqueUsers = uniqueUsers;
        this.total = total;
    }

    @NotNull
    public final String getSpaceKey() {
        return this.spaceKey;
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

    @NotNull
    public final String component1() {
        return this.spaceKey;
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
    public final SpaceActivityWithUniqueUsersData copy(@NotNull String spaceKey, long lastEventAt, long uniqueUsers, long total) {
        Intrinsics.checkNotNullParameter((Object)spaceKey, (String)"spaceKey");
        return new SpaceActivityWithUniqueUsersData(spaceKey, lastEventAt, uniqueUsers, total);
    }

    public static /* synthetic */ SpaceActivityWithUniqueUsersData copy$default(SpaceActivityWithUniqueUsersData spaceActivityWithUniqueUsersData, String string, long l, long l2, long l3, int n, Object object) {
        if ((n & 1) != 0) {
            string = spaceActivityWithUniqueUsersData.spaceKey;
        }
        if ((n & 2) != 0) {
            l = spaceActivityWithUniqueUsersData.lastEventAt;
        }
        if ((n & 4) != 0) {
            l2 = spaceActivityWithUniqueUsersData.uniqueUsers;
        }
        if ((n & 8) != 0) {
            l3 = spaceActivityWithUniqueUsersData.total;
        }
        return spaceActivityWithUniqueUsersData.copy(string, l, l2, l3);
    }

    @NotNull
    public String toString() {
        return "SpaceActivityWithUniqueUsersData(spaceKey=" + this.spaceKey + ", lastEventAt=" + this.lastEventAt + ", uniqueUsers=" + this.uniqueUsers + ", total=" + this.total + ')';
    }

    public int hashCode() {
        int result = this.spaceKey.hashCode();
        result = result * 31 + Long.hashCode(this.lastEventAt);
        result = result * 31 + Long.hashCode(this.uniqueUsers);
        result = result * 31 + Long.hashCode(this.total);
        return result;
    }

    public boolean equals(@Nullable Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof SpaceActivityWithUniqueUsersData)) {
            return false;
        }
        SpaceActivityWithUniqueUsersData spaceActivityWithUniqueUsersData = (SpaceActivityWithUniqueUsersData)other;
        if (!Intrinsics.areEqual((Object)this.spaceKey, (Object)spaceActivityWithUniqueUsersData.spaceKey)) {
            return false;
        }
        if (this.lastEventAt != spaceActivityWithUniqueUsersData.lastEventAt) {
            return false;
        }
        if (this.uniqueUsers != spaceActivityWithUniqueUsersData.uniqueUsers) {
            return false;
        }
        return this.total == spaceActivityWithUniqueUsersData.total;
    }
}

