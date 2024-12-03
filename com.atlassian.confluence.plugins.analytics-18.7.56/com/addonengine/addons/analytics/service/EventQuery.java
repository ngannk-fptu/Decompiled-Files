/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  kotlin.Metadata
 *  kotlin.jvm.internal.DefaultConstructorMarker
 *  kotlin.jvm.internal.Intrinsics
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package com.addonengine.addons.analytics.service;

import java.time.Instant;
import kotlin.Metadata;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(mv={1, 9, 0}, k=1, xi=48, d1={"\u0000&\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\b\t\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000e\n\u0000\b\u0086\b\u0018\u00002\u00020\u0001B\u0017\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\b\b\u0002\u0010\u0004\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0005J\t\u0010\t\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\n\u001a\u00020\u0003H\u00c6\u0003J\u001d\u0010\u000b\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u0003H\u00c6\u0001J\u0013\u0010\f\u001a\u00020\r2\b\u0010\u000e\u001a\u0004\u0018\u00010\u0001H\u00d6\u0003J\t\u0010\u000f\u001a\u00020\u0010H\u00d6\u0001J\t\u0010\u0011\u001a\u00020\u0012H\u00d6\u0001R\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0006\u0010\u0007R\u0011\u0010\u0004\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\b\u0010\u0007\u00a8\u0006\u0013"}, d2={"Lcom/addonengine/addons/analytics/service/EventQuery;", "", "from", "Ljava/time/Instant;", "to", "(Ljava/time/Instant;Ljava/time/Instant;)V", "getFrom", "()Ljava/time/Instant;", "getTo", "component1", "component2", "copy", "equals", "", "other", "hashCode", "", "toString", "", "analytics"})
public final class EventQuery {
    @NotNull
    private final Instant from;
    @NotNull
    private final Instant to;

    public EventQuery(@NotNull Instant from, @NotNull Instant to) {
        Intrinsics.checkNotNullParameter((Object)from, (String)"from");
        Intrinsics.checkNotNullParameter((Object)to, (String)"to");
        this.from = from;
        this.to = to;
    }

    public /* synthetic */ EventQuery(Instant instant, Instant instant2, int n, DefaultConstructorMarker defaultConstructorMarker) {
        if ((n & 2) != 0) {
            Instant instant3 = Instant.now();
            Intrinsics.checkNotNullExpressionValue((Object)instant3, (String)"now(...)");
            instant2 = instant3;
        }
        this(instant, instant2);
    }

    @NotNull
    public final Instant getFrom() {
        return this.from;
    }

    @NotNull
    public final Instant getTo() {
        return this.to;
    }

    @NotNull
    public final Instant component1() {
        return this.from;
    }

    @NotNull
    public final Instant component2() {
        return this.to;
    }

    @NotNull
    public final EventQuery copy(@NotNull Instant from, @NotNull Instant to) {
        Intrinsics.checkNotNullParameter((Object)from, (String)"from");
        Intrinsics.checkNotNullParameter((Object)to, (String)"to");
        return new EventQuery(from, to);
    }

    public static /* synthetic */ EventQuery copy$default(EventQuery eventQuery, Instant instant, Instant instant2, int n, Object object) {
        if ((n & 1) != 0) {
            instant = eventQuery.from;
        }
        if ((n & 2) != 0) {
            instant2 = eventQuery.to;
        }
        return eventQuery.copy(instant, instant2);
    }

    @NotNull
    public String toString() {
        return "EventQuery(from=" + this.from + ", to=" + this.to + ')';
    }

    public int hashCode() {
        int result = this.from.hashCode();
        result = result * 31 + this.to.hashCode();
        return result;
    }

    public boolean equals(@Nullable Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof EventQuery)) {
            return false;
        }
        EventQuery eventQuery = (EventQuery)other;
        if (!Intrinsics.areEqual((Object)this.from, (Object)eventQuery.from)) {
            return false;
        }
        return Intrinsics.areEqual((Object)this.to, (Object)eventQuery.to);
    }
}

