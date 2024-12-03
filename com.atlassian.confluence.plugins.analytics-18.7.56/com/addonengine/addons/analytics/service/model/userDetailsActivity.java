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

import com.addonengine.addons.analytics.service.confluence.model.User;
import com.addonengine.addons.analytics.service.model.AnalyticsEvent;
import java.util.Map;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(mv={1, 9, 0}, k=1, xi=48, d1={"\u00004\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010$\n\u0002\u0018\u0002\n\u0002\u0010\t\n\u0002\b\t\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000e\n\u0000\b\u0086\b\u0018\u00002\u00020\u0001B!\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0012\u0010\u0004\u001a\u000e\u0012\u0004\u0012\u00020\u0006\u0012\u0004\u0012\u00020\u00070\u0005\u00a2\u0006\u0002\u0010\bJ\t\u0010\r\u001a\u00020\u0003H\u00c6\u0003J\u0015\u0010\u000e\u001a\u000e\u0012\u0004\u0012\u00020\u0006\u0012\u0004\u0012\u00020\u00070\u0005H\u00c6\u0003J)\u0010\u000f\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\u0014\b\u0002\u0010\u0004\u001a\u000e\u0012\u0004\u0012\u00020\u0006\u0012\u0004\u0012\u00020\u00070\u0005H\u00c6\u0001J\u0013\u0010\u0010\u001a\u00020\u00112\b\u0010\u0012\u001a\u0004\u0018\u00010\u0001H\u00d6\u0003J\t\u0010\u0013\u001a\u00020\u0014H\u00d6\u0001J\t\u0010\u0015\u001a\u00020\u0016H\u00d6\u0001R\u001d\u0010\u0004\u001a\u000e\u0012\u0004\u0012\u00020\u0006\u0012\u0004\u0012\u00020\u00070\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b\t\u0010\nR\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000b\u0010\f\u00a8\u0006\u0017"}, d2={"Lcom/addonengine/addons/analytics/service/model/userDetailsActivity;", "", "user", "Lcom/addonengine/addons/analytics/service/confluence/model/User;", "eventTotals", "", "Lcom/addonengine/addons/analytics/service/model/AnalyticsEvent;", "", "(Lcom/addonengine/addons/analytics/service/confluence/model/User;Ljava/util/Map;)V", "getEventTotals", "()Ljava/util/Map;", "getUser", "()Lcom/addonengine/addons/analytics/service/confluence/model/User;", "component1", "component2", "copy", "equals", "", "other", "hashCode", "", "toString", "", "analytics"})
public final class userDetailsActivity {
    @NotNull
    private final User user;
    @NotNull
    private final Map<AnalyticsEvent, Long> eventTotals;

    public userDetailsActivity(@NotNull User user, @NotNull Map<AnalyticsEvent, Long> eventTotals) {
        Intrinsics.checkNotNullParameter((Object)user, (String)"user");
        Intrinsics.checkNotNullParameter(eventTotals, (String)"eventTotals");
        this.user = user;
        this.eventTotals = eventTotals;
    }

    @NotNull
    public final User getUser() {
        return this.user;
    }

    @NotNull
    public final Map<AnalyticsEvent, Long> getEventTotals() {
        return this.eventTotals;
    }

    @NotNull
    public final User component1() {
        return this.user;
    }

    @NotNull
    public final Map<AnalyticsEvent, Long> component2() {
        return this.eventTotals;
    }

    @NotNull
    public final userDetailsActivity copy(@NotNull User user, @NotNull Map<AnalyticsEvent, Long> eventTotals) {
        Intrinsics.checkNotNullParameter((Object)user, (String)"user");
        Intrinsics.checkNotNullParameter(eventTotals, (String)"eventTotals");
        return new userDetailsActivity(user, eventTotals);
    }

    public static /* synthetic */ userDetailsActivity copy$default(userDetailsActivity userDetailsActivity2, User user, Map map, int n, Object object) {
        if ((n & 1) != 0) {
            user = userDetailsActivity2.user;
        }
        if ((n & 2) != 0) {
            map = userDetailsActivity2.eventTotals;
        }
        return userDetailsActivity2.copy(user, map);
    }

    @NotNull
    public String toString() {
        return "userDetailsActivity(user=" + this.user + ", eventTotals=" + this.eventTotals + ')';
    }

    public int hashCode() {
        int result = this.user.hashCode();
        result = result * 31 + ((Object)this.eventTotals).hashCode();
        return result;
    }

    public boolean equals(@Nullable Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof userDetailsActivity)) {
            return false;
        }
        userDetailsActivity userDetailsActivity2 = (userDetailsActivity)other;
        if (!Intrinsics.areEqual((Object)this.user, (Object)userDetailsActivity2.user)) {
            return false;
        }
        return Intrinsics.areEqual(this.eventTotals, userDetailsActivity2.eventTotals);
    }
}

