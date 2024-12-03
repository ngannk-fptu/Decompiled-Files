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
package com.addonengine.addons.analytics.service.model;

import java.time.Instant;
import kotlin.Metadata;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(mv={1, 9, 0}, k=1, xi=48, d1={"\u0000&\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0018\u0002\n\u0002\b\t\n\u0002\u0010\u000b\n\u0002\b\u0004\n\u0002\u0010\u000e\n\u0000\b\u0086\b\u0018\u00002\u00020\u0001B\u0017\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\b\b\u0002\u0010\u0004\u001a\u00020\u0005\u00a2\u0006\u0002\u0010\u0006J\t\u0010\u000b\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\f\u001a\u00020\u0005H\u00c6\u0003J\u001d\u0010\r\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u0005H\u00c6\u0001J\u0013\u0010\u000e\u001a\u00020\u000f2\b\u0010\u0010\u001a\u0004\u0018\u00010\u0001H\u00d6\u0003J\t\u0010\u0011\u001a\u00020\u0003H\u00d6\u0001J\u0006\u0010\u0012\u001a\u00020\u0000J\t\u0010\u0013\u001a\u00020\u0014H\u00d6\u0001R\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0007\u0010\bR\u0011\u0010\u0004\u001a\u00020\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b\t\u0010\n\u00a8\u0006\u0015"}, d2={"Lcom/addonengine/addons/analytics/service/model/ActiveSession;", "", "activeOperationCount", "", "lastEventAt", "Ljava/time/Instant;", "(ILjava/time/Instant;)V", "getActiveOperationCount", "()I", "getLastEventAt", "()Ljava/time/Instant;", "component1", "component2", "copy", "equals", "", "other", "hashCode", "incremented", "toString", "", "analytics"})
public final class ActiveSession {
    private final int activeOperationCount;
    @NotNull
    private final Instant lastEventAt;

    public ActiveSession(int activeOperationCount, @NotNull Instant lastEventAt) {
        Intrinsics.checkNotNullParameter((Object)lastEventAt, (String)"lastEventAt");
        this.activeOperationCount = activeOperationCount;
        this.lastEventAt = lastEventAt;
    }

    public /* synthetic */ ActiveSession(int n, Instant instant, int n2, DefaultConstructorMarker defaultConstructorMarker) {
        if ((n2 & 2) != 0) {
            Instant instant2 = Instant.now();
            Intrinsics.checkNotNullExpressionValue((Object)instant2, (String)"now(...)");
            instant = instant2;
        }
        this(n, instant);
    }

    public final int getActiveOperationCount() {
        return this.activeOperationCount;
    }

    @NotNull
    public final Instant getLastEventAt() {
        return this.lastEventAt;
    }

    @NotNull
    public final ActiveSession incremented() {
        return new ActiveSession(this.activeOperationCount + 1, null, 2, null);
    }

    public final int component1() {
        return this.activeOperationCount;
    }

    @NotNull
    public final Instant component2() {
        return this.lastEventAt;
    }

    @NotNull
    public final ActiveSession copy(int activeOperationCount, @NotNull Instant lastEventAt) {
        Intrinsics.checkNotNullParameter((Object)lastEventAt, (String)"lastEventAt");
        return new ActiveSession(activeOperationCount, lastEventAt);
    }

    public static /* synthetic */ ActiveSession copy$default(ActiveSession activeSession, int n, Instant instant, int n2, Object object) {
        if ((n2 & 1) != 0) {
            n = activeSession.activeOperationCount;
        }
        if ((n2 & 2) != 0) {
            instant = activeSession.lastEventAt;
        }
        return activeSession.copy(n, instant);
    }

    @NotNull
    public String toString() {
        return "ActiveSession(activeOperationCount=" + this.activeOperationCount + ", lastEventAt=" + this.lastEventAt + ')';
    }

    public int hashCode() {
        int result = Integer.hashCode(this.activeOperationCount);
        result = result * 31 + this.lastEventAt.hashCode();
        return result;
    }

    public boolean equals(@Nullable Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof ActiveSession)) {
            return false;
        }
        ActiveSession activeSession = (ActiveSession)other;
        if (this.activeOperationCount != activeSession.activeOperationCount) {
            return false;
        }
        return Intrinsics.areEqual((Object)this.lastEventAt, (Object)activeSession.lastEventAt);
    }
}

