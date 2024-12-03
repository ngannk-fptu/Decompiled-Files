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

@Metadata(mv={1, 9, 0}, k=1, xi=48, d1={"\u0000.\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\t\n\u0002\b\u000f\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0002\b\u0086\b\u0018\u00002\u00020\u0001B%\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\u0006\u0010\u0006\u001a\u00020\u0007\u0012\u0006\u0010\b\u001a\u00020\u0007\u00a2\u0006\u0002\u0010\tJ\t\u0010\u0011\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\u0012\u001a\u00020\u0005H\u00c6\u0003J\t\u0010\u0013\u001a\u00020\u0007H\u00c6\u0003J\t\u0010\u0014\u001a\u00020\u0007H\u00c6\u0003J1\u0010\u0015\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00052\b\b\u0002\u0010\u0006\u001a\u00020\u00072\b\b\u0002\u0010\b\u001a\u00020\u0007H\u00c6\u0001J\u0013\u0010\u0016\u001a\u00020\u00172\b\u0010\u0018\u001a\u0004\u0018\u00010\u0001H\u00d6\u0003J\t\u0010\u0019\u001a\u00020\u001aH\u00d6\u0001J\t\u0010\u001b\u001a\u00020\u0003H\u00d6\u0001R\u0011\u0010\u0004\u001a\u00020\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b\n\u0010\u000bR\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\f\u0010\rR\u0011\u0010\b\u001a\u00020\u0007\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000e\u0010\u000fR\u0011\u0010\u0006\u001a\u00020\u0007\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0010\u0010\u000f\u00a8\u0006\u001c"}, d2={"Lcom/addonengine/addons/analytics/store/model/EventsBySpaceWithUniqueUsersData;", "", "spaceKey", "", "lastEventAt", "Ljava/time/Instant;", "uniqueUsers", "", "total", "(Ljava/lang/String;Ljava/time/Instant;JJ)V", "getLastEventAt", "()Ljava/time/Instant;", "getSpaceKey", "()Ljava/lang/String;", "getTotal", "()J", "getUniqueUsers", "component1", "component2", "component3", "component4", "copy", "equals", "", "other", "hashCode", "", "toString", "analytics"})
public final class EventsBySpaceWithUniqueUsersData {
    @NotNull
    private final String spaceKey;
    @NotNull
    private final Instant lastEventAt;
    private final long uniqueUsers;
    private final long total;

    public EventsBySpaceWithUniqueUsersData(@NotNull String spaceKey, @NotNull Instant lastEventAt, long uniqueUsers, long total) {
        Intrinsics.checkNotNullParameter((Object)spaceKey, (String)"spaceKey");
        Intrinsics.checkNotNullParameter((Object)lastEventAt, (String)"lastEventAt");
        this.spaceKey = spaceKey;
        this.lastEventAt = lastEventAt;
        this.uniqueUsers = uniqueUsers;
        this.total = total;
    }

    @NotNull
    public final String getSpaceKey() {
        return this.spaceKey;
    }

    @NotNull
    public final Instant getLastEventAt() {
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

    @NotNull
    public final Instant component2() {
        return this.lastEventAt;
    }

    public final long component3() {
        return this.uniqueUsers;
    }

    public final long component4() {
        return this.total;
    }

    @NotNull
    public final EventsBySpaceWithUniqueUsersData copy(@NotNull String spaceKey, @NotNull Instant lastEventAt, long uniqueUsers, long total) {
        Intrinsics.checkNotNullParameter((Object)spaceKey, (String)"spaceKey");
        Intrinsics.checkNotNullParameter((Object)lastEventAt, (String)"lastEventAt");
        return new EventsBySpaceWithUniqueUsersData(spaceKey, lastEventAt, uniqueUsers, total);
    }

    public static /* synthetic */ EventsBySpaceWithUniqueUsersData copy$default(EventsBySpaceWithUniqueUsersData eventsBySpaceWithUniqueUsersData, String string, Instant instant, long l, long l2, int n, Object object) {
        if ((n & 1) != 0) {
            string = eventsBySpaceWithUniqueUsersData.spaceKey;
        }
        if ((n & 2) != 0) {
            instant = eventsBySpaceWithUniqueUsersData.lastEventAt;
        }
        if ((n & 4) != 0) {
            l = eventsBySpaceWithUniqueUsersData.uniqueUsers;
        }
        if ((n & 8) != 0) {
            l2 = eventsBySpaceWithUniqueUsersData.total;
        }
        return eventsBySpaceWithUniqueUsersData.copy(string, instant, l, l2);
    }

    @NotNull
    public String toString() {
        return "EventsBySpaceWithUniqueUsersData(spaceKey=" + this.spaceKey + ", lastEventAt=" + this.lastEventAt + ", uniqueUsers=" + this.uniqueUsers + ", total=" + this.total + ')';
    }

    public int hashCode() {
        int result = this.spaceKey.hashCode();
        result = result * 31 + this.lastEventAt.hashCode();
        result = result * 31 + Long.hashCode(this.uniqueUsers);
        result = result * 31 + Long.hashCode(this.total);
        return result;
    }

    public boolean equals(@Nullable Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof EventsBySpaceWithUniqueUsersData)) {
            return false;
        }
        EventsBySpaceWithUniqueUsersData eventsBySpaceWithUniqueUsersData = (EventsBySpaceWithUniqueUsersData)other;
        if (!Intrinsics.areEqual((Object)this.spaceKey, (Object)eventsBySpaceWithUniqueUsersData.spaceKey)) {
            return false;
        }
        if (!Intrinsics.areEqual((Object)this.lastEventAt, (Object)eventsBySpaceWithUniqueUsersData.lastEventAt)) {
            return false;
        }
        if (this.uniqueUsers != eventsBySpaceWithUniqueUsersData.uniqueUsers) {
            return false;
        }
        return this.total == eventsBySpaceWithUniqueUsersData.total;
    }
}

