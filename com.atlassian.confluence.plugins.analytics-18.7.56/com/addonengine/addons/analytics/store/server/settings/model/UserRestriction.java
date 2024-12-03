/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  kotlin.Metadata
 *  kotlin.jvm.internal.Intrinsics
 *  org.codehaus.jackson.annotate.JsonProperty
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package com.addonengine.addons.analytics.store.server.settings.model;

import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import org.codehaus.jackson.annotate.JsonProperty;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(mv={1, 9, 0}, k=1, xi=48, d1={"\u0000 \n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u000b\n\u0002\u0010\b\n\u0002\b\u0002\b\u0086\b\u0018\u00002\u00020\u0001B\u0019\u0012\b\b\u0001\u0010\u0002\u001a\u00020\u0003\u0012\b\b\u0001\u0010\u0004\u001a\u00020\u0005\u00a2\u0006\u0002\u0010\u0006J\t\u0010\u000b\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\f\u001a\u00020\u0005H\u00c6\u0003J\u001d\u0010\r\u001a\u00020\u00002\b\b\u0003\u0010\u0002\u001a\u00020\u00032\b\b\u0003\u0010\u0004\u001a\u00020\u0005H\u00c6\u0001J\u0013\u0010\u000e\u001a\u00020\u00052\b\u0010\u000f\u001a\u0004\u0018\u00010\u0001H\u00d6\u0003J\t\u0010\u0010\u001a\u00020\u0011H\u00d6\u0001J\t\u0010\u0012\u001a\u00020\u0003H\u00d6\u0001R\u0011\u0010\u0004\u001a\u00020\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0007\u0010\bR\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\t\u0010\n\u00a8\u0006\u0013"}, d2={"Lcom/addonengine/addons/analytics/store/server/settings/model/UserRestriction;", "", "userKey", "", "useAnalytics", "", "(Ljava/lang/String;Z)V", "getUseAnalytics", "()Z", "getUserKey", "()Ljava/lang/String;", "component1", "component2", "copy", "equals", "other", "hashCode", "", "toString", "analytics"})
public final class UserRestriction {
    @NotNull
    private final String userKey;
    private final boolean useAnalytics;

    public UserRestriction(@JsonProperty(value="userKey") @NotNull String userKey, @JsonProperty(value="useAnalytics") boolean useAnalytics) {
        Intrinsics.checkNotNullParameter((Object)userKey, (String)"userKey");
        this.userKey = userKey;
        this.useAnalytics = useAnalytics;
    }

    @NotNull
    public final String getUserKey() {
        return this.userKey;
    }

    public final boolean getUseAnalytics() {
        return this.useAnalytics;
    }

    @NotNull
    public final String component1() {
        return this.userKey;
    }

    public final boolean component2() {
        return this.useAnalytics;
    }

    @NotNull
    public final UserRestriction copy(@JsonProperty(value="userKey") @NotNull String userKey, @JsonProperty(value="useAnalytics") boolean useAnalytics) {
        Intrinsics.checkNotNullParameter((Object)userKey, (String)"userKey");
        return new UserRestriction(userKey, useAnalytics);
    }

    public static /* synthetic */ UserRestriction copy$default(UserRestriction userRestriction, String string, boolean bl, int n, Object object) {
        if ((n & 1) != 0) {
            string = userRestriction.userKey;
        }
        if ((n & 2) != 0) {
            bl = userRestriction.useAnalytics;
        }
        return userRestriction.copy(string, bl);
    }

    @NotNull
    public String toString() {
        return "UserRestriction(userKey=" + this.userKey + ", useAnalytics=" + this.useAnalytics + ')';
    }

    public int hashCode() {
        int result = this.userKey.hashCode();
        int n = this.useAnalytics ? 1 : 0;
        if (n != 0) {
            n = 1;
        }
        result = result * 31 + n;
        return result;
    }

    public boolean equals(@Nullable Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof UserRestriction)) {
            return false;
        }
        UserRestriction userRestriction = (UserRestriction)other;
        if (!Intrinsics.areEqual((Object)this.userKey, (Object)userRestriction.userKey)) {
            return false;
        }
        return this.useAnalytics == userRestriction.useAnalytics;
    }
}

