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

import com.addonengine.addons.analytics.service.confluence.model.Space;
import java.time.Instant;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(mv={1, 9, 0}, k=1, xi=48, d1={"\u00004\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\t\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0017\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000e\n\u0000\b\u0086\b\u0018\u00002\u00020\u0001B;\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\u0006\u0010\u0006\u001a\u00020\u0005\u0012\b\u0010\u0007\u001a\u0004\u0018\u00010\b\u0012\b\u0010\t\u001a\u0004\u0018\u00010\u0005\u0012\b\u0010\n\u001a\u0004\u0018\u00010\u0005\u00a2\u0006\u0002\u0010\u000bJ\t\u0010\u0017\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\u0018\u001a\u00020\u0005H\u00c6\u0003J\t\u0010\u0019\u001a\u00020\u0005H\u00c6\u0003J\u000b\u0010\u001a\u001a\u0004\u0018\u00010\bH\u00c6\u0003J\u0010\u0010\u001b\u001a\u0004\u0018\u00010\u0005H\u00c6\u0003\u00a2\u0006\u0002\u0010\u0014J\u0010\u0010\u001c\u001a\u0004\u0018\u00010\u0005H\u00c6\u0003\u00a2\u0006\u0002\u0010\u0014JP\u0010\u001d\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00052\b\b\u0002\u0010\u0006\u001a\u00020\u00052\n\b\u0002\u0010\u0007\u001a\u0004\u0018\u00010\b2\n\b\u0002\u0010\t\u001a\u0004\u0018\u00010\u00052\n\b\u0002\u0010\n\u001a\u0004\u0018\u00010\u0005H\u00c6\u0001\u00a2\u0006\u0002\u0010\u001eJ\u0013\u0010\u001f\u001a\u00020 2\b\u0010!\u001a\u0004\u0018\u00010\u0001H\u00d6\u0003J\t\u0010\"\u001a\u00020#H\u00d6\u0001J\t\u0010$\u001a\u00020%H\u00d6\u0001R\u0011\u0010\u0004\u001a\u00020\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b\f\u0010\rR\u0013\u0010\u0007\u001a\u0004\u0018\u00010\b\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000e\u0010\u000fR\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0010\u0010\u0011R\u0011\u0010\u0006\u001a\u00020\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0012\u0010\rR\u0015\u0010\t\u001a\u0004\u0018\u00010\u0005\u00a2\u0006\n\n\u0002\u0010\u0015\u001a\u0004\b\u0013\u0010\u0014R\u0015\u0010\n\u001a\u0004\u0018\u00010\u0005\u00a2\u0006\n\n\u0002\u0010\u0015\u001a\u0004\b\u0016\u0010\u0014\u00a8\u0006&"}, d2={"Lcom/addonengine/addons/analytics/service/model/SpaceActivity;", "", "space", "Lcom/addonengine/addons/analytics/service/confluence/model/Space;", "created", "", "updated", "lastViewedAt", "Ljava/time/Instant;", "usersViewed", "views", "(Lcom/addonengine/addons/analytics/service/confluence/model/Space;JJLjava/time/Instant;Ljava/lang/Long;Ljava/lang/Long;)V", "getCreated", "()J", "getLastViewedAt", "()Ljava/time/Instant;", "getSpace", "()Lcom/addonengine/addons/analytics/service/confluence/model/Space;", "getUpdated", "getUsersViewed", "()Ljava/lang/Long;", "Ljava/lang/Long;", "getViews", "component1", "component2", "component3", "component4", "component5", "component6", "copy", "(Lcom/addonengine/addons/analytics/service/confluence/model/Space;JJLjava/time/Instant;Ljava/lang/Long;Ljava/lang/Long;)Lcom/addonengine/addons/analytics/service/model/SpaceActivity;", "equals", "", "other", "hashCode", "", "toString", "", "analytics"})
public final class SpaceActivity {
    @NotNull
    private final Space space;
    private final long created;
    private final long updated;
    @Nullable
    private final Instant lastViewedAt;
    @Nullable
    private final Long usersViewed;
    @Nullable
    private final Long views;

