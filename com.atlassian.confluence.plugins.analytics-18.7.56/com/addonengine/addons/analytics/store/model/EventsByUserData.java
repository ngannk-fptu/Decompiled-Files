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

import com.addonengine.addons.analytics.service.model.AnalyticsEvent;
import java.util.Map;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(mv={1, 9, 0}, k=1, xi=48, d1={"\u00000\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010$\n\u0002\u0018\u0002\n\u0002\u0010\t\n\u0002\b\t\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0002\b\u0086\b\u0018\u00002\u00020\u0001B#\u0012\b\u0010\u0002\u001a\u0004\u0018\u00010\u0003\u0012\u0012\u0010\u0004\u001a\u000e\u0012\u0004\u0012\u00020\u0006\u0012\u0004\u0012\u00020\u00070\u0005\u00a2\u0006\u0002\u0010\bJ\u000b\u0010\r\u001a\u0004\u0018\u00010\u0003H\u00c6\u0003J\u0015\u0010\u000e\u001a\u000e\u0012\u0004\u0012\u00020\u0006\u0012\u0004\u0012\u00020\u00070\u0005H\u00c6\u0003J+\u0010\u000f\u001a\u00020\u00002\n\b\u0002\u0010\u0002\u001a\u0004\u0018\u00010\u00032\u0014\b\u0002\u0010\u0004\u001a\u000e\u0012\u0004\u0012\u00020\u0006\u0012\u0004\u0012\u00020\u00070\u0005H\u00c6\u0001J\u0013\u0010\u0010\u001a\u00020\u00112\b\u0010\u0012\u001a\u0004\u0018\u00010\u0001H\u00d6\u0003J\t\u0010\u0013\u001a\u00020\u0014H\u00d6\u0001J\t\u0010\u0015\u001a\u00020\u0003H\u00d6\u0001R\u001d\u0010\u0004\u001a\u000e\u0012\u0004\u0012\u00020\u0006\u0012\u0004\u0012\u00020\u00070\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b\t\u0010\nR\u0013\u0010\u0002\u001a\u0004\u0018\u00010\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000b\u0010\f\u00a8\u0006\u0016"}, d2={"Lcom/addonengine/addons/analytics/store/model/EventsByUserData;", "", "userKey", "", "eventTotals", "", "Lcom/addonengine/addons/analytics/service/model/AnalyticsEvent;", "", "(Ljava/lang/String;Ljava/util/Map;)V", "getEventTotals", "()Ljava/util/Map;", "getUserKey", "()Ljava/lang/String;", "component1", "component2", "copy", "equals", "", "other", "hashCode", "", "toString", "analytics"})
public final class EventsByUserData {
    @Nullable
    private final String userKey;
    @NotNull
    private final Map<AnalyticsEvent, Long> eventTotals;

    public EventsByUserData(@Nullable String userKey, @NotNull Map<AnalyticsEvent, Long> eventTotals) {
        Intrinsics.checkNotNullParameter(eventTotals, (String)"eventTotals");
        this.userKey = userKey;
        this.eventTotals = eventTotals;
    }

    @Nullable
    public final String getUserKey() {
        return this.userKey;
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
    public final Map<AnalyticsEvent, Long> component2() {
        return this.eventTotals;
    }

    @NotNull
    public final EventsByUserData copy(@Nullable String userKey, @NotNull Map<AnalyticsEvent, Long> eventTotals) {
        Intrinsics.checkNotNullParameter(eventTotals, (String)"eventTotals");
        return new EventsByUserData(userKey, eventTotals);
    }

    public static /* synthetic */ EventsByUserData copy$default(EventsByUserData eventsByUserData, String string, Map map, int n, Object object) {
        if ((n & 1) != 0) {
            string = eventsByUserData.userKey;
        }
        if ((n & 2) != 0) {
            map = eventsByUserData.eventTotals;
        }
        return eventsByUserData.copy(string, map);
    }

    @NotNull
    public String toString() {
        return "EventsByUserData(userKey=" + this.userKey + ", eventTotals=" + this.eventTotals + ')';
    }

    public int hashCode() {
        int result = this.userKey == null ? 0 : this.userKey.hashCode();
        result = result * 31 + ((Object)this.eventTotals).hashCode();
        return result;
    }

    public boolean equals(@Nullable Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof EventsByUserData)) {
            return false;
        }
        EventsByUserData eventsByUserData = (EventsByUserData)other;
        if (!Intrinsics.areEqual((Object)this.userKey, (Object)eventsByUserData.userKey)) {
            return false;
        }
        return Intrinsics.areEqual(this.eventTotals, eventsByUserData.eventTotals);
    }
}

