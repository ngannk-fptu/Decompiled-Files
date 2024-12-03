/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  kotlin.Metadata
 *  kotlin.jvm.internal.Intrinsics
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package com.addonengine.addons.analytics.service;

import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(mv={1, 9, 0}, k=1, xi=48, d1={"\u0000(\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\t\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u001e\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0002\b\u0086\b\u0018\u00002\u00020\u0001BO\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\u0006\u0010\u0006\u001a\u00020\u0003\u0012\b\u0010\u0007\u001a\u0004\u0018\u00010\u0003\u0012\b\u0010\b\u001a\u0004\u0018\u00010\u0005\u0012\b\u0010\t\u001a\u0004\u0018\u00010\u0005\u0012\b\u0010\n\u001a\u0004\u0018\u00010\u0003\u0012\b\u0010\u000b\u001a\u0004\u0018\u00010\u0003\u00a2\u0006\u0002\u0010\fJ\t\u0010\u0019\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\u001a\u001a\u00020\u0005H\u00c6\u0003J\t\u0010\u001b\u001a\u00020\u0003H\u00c6\u0003J\u0010\u0010\u001c\u001a\u0004\u0018\u00010\u0003H\u00c6\u0003\u00a2\u0006\u0002\u0010\u000eJ\u000b\u0010\u001d\u001a\u0004\u0018\u00010\u0005H\u00c6\u0003J\u000b\u0010\u001e\u001a\u0004\u0018\u00010\u0005H\u00c6\u0003J\u0010\u0010\u001f\u001a\u0004\u0018\u00010\u0003H\u00c6\u0003\u00a2\u0006\u0002\u0010\u000eJ\u0010\u0010 \u001a\u0004\u0018\u00010\u0003H\u00c6\u0003\u00a2\u0006\u0002\u0010\u000eJh\u0010!\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00052\b\b\u0002\u0010\u0006\u001a\u00020\u00032\n\b\u0002\u0010\u0007\u001a\u0004\u0018\u00010\u00032\n\b\u0002\u0010\b\u001a\u0004\u0018\u00010\u00052\n\b\u0002\u0010\t\u001a\u0004\u0018\u00010\u00052\n\b\u0002\u0010\n\u001a\u0004\u0018\u00010\u00032\n\b\u0002\u0010\u000b\u001a\u0004\u0018\u00010\u0003H\u00c6\u0001\u00a2\u0006\u0002\u0010\"J\u0013\u0010#\u001a\u00020$2\b\u0010%\u001a\u0004\u0018\u00010\u0001H\u00d6\u0003J\t\u0010&\u001a\u00020'H\u00d6\u0001J\t\u0010(\u001a\u00020\u0005H\u00d6\u0001R\u0015\u0010\u0007\u001a\u0004\u0018\u00010\u0003\u00a2\u0006\n\n\u0002\u0010\u000f\u001a\u0004\b\r\u0010\u000eR\u0015\u0010\n\u001a\u0004\u0018\u00010\u0003\u00a2\u0006\n\n\u0002\u0010\u000f\u001a\u0004\b\u0010\u0010\u000eR\u0011\u0010\u0006\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0011\u0010\u0012R\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0013\u0010\u0012R\u0011\u0010\u0004\u001a\u00020\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0014\u0010\u0015R\u0013\u0010\b\u001a\u0004\u0018\u00010\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0016\u0010\u0015R\u0013\u0010\t\u001a\u0004\u0018\u00010\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0017\u0010\u0015R\u0015\u0010\u000b\u001a\u0004\u0018\u00010\u0003\u00a2\u0006\n\n\u0002\u0010\u000f\u001a\u0004\b\u0018\u0010\u000e\u00a8\u0006)"}, d2={"Lcom/addonengine/addons/analytics/service/Event;", "", "id", "", "name", "", "eventAt", "containerId", "spaceKey", "userKey", "contentId", "versionModificationDate", "(JLjava/lang/String;JLjava/lang/Long;Ljava/lang/String;Ljava/lang/String;Ljava/lang/Long;Ljava/lang/Long;)V", "getContainerId", "()Ljava/lang/Long;", "Ljava/lang/Long;", "getContentId", "getEventAt", "()J", "getId", "getName", "()Ljava/lang/String;", "getSpaceKey", "getUserKey", "getVersionModificationDate", "component1", "component2", "component3", "component4", "component5", "component6", "component7", "component8", "copy", "(JLjava/lang/String;JLjava/lang/Long;Ljava/lang/String;Ljava/lang/String;Ljava/lang/Long;Ljava/lang/Long;)Lcom/addonengine/addons/analytics/service/Event;", "equals", "", "other", "hashCode", "", "toString", "analytics"})
public final class Event {
    private final long id;
    @NotNull
    private final String name;
    private final long eventAt;
    @Nullable
    private final Long containerId;
    @Nullable
    private final String spaceKey;
    @Nullable
    private final String userKey;
    @Nullable
    private final Long contentId;
    @Nullable
    private final Long versionModificationDate;

