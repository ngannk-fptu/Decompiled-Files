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

import com.addonengine.addons.analytics.service.confluence.model.UserType;
import com.addonengine.addons.analytics.service.model.AnalyticsEvent;
import java.util.Map;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(mv={1, 9, 0}, k=1, xi=48, d1={"\u00006\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010$\n\u0002\u0018\u0002\n\u0002\u0010\t\n\u0002\b\f\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0002\b\u0086\b\u0018\u00002\u00020\u0001B+\u0012\b\u0010\u0002\u001a\u0004\u0018\u00010\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\u0012\u0010\u0006\u001a\u000e\u0012\u0004\u0012\u00020\b\u0012\u0004\u0012\u00020\t0\u0007\u00a2\u0006\u0002\u0010\nJ\u000b\u0010\u0011\u001a\u0004\u0018\u00010\u0003H\u00c6\u0003J\t\u0010\u0012\u001a\u00020\u0005H\u00c6\u0003J\u0015\u0010\u0013\u001a\u000e\u0012\u0004\u0012\u00020\b\u0012\u0004\u0012\u00020\t0\u0007H\u00c6\u0003J5\u0010\u0014\u001a\u00020\u00002\n\b\u0002\u0010\u0002\u001a\u0004\u0018\u00010\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00052\u0014\b\u0002\u0010\u0006\u001a\u000e\u0012\u0004\u0012\u00020\b\u0012\u0004\u0012\u00020\t0\u0007H\u00c6\u0001J\u0013\u0010\u0015\u001a\u00020\u00162\b\u0010\u0017\u001a\u0004\u0018\u00010\u0001H\u00d6\u0003J\t\u0010\u0018\u001a\u00020\u0019H\u00d6\u0001J\t\u0010\u001a\u001a\u00020\u0003H\u00d6\u0001R\u001d\u0010\u0006\u001a\u000e\u0012\u0004\u0012\u00020\b\u0012\u0004\u0012\u00020\t0\u0007\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000b\u0010\fR\u0013\u0010\u0002\u001a\u0004\u0018\u00010\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\r\u0010\u000eR\u0011\u0010\u0004\u001a\u00020\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000f\u0010\u0010\u00a8\u0006\u001b"}, d2={"Lcom/addonengine/addons/analytics/service/model/UserActivity;", "", "userKey", "", "userType", "Lcom/addonengine/addons/analytics/service/confluence/model/UserType;", "eventTotals", "", "Lcom/addonengine/addons/analytics/service/model/AnalyticsEvent;", "", "(Ljava/lang/String;Lcom/addonengine/addons/analytics/service/confluence/model/UserType;Ljava/util/Map;)V", "getEventTotals", "()Ljava/util/Map;", "getUserKey", "()Ljava/lang/String;", "getUserType", "()Lcom/addonengine/addons/analytics/service/confluence/model/UserType;", "component1", "component2", "component3", "copy", "equals", "", "other", "hashCode", "", "toString", "analytics"})
public final class UserActivity {
    @Nullable
    private final String userKey;
    @NotNull
    private final UserType userType;
    @NotNull
    private final Map<AnalyticsEvent, Long> eventTotals;

    public UserActivity(@Nullable String userKey, @NotNull UserType userType, @NotNull Map<AnalyticsEvent, Long> eventTotals) {
        Intrinsics.checkNotNullParameter((Object)((Object)userType), (String)"userType");
        Intrinsics.checkNotNullParameter(eventTotals, (String)"eventTotals");
        this.userKey = userKey;
        this.userType = userType;
        this.eventTotals = eventTotals;
    }

    @Nullable
    public final String getUserKey() {
        return this.userKey;
    }

    @NotNull
    public final UserType getUserType() {
        return this.userType;
    }

    @NotNull
    public final Map<AnalyticsEvent, Long> getEventTotals() {
        return this.eventTotals;
    }

    @Nullable
    public final String component1() {
        return this.userKey;
    }

    @NotNull
    public final UserType component2() {
        return this.userType;
    }

    @NotNull
    public final Map<AnalyticsEvent, Long> component3() {
        return this.eventTotals;
    }

    @NotNull
    public final UserActivity copy(@Nullable String userKey, @NotNull UserType userType, @NotNull Map<AnalyticsEvent, Long> eventTotals) {
        Intrinsics.checkNotNullParameter((Object)((Object)userType), (String)"userType");
        Intrinsics.checkNotNullParameter(eventTotals, (String)"eventTotals");
        return new UserActivity(userKey, userType, eventTotals);
    }

    public static /* synthetic */ UserActivity copy$default(UserActivity userActivity, String string, UserType userType, Map map, int n, Object object) {
        if ((n & 1) != 0) {
            string = userActivity.userKey;
        }
        if ((n & 2) != 0) {
            userType = userActivity.userType;
        }
        if ((n & 4) != 0) {
            map = userActivity.eventTotals;
        }
        return userActivity.copy(string, userType, map);
    }

    @NotNull
    public String toString() {
        return "UserActivity(userKey=" + this.userKey + ", userType=" + (Object)((Object)this.userType) + ", eventTotals=" + this.eventTotals + ')';
    }

    public int hashCode() {
        int result = this.userKey == null ? 0 : this.userKey.hashCode();
        result = result * 31 + this.userType.hashCode();
        result = result * 31 + ((Object)this.eventTotals).hashCode();
        return result;
    }

    public boolean equals(@Nullable Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof UserActivity)) {
            return false;
        }
        UserActivity userActivity = (UserActivity)other;
        if (!Intrinsics.areEqual((Object)this.userKey, (Object)userActivity.userKey)) {
            return false;
        }
        if (this.userType != userActivity.userType) {
            return false;
        }
        return Intrinsics.areEqual(this.eventTotals, userActivity.eventTotals);
    }
}

