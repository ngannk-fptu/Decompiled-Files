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
import java.time.Instant;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(mv={1, 9, 0}, k=1, xi=48, d1={"\u00004\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\t\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u001a\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0002\b\u0086\b\u0018\u00002\u00020\u0001BG\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\b\u0010\u0006\u001a\u0004\u0018\u00010\u0007\u0012\b\u0010\b\u001a\u0004\u0018\u00010\t\u0012\b\u0010\n\u001a\u0004\u0018\u00010\t\u0012\b\u0010\u000b\u001a\u0004\u0018\u00010\u0007\u0012\b\u0010\f\u001a\u0004\u0018\u00010\u0005\u00a2\u0006\u0002\u0010\rJ\t\u0010\u001a\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\u001b\u001a\u00020\u0005H\u00c6\u0003J\u0010\u0010\u001c\u001a\u0004\u0018\u00010\u0007H\u00c6\u0003\u00a2\u0006\u0002\u0010\u000fJ\u000b\u0010\u001d\u001a\u0004\u0018\u00010\tH\u00c6\u0003J\u000b\u0010\u001e\u001a\u0004\u0018\u00010\tH\u00c6\u0003J\u0010\u0010\u001f\u001a\u0004\u0018\u00010\u0007H\u00c6\u0003\u00a2\u0006\u0002\u0010\u000fJ\u000b\u0010 \u001a\u0004\u0018\u00010\u0005H\u00c6\u0003J^\u0010!\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00052\n\b\u0002\u0010\u0006\u001a\u0004\u0018\u00010\u00072\n\b\u0002\u0010\b\u001a\u0004\u0018\u00010\t2\n\b\u0002\u0010\n\u001a\u0004\u0018\u00010\t2\n\b\u0002\u0010\u000b\u001a\u0004\u0018\u00010\u00072\n\b\u0002\u0010\f\u001a\u0004\u0018\u00010\u0005H\u00c6\u0001\u00a2\u0006\u0002\u0010\"J\u0013\u0010#\u001a\u00020$2\b\u0010%\u001a\u0004\u0018\u00010\u0001H\u00d6\u0003J\t\u0010&\u001a\u00020'H\u00d6\u0001J\t\u0010(\u001a\u00020\tH\u00d6\u0001R\u0015\u0010\u0006\u001a\u0004\u0018\u00010\u0007\u00a2\u0006\n\n\u0002\u0010\u0010\u001a\u0004\b\u000e\u0010\u000fR\u0015\u0010\u000b\u001a\u0004\u0018\u00010\u0007\u00a2\u0006\n\n\u0002\u0010\u0010\u001a\u0004\b\u0011\u0010\u000fR\u0011\u0010\u0004\u001a\u00020\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0012\u0010\u0013R\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0014\u0010\u0015R\u0013\u0010\b\u001a\u0004\u0018\u00010\t\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0016\u0010\u0017R\u0013\u0010\n\u001a\u0004\u0018\u00010\t\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0018\u0010\u0017R\u0013\u0010\f\u001a\u0004\u0018\u00010\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0019\u0010\u0013\u00a8\u0006)"}, d2={"Lcom/addonengine/addons/analytics/store/model/EventData;", "", "name", "Lcom/addonengine/addons/analytics/service/model/AnalyticsEvent;", "eventAt", "Ljava/time/Instant;", "containerId", "", "spaceKey", "", "userKey", "contentId", "versionModificationDate", "(Lcom/addonengine/addons/analytics/service/model/AnalyticsEvent;Ljava/time/Instant;Ljava/lang/Long;Ljava/lang/String;Ljava/lang/String;Ljava/lang/Long;Ljava/time/Instant;)V", "getContainerId", "()Ljava/lang/Long;", "Ljava/lang/Long;", "getContentId", "getEventAt", "()Ljava/time/Instant;", "getName", "()Lcom/addonengine/addons/analytics/service/model/AnalyticsEvent;", "getSpaceKey", "()Ljava/lang/String;", "getUserKey", "getVersionModificationDate", "component1", "component2", "component3", "component4", "component5", "component6", "component7", "copy", "(Lcom/addonengine/addons/analytics/service/model/AnalyticsEvent;Ljava/time/Instant;Ljava/lang/Long;Ljava/lang/String;Ljava/lang/String;Ljava/lang/Long;Ljava/time/Instant;)Lcom/addonengine/addons/analytics/store/model/EventData;", "equals", "", "other", "hashCode", "", "toString", "analytics"})
public final class EventData {
    @NotNull
    private final AnalyticsEvent name;
    @NotNull
    private final Instant eventAt;
    @Nullable
    private final Long containerId;
    @Nullable
    private final String spaceKey;
    @Nullable
    private final String userKey;
    @Nullable
    private final Long contentId;
    @Nullable
    private final Instant versionModificationDate;