    public Event(long id, @NotNull String name, long eventAt, @Nullable Long containerId, @Nullable String spaceKey, @Nullable String userKey, @Nullable Long contentId, @Nullable Long versionModificationDate) {
        Intrinsics.checkNotNullParameter((Object)name, (String)"name");
        this.id = id;
        this.name = name;
        this.eventAt = eventAt;
        this.containerId = containerId;
        this.spaceKey = spaceKey;
        this.userKey = userKey;
        this.contentId = contentId;
        this.versionModificationDate = versionModificationDate;
    }

    public final long getId() {
        return this.id;
    }

    @NotNull
    public final String getName() {
        return this.name;
    }

    public final long getEventAt() {
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
    public final Long getVersionModificationDate() {
        return this.versionModificationDate;
    }

    public final long component1() {
        return this.id;
    }

    @NotNull
    public final String component2() {
        return this.name;
    }

    public final long component3() {
        return this.eventAt;
    }

    @Nullable
    public final Long component4() {
        return this.containerId;
    }

    @Nullable
    public final String component5() {
        return this.spaceKey;
    }

    @Nullable
    public final String component6() {
        return this.userKey;
    }

    @Nullable
    public final Long component7() {
        return this.contentId;
    }

    @Nullable
    public final Long component8() {
        return this.versionModificationDate;
    }

    @NotNull
    public final Event copy(long id, @NotNull String name, long eventAt, @Nullable Long containerId, @Nullable String spaceKey, @Nullable String userKey, @Nullable Long contentId, @Nullable Long versionModificationDate) {
        Intrinsics.checkNotNullParameter((Object)name, (String)"name");
        return new Event(id, name, eventAt, containerId, spaceKey, userKey, contentId, versionModificationDate);
    }

    public static /* synthetic */ Event copy$default(Event event, long l, String string, long l2, Long l3, String string2, String string3, Long l4, Long l5, int n, Object object) {
        if ((n & 1) != 0) {
            l = event.id;
        }
        if ((n & 2) != 0) {
            string = event.name;
        }
        if ((n & 4) != 0) {
            l2 = event.eventAt;
        }
        if ((n & 8) != 0) {
            l3 = event.containerId;
        }
        if ((n & 0x10) != 0) {
            string2 = event.spaceKey;
        }
        if ((n & 0x20) != 0) {
            string3 = event.userKey;
        }
        if ((n & 0x40) != 0) {
            l4 = event.contentId;
        }
        if ((n & 0x80) != 0) {
            l5 = event.versionModificationDate;
        }
        return event.copy(l, string, l2, l3, string2, string3, l4, l5);
    }

    @NotNull
    public String toString() {
        return "Event(id=" + this.id + ", name=" + this.name + ", eventAt=" + this.eventAt + ", containerId=" + this.containerId + ", spaceKey=" + this.spaceKey + ", userKey=" + this.userKey + ", contentId=" + this.contentId + ", versionModificationDate=" + this.versionModificationDate + ')';
    }

    public int hashCode() {
        int result = Long.hashCode(this.id);
        result = result * 31 + this.name.hashCode();
        result = result * 31 + Long.hashCode(this.eventAt);
        result = result * 31 + (this.containerId == null ? 0 : ((Object)this.containerId).hashCode());
        result = result * 31 + (this.spaceKey == null ? 0 : this.spaceKey.hashCode());
        result = result * 31 + (this.userKey == null ? 0 : this.userKey.hashCode());
        result = result * 31 + (this.contentId == null ? 0 : ((Object)this.contentId).hashCode());
        result = result * 31 + (this.versionModificationDate == null ? 0 : ((Object)this.versionModificationDate).hashCode());
        return result;
    }

    public boolean equals(@Nullable Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof Event)) {
            return false;
        }
        Event event = (Event)other;
        if (this.id != event.id) {
            return false;
        }
        if (!Intrinsics.areEqual((Object)this.name, (Object)event.name)) {
            return false;
        }
        if (this.eventAt != event.eventAt) {
            return false;
        }
        if (!Intrinsics.areEqual((Object)this.containerId, (Object)event.containerId)) {
            return false;
        }
        if (!Intrinsics.areEqual((Object)this.spaceKey, (Object)event.spaceKey)) {
            return false;
        }
        if (!Intrinsics.areEqual((Object)this.userKey, (Object)event.userKey)) {
            return false;
        }
        if (!Intrinsics.areEqual((Object)this.contentId, (Object)event.contentId)) {
            return false;
        }
        return Intrinsics.areEqual((Object)this.versionModificationDate, (Object)event.versionModificationDate);
    }
}