    public SpaceActivity(@NotNull Space space, long created, long updated, @Nullable Instant lastViewedAt, @Nullable Long usersViewed, @Nullable Long views) {
        Intrinsics.checkNotNullParameter((Object)space, (String)"space");
        this.space = space;
        this.created = created;
        this.updated = updated;
        this.lastViewedAt = lastViewedAt;
        this.usersViewed = usersViewed;
        this.views = views;
    }

    @NotNull
    public final Space getSpace() {
        return this.space;
    }

    public final long getCreated() {
        return this.created;
    }

    public final long getUpdated() {
        return this.updated;
    }

    @Nullable
    public final Instant getLastViewedAt() {
        return this.lastViewedAt;
    }

    @Nullable
    public final Long getUsersViewed() {
        return this.usersViewed;
    }

    @Nullable
    public final Long getViews() {
        return this.views;
    }

    @NotNull
    public final Space component1() {
        return this.space;
    }

    public final long component2() {
        return this.created;
    }

    public final long component3() {
        return this.updated;
    }

    @Nullable
    public final Instant component4() {
        return this.lastViewedAt;
    }

    @Nullable
    public final Long component5() {
        return this.usersViewed;
    }

    @Nullable
    public final Long component6() {
        return this.views;
    }

    @NotNull
    public final SpaceActivity copy(@NotNull Space space, long created, long updated, @Nullable Instant lastViewedAt, @Nullable Long usersViewed, @Nullable Long views) {
        Intrinsics.checkNotNullParameter((Object)space, (String)"space");
        return new SpaceActivity(space, created, updated, lastViewedAt, usersViewed, views);
    }

    public static /* synthetic */ SpaceActivity copy$default(SpaceActivity spaceActivity, Space space, long l, long l2, Instant instant, Long l3, Long l4, int n, Object object) {
        if ((n & 1) != 0) {
            space = spaceActivity.space;
        }
        if ((n & 2) != 0) {
            l = spaceActivity.created;
        }
        if ((n & 4) != 0) {
            l2 = spaceActivity.updated;
        }
        if ((n & 8) != 0) {
            instant = spaceActivity.lastViewedAt;
        }
        if ((n & 0x10) != 0) {
            l3 = spaceActivity.usersViewed;
        }
        if ((n & 0x20) != 0) {
            l4 = spaceActivity.views;
        }
        return spaceActivity.copy(space, l, l2, instant, l3, l4);
    }

    @NotNull
    public String toString() {
        return "SpaceActivity(space=" + this.space + ", created=" + this.created + ", updated=" + this.updated + ", lastViewedAt=" + this.lastViewedAt + ", usersViewed=" + this.usersViewed + ", views=" + this.views + ')';
    }

    public int hashCode() {
        int result = this.space.hashCode();
        result = result * 31 + Long.hashCode(this.created);
        result = result * 31 + Long.hashCode(this.updated);
        result = result * 31 + (this.lastViewedAt == null ? 0 : this.lastViewedAt.hashCode());
        result = result * 31 + (this.usersViewed == null ? 0 : ((Object)this.usersViewed).hashCode());
        result = result * 31 + (this.views == null ? 0 : ((Object)this.views).hashCode());
        return result;
    }

    public boolean equals(@Nullable Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof SpaceActivity)) {
            return false;
        }
        SpaceActivity spaceActivity = (SpaceActivity)other;
        if (!Intrinsics.areEqual((Object)this.space, (Object)spaceActivity.space)) {
            return false;
        }
        if (this.created != spaceActivity.created) {
            return false;
        }
        if (this.updated != spaceActivity.updated) {
            return false;
        }
        if (!Intrinsics.areEqual((Object)this.lastViewedAt, (Object)spaceActivity.lastViewedAt)) {
            return false;
        }
        if (!Intrinsics.areEqual((Object)this.usersViewed, (Object)spaceActivity.usersViewed)) {
            return false;
        }
        return Intrinsics.areEqual((Object)this.views, (Object)spaceActivity.views);
    }
}