    public EventData(@NotNull AnalyticsEvent name, @NotNull Instant eventAt, @Nullable Long containerId, @Nullable String spaceKey, @Nullable String userKey, @Nullable Long contentId, @Nullable Instant versionModificationDate) {
        Intrinsics.checkNotNullParameter((Object)((Object)name), (String)"name");
        Intrinsics.checkNotNullParameter((Object)eventAt, (String)"eventAt");
        this.name = name;
        this.eventAt = eventAt;
        this.containerId = containerId;
        this.spaceKey = spaceKey;
        this.userKey = userKey;
        this.contentId = contentId;
        this.versionModificationDate = versionModificationDate;
    }

    @NotNull
    public final AnalyticsEvent getName() {
        return this.name;
    }

    @NotNull
    public final Instant getEventAt() {
        return this.eventAt;
    }

    @Nullable
    public final Long getContainerId() {
        return this.containerId;
    }

    @Nullable
    public final String getSpaceKey() {
        return this.spaceKey;
    }

    @Nullable
    public final String getUserKey() {
        return this.userKey;
    }

    @Nullable
    public final Long getContentId() {
        return this.contentId;
    }

    @Nullable
    public final Instant getVersionModificationDate() {
        return this.versionModificationDate;
    }

    @NotNull
    public final AnalyticsEvent component1() {
        return this.name;
    }

    @NotNull
    public final Instant component2() {
        return this.eventAt;
    }

    @Nullable
    public final Long component3() {
        return this.containerId;
    }

    @Nullable
    public final String component4() {
        return this.spaceKey;
    }

    @Nullable
    public final String component5() {
        return this.userKey;
    }

    @Nullable
    public final Long component6() {
        return this.contentId;
    }

    @Nullable
    public final Instant component7() {
        return this.versionModificationDate;
    }

    @NotNull
    public final EventData copy(@NotNull AnalyticsEvent name, @NotNull Instant eventAt, @Nullable Long containerId, @Nullable String spaceKey, @Nullable String userKey, @Nullable Long contentId, @Nullable Instant versionModificationDate) {
        Intrinsics.checkNotNullParameter((Object)((Object)name), (String)"name");
        Intrinsics.checkNotNullParameter((Object)eventAt, (String)"eventAt");
        return new EventData(name, eventAt, containerId, spaceKey, userKey, contentId, versionModificationDate);
    }

    public static /* synthetic */ EventData copy$default(EventData eventData, AnalyticsEvent analyticsEvent, Instant instant, Long l, String string, String string2, Long l2, Instant instant2, int n, Object object) {
        if ((n & 1) != 0) {
            analyticsEvent = eventData.name;
        }
        if ((n & 2) != 0) {
            instant = eventData.eventAt;
        }
        if ((n & 4) != 0) {
            l = eventData.containerId;
        }
        if ((n & 8) != 0) {
            string = eventData.spaceKey;
        }
        if ((n & 0x10) != 0) {
            string2 = eventData.userKey;
        }
        if ((n & 0x20) != 0) {
            l2 = eventData.contentId;
        }
        if ((n & 0x40) != 0) {
            instant2 = eventData.versionModificationDate;
        }
        return eventData.copy(analyticsEvent, instant, l, string, string2, l2, instant2);
    }

    @NotNull
    public String toString() {
        return "EventData(name=" + (Object)((Object)this.name) + ", eventAt=" + this.eventAt + ", containerId=" + this.containerId + ", spaceKey=" + this.spaceKey + ", userKey=" + this.userKey + ", contentId=" + this.contentId + ", versionModificationDate=" + this.versionModificationDate + ')';
    }

    public int hashCode() {
        int result = this.name.hashCode();
        result = result * 31 + this.eventAt.hashCode();
        result = result * 31 + (this.containerId == null ? 0 : ((Object)this.containerId).hashCode());
        result = result * 31 + (this.spaceKey == null ? 0 : this.spaceKey.hashCode());
        result = result * 31 + (this.userKey == null ? 0 : this.userKey.hashCode());
        result = result * 31 + (this.contentId == null ? 0 : ((Object)this.contentId).hashCode());
        result = result * 31 + (this.versionModificationDate == null ? 0 : this.versionModificationDate.hashCode());
        return result;
    }

    public boolean equals(@Nullable Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof EventData)) {
            return false;
        }
        EventData eventData = (EventData)other;
        if (this.name != eventData.name) {
            return false;
        }
        if (!Intrinsics.areEqual((Object)this.eventAt, (Object)eventData.eventAt)) {
            return false;
        }
        if (!Intrinsics.areEqual((Object)this.containerId, (Object)eventData.containerId)) {
            return false;
        }
        if (!Intrinsics.areEqual((Object)this.spaceKey, (Object)eventData.spaceKey)) {
            return false;
        }
        if (!Intrinsics.areEqual((Object)this.userKey, (Object)eventData.userKey)) {
            return false;
        }
        if (!Intrinsics.areEqual((Object)this.contentId, (Object)eventData.contentId)) {
            return false;
        }
        return Intrinsics.areEqual((Object)this.versionModificationDate, (Object)eventData.versionModificationDate);
    }
}

