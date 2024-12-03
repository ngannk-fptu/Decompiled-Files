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

import com.addonengine.addons.analytics.service.confluence.model.ContentVersion;
import com.addonengine.addons.analytics.service.confluence.model.UserType;
import java.time.Instant;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(mv={1, 9, 0}, k=1, xi=48, d1={"\u0000:\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\t\n\u0002\b\u0012\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0002\b\u0086\b\u0018\u00002\u00020\u0001B1\u0012\b\u0010\u0002\u001a\u0004\u0018\u00010\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\b\u0010\u0006\u001a\u0004\u0018\u00010\u0007\u0012\u0006\u0010\b\u001a\u00020\t\u0012\u0006\u0010\n\u001a\u00020\u000b\u00a2\u0006\u0002\u0010\fJ\u000b\u0010\u0017\u001a\u0004\u0018\u00010\u0003H\u00c6\u0003J\t\u0010\u0018\u001a\u00020\u0005H\u00c6\u0003J\u000b\u0010\u0019\u001a\u0004\u0018\u00010\u0007H\u00c6\u0003J\t\u0010\u001a\u001a\u00020\tH\u00c6\u0003J\t\u0010\u001b\u001a\u00020\u000bH\u00c6\u0003J?\u0010\u001c\u001a\u00020\u00002\n\b\u0002\u0010\u0002\u001a\u0004\u0018\u00010\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00052\n\b\u0002\u0010\u0006\u001a\u0004\u0018\u00010\u00072\b\b\u0002\u0010\b\u001a\u00020\t2\b\b\u0002\u0010\n\u001a\u00020\u000bH\u00c6\u0001J\u0013\u0010\u001d\u001a\u00020\u001e2\b\u0010\u001f\u001a\u0004\u0018\u00010\u0001H\u00d6\u0003J\t\u0010 \u001a\u00020!H\u00d6\u0001J\t\u0010\"\u001a\u00020\u0003H\u00d6\u0001R\u0013\u0010\u0006\u001a\u0004\u0018\u00010\u0007\u00a2\u0006\b\n\u0000\u001a\u0004\b\r\u0010\u000eR\u0011\u0010\b\u001a\u00020\t\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000f\u0010\u0010R\u0013\u0010\u0002\u001a\u0004\u0018\u00010\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0011\u0010\u0012R\u0011\u0010\u0004\u001a\u00020\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0013\u0010\u0014R\u0011\u0010\n\u001a\u00020\u000b\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0015\u0010\u0016\u00a8\u0006#"}, d2={"Lcom/addonengine/addons/analytics/service/model/UserViews;", "", "userKey", "", "userType", "Lcom/addonengine/addons/analytics/service/confluence/model/UserType;", "lastVersionViewed", "Lcom/addonengine/addons/analytics/service/confluence/model/ContentVersion;", "lastViewedAt", "Ljava/time/Instant;", "views", "", "(Ljava/lang/String;Lcom/addonengine/addons/analytics/service/confluence/model/UserType;Lcom/addonengine/addons/analytics/service/confluence/model/ContentVersion;Ljava/time/Instant;J)V", "getLastVersionViewed", "()Lcom/addonengine/addons/analytics/service/confluence/model/ContentVersion;", "getLastViewedAt", "()Ljava/time/Instant;", "getUserKey", "()Ljava/lang/String;", "getUserType", "()Lcom/addonengine/addons/analytics/service/confluence/model/UserType;", "getViews", "()J", "component1", "component2", "component3", "component4", "component5", "copy", "equals", "", "other", "hashCode", "", "toString", "analytics"})
public final class UserViews {
    @Nullable
    private final String userKey;
    @NotNull
    private final UserType userType;
    @Nullable
    private final ContentVersion lastVersionViewed;
    @NotNull
    private final Instant lastViewedAt;
    private final long views;

    public UserViews(@Nullable String userKey, @NotNull UserType userType, @Nullable ContentVersion lastVersionViewed, @NotNull Instant lastViewedAt, long views) {
        Intrinsics.checkNotNullParameter((Object)((Object)userType), (String)"userType");
        Intrinsics.checkNotNullParameter((Object)lastViewedAt, (String)"lastViewedAt");
        this.userKey = userKey;
        this.userType = userType;
        this.lastVersionViewed = lastVersionViewed;
        this.lastViewedAt = lastViewedAt;
        this.views = views;
    }

    @Nullable
    public final String getUserKey() {
        return this.userKey;
    }

    @NotNull
    public final UserType getUserType() {
        return this.userType;
    }

    @Nullable
    public final ContentVersion getLastVersionViewed() {
        return this.lastVersionViewed;
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
    public final UserType component2() {
        return this.userType;
    }

    @Nullable
    public final ContentVersion component3() {
        return this.lastVersionViewed;
    }

    @NotNull
    public final Instant component4() {
        return this.lastViewedAt;
    }

    public final long component5() {
        return this.views;
    }

    @NotNull
    public final UserViews copy(@Nullable String userKey, @NotNull UserType userType, @Nullable ContentVersion lastVersionViewed, @NotNull Instant lastViewedAt, long views) {
        Intrinsics.checkNotNullParameter((Object)((Object)userType), (String)"userType");
        Intrinsics.checkNotNullParameter((Object)lastViewedAt, (String)"lastViewedAt");
        return new UserViews(userKey, userType, lastVersionViewed, lastViewedAt, views);
    }

    public static /* synthetic */ UserViews copy$default(UserViews userViews, String string, UserType userType, ContentVersion contentVersion, Instant instant, long l, int n, Object object) {
        if ((n & 1) != 0) {
            string = userViews.userKey;
        }
        if ((n & 2) != 0) {
            userType = userViews.userType;
        }
        if ((n & 4) != 0) {
            contentVersion = userViews.lastVersionViewed;
        }
        if ((n & 8) != 0) {
            instant = userViews.lastViewedAt;
        }
        if ((n & 0x10) != 0) {
            l = userViews.views;
        }
        return userViews.copy(string, userType, contentVersion, instant, l);
    }

    @NotNull
    public String toString() {
        return "UserViews(userKey=" + this.userKey + ", userType=" + (Object)((Object)this.userType) + ", lastVersionViewed=" + this.lastVersionViewed + ", lastViewedAt=" + this.lastViewedAt + ", views=" + this.views + ')';
    }

    public int hashCode() {
        int result = this.userKey == null ? 0 : this.userKey.hashCode();
        result = result * 31 + this.userType.hashCode();
        result = result * 31 + (this.lastVersionViewed == null ? 0 : this.lastVersionViewed.hashCode());
        result = result * 31 + this.lastViewedAt.hashCode();
        result = result * 31 + Long.hashCode(this.views);
        return result;
    }

    public boolean equals(@Nullable Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof UserViews)) {
            return false;
        }
        UserViews userViews = (UserViews)other;
        if (!Intrinsics.areEqual((Object)this.userKey, (Object)userViews.userKey)) {
            return false;
        }
        if (this.userType != userViews.userType) {
            return false;
        }
        if (!Intrinsics.areEqual((Object)this.lastVersionViewed, (Object)userViews.lastVersionViewed)) {
            return false;
        }
        if (!Intrinsics.areEqual((Object)this.lastViewedAt, (Object)userViews.lastViewedAt)) {
            return false;
        }
        return this.views == userViews.views;
    }
}

