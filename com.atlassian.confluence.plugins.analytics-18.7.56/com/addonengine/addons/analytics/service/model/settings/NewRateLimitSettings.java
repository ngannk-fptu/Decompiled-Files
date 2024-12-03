/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  kotlin.Metadata
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package com.addonengine.addons.analytics.service.model.settings;

import kotlin.Metadata;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(mv={1, 9, 0}, k=1, xi=48, d1={"\u0000$\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0010\t\n\u0002\b\u0012\n\u0002\u0010\u000e\n\u0000\b\u0086\b\u0018\u00002\u00020\u0001B%\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\u0006\u0010\u0006\u001a\u00020\u0007\u0012\u0006\u0010\b\u001a\u00020\u0005\u00a2\u0006\u0002\u0010\tJ\t\u0010\u0011\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\u0012\u001a\u00020\u0005H\u00c6\u0003J\t\u0010\u0013\u001a\u00020\u0007H\u00c6\u0003J\t\u0010\u0014\u001a\u00020\u0005H\u00c6\u0003J1\u0010\u0015\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00052\b\b\u0002\u0010\u0006\u001a\u00020\u00072\b\b\u0002\u0010\b\u001a\u00020\u0005H\u00c6\u0001J\u0013\u0010\u0016\u001a\u00020\u00032\b\u0010\u0017\u001a\u0004\u0018\u00010\u0001H\u00d6\u0003J\t\u0010\u0018\u001a\u00020\u0005H\u00d6\u0001J\t\u0010\u0019\u001a\u00020\u001aH\u00d6\u0001R\u0011\u0010\b\u001a\u00020\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b\n\u0010\u000bR\u0011\u0010\u0004\u001a\u00020\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b\f\u0010\u000bR\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\r\u0010\u000eR\u0011\u0010\u0006\u001a\u00020\u0007\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000f\u0010\u0010\u00a8\u0006\u001b"}, d2={"Lcom/addonengine/addons/analytics/service/model/settings/NewRateLimitSettings;", "", "enabled", "", "concurrentSessions", "", "staleOperationSeconds", "", "concurrentOperationsPerSession", "(ZIJI)V", "getConcurrentOperationsPerSession", "()I", "getConcurrentSessions", "getEnabled", "()Z", "getStaleOperationSeconds", "()J", "component1", "component2", "component3", "component4", "copy", "equals", "other", "hashCode", "toString", "", "analytics"})
public final class NewRateLimitSettings {
    private final boolean enabled;
    private final int concurrentSessions;
    private final long staleOperationSeconds;
    private final int concurrentOperationsPerSession;

    public NewRateLimitSettings(boolean enabled, int concurrentSessions, long staleOperationSeconds, int concurrentOperationsPerSession) {
        this.enabled = enabled;
        this.concurrentSessions = concurrentSessions;
        this.staleOperationSeconds = staleOperationSeconds;
        this.concurrentOperationsPerSession = concurrentOperationsPerSession;
    }

    public final boolean getEnabled() {
        return this.enabled;
    }

    public final int getConcurrentSessions() {
        return this.concurrentSessions;
    }

    public final long getStaleOperationSeconds() {
        return this.staleOperationSeconds;
    }

    public final int getConcurrentOperationsPerSession() {
        return this.concurrentOperationsPerSession;
    }

    public final boolean component1() {
        return this.enabled;
    }

    public final int component2() {
        return this.concurrentSessions;
    }

    public final long component3() {
        return this.staleOperationSeconds;
    }

    public final int component4() {
        return this.concurrentOperationsPerSession;
    }

    @NotNull
    public final NewRateLimitSettings copy(boolean enabled, int concurrentSessions, long staleOperationSeconds, int concurrentOperationsPerSession) {
        return new NewRateLimitSettings(enabled, concurrentSessions, staleOperationSeconds, concurrentOperationsPerSession);
    }

    public static /* synthetic */ NewRateLimitSettings copy$default(NewRateLimitSettings newRateLimitSettings, boolean bl, int n, long l, int n2, int n3, Object object) {
        if ((n3 & 1) != 0) {
            bl = newRateLimitSettings.enabled;
        }
        if ((n3 & 2) != 0) {
            n = newRateLimitSettings.concurrentSessions;
        }
        if ((n3 & 4) != 0) {
            l = newRateLimitSettings.staleOperationSeconds;
        }
        if ((n3 & 8) != 0) {
            n2 = newRateLimitSettings.concurrentOperationsPerSession;
        }
        return newRateLimitSettings.copy(bl, n, l, n2);
    }

    @NotNull
    public String toString() {
        return "NewRateLimitSettings(enabled=" + this.enabled + ", concurrentSessions=" + this.concurrentSessions + ", staleOperationSeconds=" + this.staleOperationSeconds + ", concurrentOperationsPerSession=" + this.concurrentOperationsPerSession + ')';
    }

    public int hashCode() {
        int n = this.enabled ? 1 : 0;
        if (n != 0) {
            n = 1;
        }
        int result = n;
        result = result * 31 + Integer.hashCode(this.concurrentSessions);
        result = result * 31 + Long.hashCode(this.staleOperationSeconds);
        result = result * 31 + Integer.hashCode(this.concurrentOperationsPerSession);
        return result;
    }

    public boolean equals(@Nullable Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof NewRateLimitSettings)) {
            return false;
        }
        NewRateLimitSettings newRateLimitSettings = (NewRateLimitSettings)other;
        if (this.enabled != newRateLimitSettings.enabled) {
            return false;
        }
        if (this.concurrentSessions != newRateLimitSettings.concurrentSessions) {
            return false;
        }
        if (this.staleOperationSeconds != newRateLimitSettings.staleOperationSeconds) {
            return false;
        }
        return this.concurrentOperationsPerSession == newRateLimitSettings.concurrentOperationsPerSession;
    }
}

